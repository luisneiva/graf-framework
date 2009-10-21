package model.graphTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import model.AGGAssignment;
import model.ListGraph;
import model.exceptions.RuleException;
import model.exceptions.RuleNoMatchException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import agg.attribute.impl.VarMember;
import agg.xt_basis.Arc;
import agg.xt_basis.GraGra;
import agg.xt_basis.Graph;
import agg.xt_basis.Match;
import agg.xt_basis.Morphism;
import agg.xt_basis.Node;
import agg.xt_basis.OrdinaryMorphism;
import agg.xt_basis.Rule;
import agg.xt_basis.Step;
import agg.xt_basis.TypeException;

/**
 * Applies the AGG methods to transform a graph.
 *
 * @author Oscar Wood
 */
public class AGGTransformer {

	private GraGra gg;

	/** Holds sequences of rules to apply for corresponding action names */ 
	private Document gtsRulesSeq;

	public AGGTransformer(String gtsRulesPath, String gtsRulesSeqPath) throws Exception {

		gg = new GraGra(false);
		gg.load(gtsRulesPath);
		gtsRulesSeq = new Builder().build(gtsRulesSeqPath);
	}

	/** Assign graph to be used in the transformations */
	public void setGraph(Graph graph) {
		gg.resetGraph(graph);
	}

