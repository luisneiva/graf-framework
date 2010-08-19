package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import model.exceptions.GraphToModelException;
import model.exceptions.ModelToGraphException;
import model.graphTransformer.AGGTransformer;
import model.modelTransformer.GraphToModel;
import model.modelTransformer.GraphToModelFactory;
import model.modelTransformer.ModelToGraph;
import model.modelTransformer.ModelToGraphFactory;
import model.modelTransformer.objectDisplay.DisplayObject;
import model.modelTransformer.objectDisplay.ObjectDisplay;
import agg.xt_basis.Arc;

/**
 * Facade to model. Holds graph and coordinates model logic.
 * 
 * @author Kevin O'Shea
 * @author Oscar Wood
 */
public class PluginModel {

	/** Provides functionality for model to graph transformation */
	private ModelToGraph modeltograph;
	/** Provides functionality for graph transformations */
	private AGGTransformer graphTransformer;

	/** Provides functionality for graph to model transformation */
	private GraphToModel graphtomodel;

	/** The graph (list of edges) */
	private ListGraph graph;

	/** Number of actions that can be undone at a time */
	private final int undoLimit = 10;
	/** Number of undos that can be currently be performed */
	private int undosRemaining = 0;
	/** The initial graph constructed */
	private ListGraph origGraph;
	/** A list of the graphs from previous transitions */
	private ArrayList<ListGraph> graphs = new ArrayList<ListGraph>(undoLimit);

	/** URL to the folder for placing graph outputs */
	private final String graphOutputsPath;

	/** Reference to data prepared for displaying in view */
	private ObjectDisplay objectdisplay;

	/** Is the system in debug mode - in debug mode, graph states are output (as dot) at every transition */
	private final boolean debugmode;
	/** Keeps track of the number of transitions performed (used in names for output dot files) */
	private Integer transitionnumber = 0;
	private boolean printRule, printGraph;

	public ObjectDisplay getObjectDisplay() {
		return objectdisplay;
	}

	public PluginModel(String gtsRulesPath, String gtsRulesSeqPath,
			boolean debugmode, String graphOutputsPath, boolean isPrintRule, boolean isPrintGraph) throws Exception {
		this.debugmode = debugmode;  // UNNECESSARY  debugMode
		printRule = isPrintRule;   // BROKEN TODO!!!
		printGraph = isPrintGraph;
		if (!graphOutputsPath.endsWith("/")) graphOutputsPath = graphOutputsPath + "/";
		this.graphOutputsPath = graphOutputsPath;

		modeltograph = ModelToGraphFactory.createModelToGraph();
		graphtomodel = GraphToModelFactory.createGraphToModel();
		graphTransformer = new AGGTransformer(gtsRulesPath, gtsRulesSeqPath);

		if (printRule) {
		try {
			graphTransformer.outputRulesAsDot(this.graphOutputsPath);
			
//	 		Uncomment the following to generate rule .dot files on setup & convert them to images
//			String path = "GraphOutputs\\conversionFiles\\dotToImg";
//			String[] command = {"cmd", "/C", "start " + path + " rules"};
//			Process p = Runtime.getRuntime().exec(command);
//			p.waitFor();
		} catch (Exception err) {
			System.out.println("Rule conversion failed!");
			err.printStackTrace();
		}
		}
	}

	/** Reset model for new animation */
	public void reset() throws GraphToModelException {
		transitionnumber = 0;
		undosRemaining = 0;
		graphs.clear();
		graphtomodel.reset();
		if (origGraph != null) {
			graph = new ListGraph(origGraph);
			graphTransformer.setGraph(graph);
			objectdisplay = graphtomodel.generateDisplayObjects(graph);
		}
	}

