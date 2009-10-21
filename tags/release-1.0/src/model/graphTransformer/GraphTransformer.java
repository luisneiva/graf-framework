package model.graphTransformer;

import java.net.URL;
import java.util.ArrayList;

import model.Graph;
import model.Node;
import model.exceptions.RuleException;
import model.exceptions.RuleNoMatchException;
import model.objectDiagram.ODObject;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;


public class GraphTransformer {

	private Graph graph;

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
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/** Apply a GTS rule to the graph.
	 * For example: obj.getName()="m1", actionName="actorSendSignal", actionParam="cook" */
	public Graph transition(ODObject obj, String actionName, String actionParam) throws RuleException {
		
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
                	boolean foundmatch = false;
                	for (int j = 0; j < depends.getChildElements().size(); j++) {
                		try {
                			Element exdepends = depends.getChildElements().get(j);
                			graph = transition(obj, exdepends.getValue(), actionParam);
                			//if successful transition then stop - only apply one dependency
                			foundmatch = true;
                			break;
                		} catch (RuleNoMatchException e) {
                			//(ignore non-matching rules)
                		}
                	}
                	if (!foundmatch) throw new RuleException("Did not find rule match for " +
                			"any of the exclusive dependencies of " + actionName);
                }
            }
        }
        
        Rule rule = parseRule(ruleElem);
        
        //rule may be null if no actual rule (eg a composite rule)
        if (rule != null) {
        	
        	// Read <parameter> elements of rule to determine assignments to make
        	PatternMatch assignments = new PatternMatch();
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
			
			System.out.println("GTS Rule Applied: object="+obj.getName()+", action="+actionName+", " +
					"actionParam="+actionParam);
        }
        
		return new Graph(graph);
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
		Graph before = new Graph();
		Graph after = new Graph();
		Graph forbidden = new Graph();
		
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
		
		return new Rule(before, after, forbidden);
	}

	/**
	 * Constructs a graph.
	 * 
	 * @param element The element that is the parent of all the edges in the graph.
	 * 
	 */
	private Graph loadRuleGraph(Element element) {

		Graph graph = new Graph();

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
