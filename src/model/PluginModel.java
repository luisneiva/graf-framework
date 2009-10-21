package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;

import model.exceptions.ModelToGraphException;
import model.graphTransformer.GraphTransformer;
import model.modelTransformer.GraphToModel;
import model.modelTransformer.GraphToModelFactory;
import model.modelTransformer.ModelToGraph;
import model.modelTransformer.ModelToGraphFactory;
import model.modelTransformer.objectDisplay.DisplayObject;
import model.modelTransformer.objectDisplay.ObjectDisplay;

/**
 * 
 * @author Kevin O'Shea
 * @author Oscar Wood
 */
public class PluginModel {
	
	private ModelToGraph modeltograph;
	private GraphTransformer graphTransformer;
	private GraphToModel graphtomodel;
	
	/** The graph (list of edges) */
	private Graph graph;
	
	/** Number of actions that can be undone at a time */
	private final int undoLimit = 10;
	/** Number of undos that can be currently be performed */
	private int undosRemaining = 0;
	/** The initial graph constructed */
	private Graph origGraph;
	/** A list of the graphs from previous transitions */
	private ArrayList<Graph> graphs = new ArrayList<Graph>(undoLimit);
	
	/** URL to the folder for placing graph outputs */
	private final String graphOutputsPath;
	
	/** Reference to data prepared for displaying in view */
	private ObjectDisplay objectdisplay;
	
	/** Is the system in debug mode - in debug mode, graph states are output (as dot) at every transition */
	private final boolean debugmode;
	/** Keeps track of the number of transitions performed (used in names for output dot files) */
	private Integer transitionnumber = 0;
	
	public ObjectDisplay getObjectDisplay() {
		return objectdisplay;
	}
	
	public PluginModel(URL gtsRulesURL, boolean debugmode, String graphOutputsPath) {
		System.out.println("Model is alive");
		
		this.debugmode = debugmode;
		if (!graphOutputsPath.endsWith("/")) graphOutputsPath = graphOutputsPath + "/";
		this.graphOutputsPath = graphOutputsPath;
		
		modeltograph = ModelToGraphFactory.createModelToGraph();
		graphtomodel = GraphToModelFactory.createGraphToModel();
		graphTransformer = new GraphTransformer(gtsRulesURL);
	}
	
	/** Reset model for new animation */
	public void reset() {
		transitionnumber = 0;
		undosRemaining = 0;
		graphs.clear();
		graphtomodel.reset();
		if (origGraph != null) {
			graph = new Graph(origGraph);
			graphTransformer.setGraph(graph);
			objectdisplay = graphtomodel.generateDisplayObjects(graph);
		}
	}
	
	/**
	 * Build the graph
	 * @param modelpath Filepath to the model file
	 * @param instancepath Filepath to the instance definition
	 */
	public void buildGraph(URL modelURL, URL instanceURL) throws ModelToGraphException {
		graph = modeltograph.buildGraph(modelURL, instanceURL);
		graphTransformer.setGraph(graph);
		origGraph = new Graph(graph);
		
		//if system is in debug mode, output graph as dot file
		if (debugmode) outputdot(graph, graph);
		
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
		graphs.add(new Graph(graph));
		undosRemaining = graphs.size();
		
		//Extract transition parameters
		DisplayObject obj = transAction.getObject();
		String actionName = transAction.getActionName();
		String actionParam = transAction.getActionParam();

		graph = graphTransformer.transition(obj, actionName, actionParam);
		
		transitionnumber++;
		if (debugmode) outputdot(graph, graphs.get(graphs.size()-1));
		
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
	public void undoAction() {
		if (getUndosRemaining()==0) return;
		Graph tmp = new Graph(graph);
		graph = new Graph(graphs.get(undosRemaining-1));
		graphs.set(undosRemaining-1, tmp);
		graphTransformer.setGraph(graph);
		undosRemaining--;
		transitionnumber--;
		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}
	/** Redo the last transition that occured */
	public void redoAction() {
		if (getRedosRemaining()==0) return;
		Graph tmp = new Graph(graph);
		graph = new Graph(graphs.get(undosRemaining));
		graphs.set(undosRemaining, tmp);
		graphTransformer.setGraph(graph);
		undosRemaining++;
		transitionnumber++;
		objectdisplay = graphtomodel.generateDisplayObjects(graph);
	}
	
	/** Outputs dot code for newgraph and colours edges in comparison with oldGraph.
	 * Green = edge added, Red = edge deleted (note that red edges are NOT in the new graph!) */
	private void outputdot(Graph newgraph, Graph oldgraph) {
		String res = "digraph umlMetamodel {\n";
		for (Edge edge : newgraph) {
			res += "\t\"" + edge.getFromName() + "\" -> \"" + edge.getToName() +
				"\" [label=\"" + edge.getLabel() + "\"";
			if (!oldgraph.containsEdge(edge)) {
				res += ", color=\"green\"";
			} else if (edge.getLabel().equals("i")) {
				res += ", color=\"lightgrey\"";
			} else {
				res += ", color=\"grey\"";
			}
			res += "]\n";
			//new nodes are coloured green
			if (!oldgraph.containsNode(edge.getFromName())) {
				res += "\t\"" + edge.getFromName() + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
			}
			if (!oldgraph.containsNode(edge.getToName())) {
				res += "\t\"" + edge.getToName() + "\" [color=\"green\", fontcolor=\"green\"]" + "\n";
			}
		}
		//check for (and add red) deleted edges
		for (Edge edge : oldgraph) {
			if (!newgraph.containsEdge(edge)) {
				res += "\t\"" + edge.getFromName() + "\" -> \"" + edge.getToName() +
				"\" [label=\"" + edge.getLabel() + "\", color=\"red\"]\n";
			}
			//deleted nodes are coloured red
			if (!newgraph.containsNode(edge.getFromName())) {
				res += "\t\"" + edge.getFromName() + "\" [color=\"red\", fontcolor=\"red\"]" + "\n";
			}
			if (!newgraph.containsNode(edge.getToName())) {
				res += "\t\"" + edge.getToName() + "\" [color=\"red\", fontcolor=\"red\"]" + "\n";
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
			System.err.println("Error writing dot file: " + e.getMessage());
		}
	}
}
