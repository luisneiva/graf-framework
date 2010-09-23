package model.graphTransformer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import controller.Properties;

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
import agg.xt_basis.CompletionPropertyBits;
import agg.xt_basis.Completion_CSP;
import agg.xt_basis.GraGra;
import agg.xt_basis.Graph;
import agg.xt_basis.Match;
import agg.xt_basis.MorphCompletionStrategy;
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
 * @author Kevin O'Shea
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
		Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
		if(printDebug) {
			System.out.println("Action name is " + actionName);
		}
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
						if(printDebug) {
							System.out.println("Rule Cancelled: object="+objName+
									", action="+child.getChildElements().get(j).getValue()+
									", actionParam="+actionParam + " (" + e.getMessage() + ")");
						}
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

		long time = System.currentTimeMillis();
		
		//System.out.println("[Trying to apply rule for " + actionName + "]");
		Rule rule = gg.getRule(actionName);
		if (rule==null)
			throw new RuleException("Could not find rule for " + actionName + " in ggx");

		setInputParameters(rule, objName, actionParam, true);

		// If the input parameters are set in the wrong order
		// then reverse the order and try again
		try {
			apply(rule);	//throws RuleNoMatchException
			// I'm assuming apply() will never partly execute. Therefore we don't have to go back to an old copy
		}
		catch(RuleNoMatchException rnme) {
			rule.unsetInputParameter(true);
			setInputParameters(rule, objName, actionParam, false);
			apply(rule);
		}

		Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
		if(printDebug) {
			System.out.println("Rule Applied: object="+objName+", action="
					+actionName+", actionParam="+actionParam);
		}
		
		long time1 = System.currentTimeMillis();
		long totalTime = time1 - time;
		System.out.println("total rule application time = " + totalTime);
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

		rule.setInputParameters(parameters);
	}

	/**
	 * Used for debugging
	 * Deleting specific edges from a rule
	 * can be a useful technique to work out why a rule
	 * won't fit.
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
					Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
					if(printDebug) {
						System.out.println(ListGraph.getName(node) + " " + arcsNum);
					}
				}
				catch(NullPointerException npe)
				{
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

		// graTraOptions contains a string called "Injective"
		// we want to remove this string but none of the code
		// below will achieve this
		/*Vector<String> options = gg.getGraTraOptions();
		
		System.out.println(">o>" + options.size());
		
		
		Iterator<String> optionIterator = options.iterator();
		while(optionIterator.hasNext()) {
			String next = optionIterator.next();
			if(next.equals("injective")) {
				optionIterator.remove();
			}
		}
		
		MorphCompletionStrategy mcs = new Completion_CSP(false);
		BitSet properties = new BitSet();
		properties.set(CompletionPropertyBits.DANGLING);
		properties.set(CompletionPropertyBits.INJECTIVE, false);
		mcs.showProperties();
		mcs.removeProperty("Injective");
		System.out.println("allowed: "  +mcs.getSupportedProperties());
		gg.setGraTraOptions(mcs);

		Vector<String> newOptions = gg.getGraTraOptions();
		System.out.println(">>" + newOptions.size());
		for(String option : newOptions)
			System.out.println(">"+  option);*/
		
		
		Match match = gg.createMatch(rule);

		if (match.nextCompletion()) {
			if (match.isValid()) {
				Step step = new Step();
				try {
					Morphism comatch = step.execute(match);
					//	System.out.println("RULE APPLIED (" +
					//			match.getRule().getName() + ")");
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

		ListGraph.giveAddedNodesProperNames(gg.getGraph());	
	}

	/** Outputs dot code for each rule in the GraGra
	 *  Green = to add, blue = to delete, red = NAC. 
	 * @param outputPath The path of the output folder
	 * @throws IOException 
	 */
	public void outputRulesAsDot(String outputPath) throws IOException {
		Enumeration<Rule> listOfRules = gg.getRules();
		String dot;
		Rule rule;
		ListGraph leftGraph, rightGraph, condition;
		Enumeration<OrdinaryMorphism> nac;
		String filepath;

		/* Iterate through the available rules */
		while (listOfRules.hasMoreElements()) {
			rule = listOfRules.nextElement();
			leftGraph = new ListGraph(rule.getLeft());
			rightGraph = new ListGraph(rule.getRight());
			nac = rule.getNACs();
			filepath = outputPath;

			dot = "digraph " + rule.getName().replace('-', '_') + " {\n";
			/* Get the RHS (precondition) of the rule */
			for (Arc edge : rightGraph.getArcsList()) {
				/* Add the edge, with it's source and target nodes, to the .dot string */
				dot += "\t\"" + ListGraph.getName(edge.getSource()) + "\" -> \"" + ListGraph.getName(edge.getTarget()) +
				"\" [label=\"" + ListGraph.getName(edge) + "\"";
				/* New edges are coloured green */
				if (!leftGraph.containsThisEdge(edge)) {
					dot += ", color=\"green\"";
				}
				dot += "]\n";
				/* New nodes are coloured green */
				if (!leftGraph.containsNode(ListGraph.getName(edge.getSource()))) {
					dot += "\t\"" + ListGraph.getName(edge.getSource()) + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
				}
				if (!leftGraph.containsNode(ListGraph.getName(edge.getTarget()))) {
					dot += "\t\"" + ListGraph.getName(edge.getTarget()) + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
				}
			}
			/* Check for and add deleted edges */
			for (Arc edge : leftGraph.getArcsList()) {
				if (!rightGraph.containsThisEdge(edge)) {
					dot += "\t\"" + ListGraph.getName(edge.getSource()) + "\" -> \"" + ListGraph.getName(edge.getTarget()) +
					"\" [label=\"" + ListGraph.getName(edge) + "\", color=\"blue\"]\n";
				}
				if (!rightGraph.containsNode(ListGraph.getName(edge.getSource()))) {
					dot += "\t\"" + ListGraph.getName(edge.getSource()) + "\" [color=\"blue\", fontcolor=\"red\"]" + "\n";
				}
				if (!rightGraph.containsNode(ListGraph.getName(edge.getTarget()))) {
					dot += "\t\"" + ListGraph.getName(edge.getTarget()) + "\" [color=\"blue\", fontcolor=\"red\"]" + "\n";
				}
			}
			/* Add NACs (edges only) */
			while(nac.hasMoreElements()) {
				condition = new ListGraph(nac.nextElement().getImage());
				for (Arc edge : condition.getArcsList()) {
					dot += "\t\"" + ListGraph.getName(edge.getSource()) + "\" -> \"" + ListGraph.getName(edge.getTarget()) +
					"\" [label=\"" + ListGraph.getName(edge) + "\", color=\"red\"]\n";
				}
			}
			dot += "}\n";
			/* Write complete rule to file */
			try {
				filepath += rule.getName() + ".dot";
				BufferedWriter out = new BufferedWriter(new FileWriter(filepath));
				out.write(dot);
				out.close();
				Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
				if(printDebug) {
					System.out.println("Dot code written: " + filepath);
				}
			} catch (Exception e) {
				throw new IOException("Error writing dot file: " + e.getMessage());
			}

		}
	}
}
