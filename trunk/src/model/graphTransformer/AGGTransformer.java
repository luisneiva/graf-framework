package model.graphTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import model.AGGAssignment;
import model.ListGraph;
import model.MyGraTraEventListener;
import model.exceptions.RuleException;
import model.exceptions.RuleNoMatchException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import agg.attribute.impl.VarMember;
import agg.xt_basis.CompletionStrategySelector;
import agg.xt_basis.DefaultGraTraImpl;
import agg.xt_basis.GraGra;
import agg.xt_basis.GraTra;
import agg.xt_basis.GraTraEventListener;
import agg.xt_basis.Graph;
import agg.xt_basis.LayeredGraTraImpl;
import agg.xt_basis.Match;
import agg.xt_basis.MorphCompletionStrategy;
import agg.xt_basis.PriorityGraTraImpl;
import agg.xt_basis.Rule;
import agg.xt_basis.RuleSequencesGraTraImpl;

public class AGGTransformer {

	private GraTra gratra;
	private GraGra gg;

	/** Holds sequences of rules to apply for corresponding action names */ 
	private Document gtsRulesSeq;

	public AGGTransformer(String gtsRulesPath, String gtsRulesSeqPath) throws Exception {
		gratra = new DefaultGraTraImpl();
	//	gratra = new RuleSequencesGraTraImpl();
	//	gratra = new PriorityGraTraImpl();
	//	gratra = new LayeredGraTraImpl();
		
		gg = new GraGra(false);
		gg.load(gtsRulesPath);
		gratra.setGraGra(gg);
		gtsRulesSeq = new Builder().build(gtsRulesSeqPath);
	}

	/** Assign graph to be used in the transformations */
	public void setGraph(Graph graph) {
		gratra.setHostGraph(graph);
	}

	/** Apply a GTS rule to the graph. */
	public Graph transition(String objName, String actionName, String actionParam)
	throws RuleException {
		System.out.println("Action name is " + actionName);
		Element gtsRulesSeqRoot = gtsRulesSeq.getRootElement();
		Nodes actionnodes = gtsRulesSeqRoot.query("action[@name=\"" + actionName + "\"]");
		if (actionnodes.size()==0)
			throw new RuleException("Could not find definition for " + actionName + " in xml");
		else if (actionnodes.size()>1)
			throw new RuleException("Duplicate definition for " + actionName + " in xml");
		Element actionElem = (Element)actionnodes.get(0);

		for (int i = 0; i < actionElem.getChildElements().size(); i++) {
			Element child = actionElem.getChildElements().get(i);
			if (child.getLocalName().equals("rule")) {
				applyRule(objName, child.getValue(), actionParam);
			} else if (child.getLocalName().equals("action")) {
				setGraph(transition(objName, child.getValue(), actionParam));
			} else if (child.getLocalName().equals("exclusive-dependencies")) {
				Graph graphBackup = new ListGraph(gratra.getHostGraph());
				boolean foundmatch = false;
				for (int j = 0; j < child.getChildElements().size(); j++) {
					try {
						Element exdepends = child.getChildElements().get(j);
						if (exdepends.getLocalName().equals("rule")) {
							applyRule(objName, exdepends.getValue(), actionParam);
						} else if (exdepends.getLocalName().equals("action")) {
							setGraph(transition(objName, exdepends.getValue(), actionParam));
						} else {
							throw new RuleException("Invalid xml file. Encountered: "
									+ exdepends.getLocalName());
						}
						//if successful transition then stop - only apply one dependency
						foundmatch = true;
						break;
					} catch (RuleNoMatchException e) {
						//if the exclusive dependency did not fully complete, erase its work.
						setGraph(graphBackup);
						//System.out.println("GTS Rule Cancelled: object="+obj.getName()+
						//		", action="+depends.getChildElements().get(j).getValue()+
						//		", actionParam="+actionParam);
					}
				}
				if (!foundmatch) throw new RuleException("Could not complete " +
						"any of the exclusive dependencies of " + actionName);
			} else if(child.getLocalName().equals("looping-dependency")) {
				try {
					while(true){
						for (int j = 0; j < child.getChildElements().size(); j++) {
							Element loopdepends;
							loopdepends = child.getChildElements().get(j);
							if (loopdepends.getLocalName().equals("rule")) {
								applyRule(objName, loopdepends.getValue(), actionParam);
							} else if (loopdepends.getLocalName().equals("action")) {
								setGraph(transition(objName, loopdepends.getValue(), actionParam));
							} else {
								throw new RuleException("Invalid xml file. Encountered "
										+ loopdepends.getLocalName());
							}
						}
					}
				} catch(RuleNoMatchException e) {
					//completed looping dependency
				}
			} else {
				throw new RuleException("Invalid xml file. Encountered " + child.getLocalName());
			}
		}
		return gratra.getHostGraph();
	}

	private void applyRule(String objName, String actionName, String actionParam)
	throws RuleException {
		System.out.println("[Trying to apply rule for " + actionName + "]");
		Rule rule = gg.getRule(actionName);
		if (rule==null)
			throw new RuleException("Could not find rule for " + actionName + " in ggx");
		setInputParameters(rule, objName, actionParam);
		
		//gratra.apply(rule);
		apply(rule);
		
		System.out.println("GTS Rule Applied: object="+objName+", action="
				+actionName+", actionParam="+actionParam);
	}

	private void setInputParameters(Rule rule,String object, String actionParam) {

		HashMap<String, Vector<Object>> parameters = new HashMap<String, Vector<Object>>();

		Vector<String> suppliedInput = new Vector<String>();

		for(VarMember vm : rule.getInputParameters()) {
			String str = vm.getName();
			suppliedInput.add(str);
		}

		ArrayList<AGGAssignment> result = new ArrayList<AGGAssignment>();

		if(suppliedInput.size() > 0)
			result.add(new AGGAssignment(suppliedInput.get(0),object));
		if(suppliedInput.size() > 1)
			result.add(new AGGAssignment(suppliedInput.get(1),actionParam));

		for(AGGAssignment assign : result) {
			Vector<Object> vector = new Vector<Object>();
			vector.add(assign.cons);
			vector.add("String");
			parameters.put(assign.var, vector);
		}

		//System.out.println(">>>>> " + parameters + " " + parameters.size());

		rule.setInputParameters(parameters);
	}

	private void apply(Rule rule) {
		GraTraEventListener l = new MyGraTraEventListener();
		gratra.addGraTraListener(l);

		Match m = gg.createMatch(rule);

		//TODO this doesn't work yet because "[Variable:  c1receipt  is not definite!]"
		if(!m.isReadyToTransform()) {
			System.out.println(m.getErrorMsg());
		}
		else {
			gratra.apply(m);
		}			

	}

	private Boolean applicable(Rule rule) {
		Match m = gg.createMatch(rule);
		//return m.nextCompletion();
		return m.canComplete();
	}
}