	/**
	 * Build the graph
	 * @param modelpath Filepath to the model file
	 * @param instancepath Filepath to the instance definition
	 */
	public void buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException, GraphToModelException {
		graph = modeltograph.buildGraph(modelURL, instanceURL);
		graphTransformer.setGraph(graph);
		origGraph = new ListGraph(graph);

		//if system is in debug mode, output graph as dot file
		// printGraph
		if (printRule) try {   //debugmode
			outputdot(graph, graph);
		} catch(IOException e) {
			throw new GraphToModelException(e.getMessage());
		}

		//Construct the object diagram representation of the new graph
		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}
	/** Apply a GTS rule and construct object display of new system state. */
	public void transition(TransitionAction transAction) throws Exception {
		//Remember graph so can undo/redo later
		while (graphs.size() > undosRemaining) {
			graphs.remove(graphs.size()-1);
		}
		if (undosRemaining == undoLimit) {
			graphs.remove(0);
		}
		graphs.add(new ListGraph(graph));
		undosRemaining = graphs.size();

		//Extract transition parameters
		DisplayObject obj = transAction.getObject();
		String actionName = transAction.getActionName();
		String actionParam = transAction.getActionParam();
		graph = (ListGraph)graphTransformer.transition(
				ListGraph.getName(obj.getGraphNode()), actionName, actionParam);
		transitionnumber++;
		if (printGraph) outputdot(graph, graphs.get(graphs.size()-1));  //debugmode

		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}

	/** Returns the number of 'undo's that can be performed */
	public Integer getUndosRemaining() {
		return undosRemaining;
	}
	/** Returns the number of 'redo's that can be performed */
	public Integer getRedosRemaining() {
		return graphs.size() - undosRemaining;
	}
	/** Undo the last transition that occured */
	public void undoAction() throws GraphToModelException {
		if (getUndosRemaining()==0) return;
		ListGraph tmp = new ListGraph(graph);
		graph = new ListGraph(graphs.get(undosRemaining-1));
		graphs.set(undosRemaining-1, tmp);
		graphTransformer.setGraph(graph);
		undosRemaining--;
		transitionnumber--;
		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}
	/** Redo the last transition that occured */
	public void redoAction() throws GraphToModelException {
		if (getRedosRemaining()==0) return;
		ListGraph tmp = new ListGraph(graph);
		graph = new ListGraph(graphs.get(undosRemaining));
		graphs.set(undosRemaining, tmp);
		graphTransformer.setGraph(graph);
		undosRemaining++;
		transitionnumber++;
		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}

	/** Outputs dot code for newgraph and colours edges in comparison with oldGraph.
	 * Green = edge added, Red = edge deleted (note that red edges are NOT in the new graph!) */
	private void outputdot(ListGraph newgraph, ListGraph oldgraph) throws IOException {
		String res = "digraph umlMetamodel {\n";
		for (Arc edge : newgraph.getArcsList()) {
			res += "\t\"" + ListGraph.getName(edge.getSource()) + "\" -> \"" + ListGraph.getName(edge.getTarget()) +
			"\" [label=\"" + ListGraph.getName(edge) + "\"";
			if (!oldgraph.containsEdge(edge)) {
				res += ", color=\"green\"";
			} else if (ListGraph.getName(edge).equals("i")) {
				res += ", color=\"lightgrey\"";
			} else {
				res += ", color=\"grey\"";
			}
			res += "]\n";
			//new nodes are coloured green
			if (!oldgraph.containsNode(ListGraph.getName(edge.getSource()))) {
				res += "\t\"" + ListGraph.getName(edge.getSource()) + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
			}
			if (!oldgraph.containsNode(ListGraph.getName(edge.getTarget()))) {
				res += "\t\"" + ListGraph.getName(edge.getTarget()) + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
			}
		}
		//check for (and add red) deleted edges
		for (Arc edge : oldgraph.getArcsList()) {
			if (!newgraph.containsEdge(edge)) {
				res += "\t\"" + ListGraph.getName(edge.getSource()) + "\" -> \"" + ListGraph.getName(edge.getTarget()) +
				"\" [label=\"" + ListGraph.getName(edge) + "\", color=\"red\"]\n";
			}
			//deleted nodes are coloured red
			if (!newgraph.containsNode(ListGraph.getName(edge.getSource()))) {
				res += "\t\"" + ListGraph.getName(edge.getSource()) + "\" [color=\"red\", fontcolor=\"red\"]" + "\n";
			}
			if (!newgraph.containsNode(ListGraph.getName(edge.getTarget()))) {
				res += "\t\"" + ListGraph.getName(edge.getTarget()) + "\" [color=\"red\", fontcolor=\"red\"]" + "\n";
			}
		}
		res += "}\n";
		try {
			String filepath = graphOutputsPath + "state" + transitionnumber + ".dot";
			BufferedWriter out = new BufferedWriter(new FileWriter(filepath));
			out.write(res);
			out.close();
			System.out.println("Dot code written: " + filepath);
		} catch (Exception e) {
			throw new IOException("Error writing dot file: " + e.getMessage());
		}
	}
}
