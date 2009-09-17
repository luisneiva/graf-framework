package model.graphTransformer;

import java.net.URL;

import model.ListGraph;
import model.exceptions.RuleException;
import model.exceptions.RuleNoMatchException;
import model.modelTransformer.objectDisplay.DisplayObject;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import agg.xt_basis.Graph;
import agg.xt_basis.Node;
import agg.xt_basis.Rule;


public class GraphTransformer {

	private ListGraph graph;

	/** Document containing GTS rule definitions */
	private Document gtsRules;

	public GraphTransformer(URL gtsRulesPath) {
		try {
			Builder builder = new Builder();
			gtsRules = builder.build(gtsRulesPath.openStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Assign a graph to be used in the transformations */
	public void setGraph(ListGraph graph) {
		this.graph = graph;
	}

	/** Apply a GTS rule to the graph. */
	public ListGraph transition(DisplayObject obj, String actionName, String actionParam) throws RuleException {

		// Find rule for actionName in gtsRules
		Element gtsRulesRoot = gtsRules.getRootElement();
		Nodes rulenodes = gtsRulesRoot.query("rule[@name=\"" + actionName + "\"]");
		if (rulenodes.size()==0) throw new RuleException("Could not find rule for " + actionName);
		else if (rulenodes.size()>1) throw new RuleException("Duplicate rule for " + actionName);
		Element ruleElem = (Element)rulenodes.get(0);

		// If composite rule, apply dependency rules first
		String composite = ruleElem.getAttributeValue("composite");
		if (composite != null && composite.equals("true")) {
			for (int i = 0; i < ruleElem.getChildElements().size(); i++) {
				Element depends = ruleElem.getChildElements().get(i);

				if (depends.getLocalName().equals("dependency")) {
					graph = transition(obj, depends.getValue(), actionParam);
				} else if (depends.getLocalName().equals("exclusive-dependencies")) {
					ListGraph graphBackup = new ListGraph(graph);
					boolean foundmatch = false;
					for (int j = 0; j < depends.getChildElements().size(); j++) {
						try {
							Element exdepends = depends.getChildElements().get(j);
							graph = transition(obj, exdepends.getValue(), actionParam);
							//if successful transition then stop - only apply one dependency
							foundmatch = true;
							break;
						} catch (RuleNoMatchException e) {
							//if the exclusive dependency did not fully complete, erase its work.
							graph = graphBackup;
							//System.out.println("GTS Rule Cancelled: object="+obj.getName()+
							//		", action="+depends.getChildElements().get(j).getValue()+
							//		", actionParam="+actionParam);
						}
					}
					if (!foundmatch) throw new RuleException("Could not complete " +
							"any of the exclusive dependencies of " + actionName);
				}
				else if(depends.getLocalName().equals("looping-dependency")) {

					//int n = 0;
					doloop : while(true)
					{
						try {
							for (int j = 0; j < depends.getChildElements().size(); j++) { // usually there'll only be 1 child element
								Element exdepends;
								exdepends = depends.getChildElements().get(j);
								graph = transition(obj, exdepends.getValue(), actionParam);
								//n++;
							}
						} catch(RuleNoMatchException e) {
							//System.out.println("applied the rule " + n + " times");
							break doloop;
						}
					}
				}
			}
		}

		Rule rule = parseRule(ruleElem);

		//rule may be null if no actual rule (eg a composite rule)
		if (rule != null) {

			System.out.println("error: GraphTransformer is being used. Use AGGTransformer instead");
			
			// Read <parameter> elements of rule to determine assignments to make
/*			PatternMatch assignments = new PatternMatch();
			Nodes parameternames = ruleElem.query("parameters/parameter/@name");
			if (parameternames.size()>0) {
				String parametername = parameternames.get(0).getValue();
				NodeVariableAssignment assign
				= new NodeVariableAssignment(new Node(parametername), obj.getGraphNode());
				assignments.add(assign);
			}
			if (parameternames.size()>1) {
				String parametername = parameternames.get(1).getValue();
				NodeVariableAssignment assign
				= new NodeVariableAssignment(new Node(parametername), new Node(actionParam));
				assignments.add(assign);
			}

			PatternMatch fit = rule.generatePatternMacth(graph, assignments);
			if(fit == null) {
				throw new RuleNoMatchException("Rule for " + actionName + " did not match");
			}
			rule.applyPatternMatch(fit);
			rule.transform(graph);
*/
			System.out.println("GTS Rule Applied: object="+obj.getName()+", action="+actionName+", " +
					"actionParam="+actionParam);
		}

		return new ListGraph(graph);
	}

	/**
	 * Parse a given xml &ltrule&gt element into a GTS Rule object.
	 * 
	 * @param actionName The name of the rule to look up in the xml file
	 * @return The rule loaded from the xml file, or null if there was no before/after
	 * 
	 * @author Kevin
	 */
	private Rule parseRule(Element rule) {
		ListGraph before = new ListGraph();
		ListGraph after = new ListGraph();
		ListGraph forbidden = new ListGraph();
		
		//AGG agg = new AGG();
		//Rule newRule = agg.getGraGra().createRule();

		// Extract before, after and forbidden graphs from rule
		if (rule.query("before").size()==1) {
			before = loadRuleGraph((Element)rule.query("before").get(0));
		} else return null;
		if (rule.query("after").size()==1) {
			after = loadRuleGraph((Element)rule.query("after").get(0));
		} else return null;
		if (rule.query("forbidden").size()==1) {
			forbidden = loadRuleGraph((Element)rule.query("forbidden").get(0));
		}

		System.out.println("error: GraphTransformer is being used. Use AGGTransformer instead.");
		return null;
		//return new Rule(before, after, forbidden);
	}

	/**
	 * Constructs a graph.
	 * 
	 * @param element The element that is the parent of all the edges in the graph.
	 * 
	 */
	private ListGraph loadRuleGraph(Element element) {

		ListGraph graph = new ListGraph();

		Elements children = element.getChildElements();
		for(int i = 0; i < children.size(); i++) {
			String from = children.get(i).getAttributeValue("from");
			String label = children.get(i).getAttributeValue("label");
			String to = children.get(i).getAttributeValue("to");
			graph.addEdge(from, label, to);		
		}

		return graph;
	}

}