	/** Determine what GTS rule or sequence to apply and apply it. */
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
				transition(objName, child.getValue(), actionParam);
			} else if (child.getLocalName().equals("exclusive-dependencies")) {
				Graph graphBackup = new ListGraph(gg.getGraph());
				boolean foundmatch = false;
				for (int j = 0; j < child.getChildElements().size(); j++) {
					try {
						Element exdepends = child.getChildElements().get(j);
						if (exdepends.getLocalName().equals("rule")) {
							applyRule(objName, exdepends.getValue(), actionParam);
						} else if (exdepends.getLocalName().equals("action")) {
							transition(objName, exdepends.getValue(), actionParam);
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
						System.out.println("GTS Rule Cancelled: object="+objName+
								", action="+child.getChildElements().get(j).getValue()+
								", actionParam="+actionParam + " (" + e.getMessage() + ")");
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
								transition(objName, loopdepends.getValue(), actionParam);
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

		return gg.getGraph();
	}

	/**
	 * Sets the input parameters for a rule and applies it.
	 * 
	 * @param objName In UML, the name of the object
	 * @param actionName The name of the rule
	 * @param actionParam Varies on the rule
	 */
	private void applyRule(String objName, String actionName, String actionParam)
	throws RuleException {

		System.out.println("[Trying to apply rule for " + actionName + "]");
		Rule rule = gg.getRule(actionName);
		if (rule==null)
			throw new RuleException("Could not find rule for " + actionName + " in ggx");

		setInputParameters(rule, objName, actionParam, true);

		// I'm assuming apply() will never partly execute. Therefore we don't have to go back to an old copy
		try {
			apply(rule);	//throws RuleNoMatchException
			//System.out.println("object then action worked.");
		}
		catch(RuleNoMatchException rnme) {
			//System.out.println("object then action didn't work.");
			rule.unsetInputParameter(true);
			setInputParameters(rule, objName, actionParam, false);
			apply(rule);
			//System.out.println("action then object worked.");
		}

		System.out.println("GTS Rule Applied: object="+objName+", action="
				+actionName+", actionParam="+actionParam);
	}

	/**
	 * Calls the AGG code to set input parameters for a rule
	 * 
	 * @param object In UML, the name of the object
	 * @param actionParam Depends on the rule
	 * @param inOrder Whether to apply the object to the first parameter slot 
	 * and actionParam to the second, or vice versa
	 */
	private void setInputParameters(Rule rule,String object, String actionParam, Boolean inOrder) {

		//debugDeleteEdges(rule);

		HashMap<String, Vector<Object>> parameters = new HashMap<String, Vector<Object>>();

		Vector<String> suppliedInput = new Vector<String>();

		for(VarMember vm : rule.getInputParameters()) {
			String str = vm.getName();
			suppliedInput.add(str);
		}

		ArrayList<AGGAssignment> result = new ArrayList<AGGAssignment>();

		if(inOrder) {
			if(suppliedInput.size() > 0)
				result.add(new AGGAssignment(suppliedInput.get(0),object));
			if(suppliedInput.size() > 1)
				result.add(new AGGAssignment(suppliedInput.get(1),actionParam));
		}
		else {
			if(suppliedInput.size() > 0)
				result.add(new AGGAssignment(suppliedInput.get(0),actionParam));
			if(suppliedInput.size() > 1)
				result.add(new AGGAssignment(suppliedInput.get(1),object));
		}	


		for(AGGAssignment assign : result) {
			Vector<Object> vector = new Vector<Object>();
			vector.add(assign.cons);
			vector.add("String");
			parameters.put(assign.var, vector);
		}

		//System.out.println(">>>>> " + parameters + " " + parameters.size());

		rule.setInputParameters(parameters);
	}

	/**
	 * Used for testing
	 */
	private void debugDeleteEdges(Rule rule) {

		//System.out.println(rule.getName());
		if(rule.getName().equals("(insert the name of the rule here)"))
		{
			Graph before = rule.getLeft();
			ArrayList<Arc> arcsToDelete = new ArrayList<Arc>();
			ArrayList<Node> nodesToDelete = new ArrayList<Node>();
			for(Arc arc : before.getArcsList())
			{
				String name = ListGraph.getName(arc);
				ArrayList<String> toDelete = new ArrayList<String>();
				
				// add edges to be deleted here
				/*	toDelete.add("source");
				toDelete.add("i");
				toDelete.add("sendEvent");
				toDelete.add("signal");
				toDelete.add("message");
				toDelete.add("receiveEvent");
				toDelete.add("receiver");
				toDelete.add("message");
				toDelete.add("trigger");
				toDelete.add("target");
				toDelete.add("activeState");
				toDelete.add("activity");
				toDelete.add("receiveSignal");
				toDelete.add("event");
				toDelete.add("host");
				toDelete.add("entry");
				toDelete.add("behavior");
				toDelete.add("execution");*/
				
				if(toDelete.contains(name))
				{
					arcsToDelete.add(arc);
					Node src = (Node)arc.getSource();
					Node tar = (Node)arc.getTarget();

					if(!nodesToDelete.contains(src))
						nodesToDelete.add(src);
					if(!nodesToDelete.contains(tar))
						nodesToDelete.add(tar);
				}
			}

			for(Arc arc : arcsToDelete)
			{
				try
				{
					before.destroyArc(arc,true);
				}
				catch(TypeException te)
				{
					System.out.println("couldn't delete");
					te.printStackTrace();
				}
			}
			for(Node node : nodesToDelete)
			{
				Integer arcsNum = node.getNumberOfArcs();
				try
				{
					System.out.println(ListGraph.getName(node) + " " + arcsNum);
				}
				catch(NullPointerException npe)
				{
					System.out.println("npe");
					continue;
				}
				if(arcsNum == 0)
				{
					try
					{
						before.destroyNode(node,true);
					}
					catch(TypeException te)
					{
						System.out.println("couldn't delete");
						te.printStackTrace();
					}
				}
			}

			System.out.println(before.getArcsCount());
			System.out.println(before.getNodesCount());
			for(Node node : before.getNodesList())
				System.out.println(ListGraph.getName(node));
		}
	}

	/**
	 * Having set input parameters and determined the appropriate
	 * sequences, loops etc, this actually applies the rule.
	 */
	private void apply(Rule rule) throws RuleNoMatchException {

		Match match = gg.createMatch(rule);

		//System.out.println("before: "+new ListGraph(rule.getLeft()));
		//try {System.out.println("nac: "+rule.getNACsList().get(0)); } catch(Exception e) {}
		//System.out.println("after: "+new ListGraph(rule.getRight()));
		//System.out.println("GG:" + gg.getGraph());

		//match.setCompletionStrategy(CompletionStrategySelector.getDefault(), true);

		/*System.out.println("Match attribute context:");  
		AttrContext ac = match.getAttrContext();
		for (int i = 0; i < ac.getVariables().getNumberOfEntries(); i++) {
		   AttrInstanceMember am = (AttrInstanceMember) ac.getVariables().getMemberAt(i);
		    System.out.println("Variable "+i+": name="+am.getName()+" value="+am.getExprAsText());
		}*/

		if (match.nextCompletion()) {
			if (match.isValid()) {
				Step step = new Step();
				try {
					Morphism comatch = step.execute(match);
					System.out.println("RULE APPLIED (" +
							match.getRule().getName() + ")");
					((OrdinaryMorphism) comatch).dispose();
				} catch (TypeException ex) {
					//System.out.println("1"+match.getErrorMsg());
					throw new RuleNoMatchException("Rule " + rule.getName()
							+ " match failed");
				}
			} else {
				//System.out.println("2"+match.getErrorMsg());
				throw new RuleNoMatchException("Rule " + rule.getName()
						+ " match is not valid");
			}
		} else {
			//System.out.println("3"+match.getErrorMsg());
			throw new RuleNoMatchException("Rule " + rule.getName()
					+ " did not match");
		}

		ListGraph.giveAddedNodesUniqueNames(gg.getGraph());
		/*gg.getGraph().setChanged();
		
		ListGraph newGraph = new ListGraph(gg.getGraph());
		setGraph(newGraph);*/
	}
}
