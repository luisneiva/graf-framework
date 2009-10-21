package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

import model.graphTransformer.GraphTransformer;
import model.objectDiagram.ODLink;
import model.objectDiagram.ODObject;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * 
 * @author Kevin O'Shea
 * @author Oscar Wood
 */
public class PluginModel {
	
	private UMLToGraph umltograph;
	private GraphTransformer graphTransformer;
	
	/** The graph (list of edges) */
	private Graph graph;
	
	/** Objects in object diagram representation of graph */
	private ArrayList<ODObject> odObjs;
	
	/** Links in object diagram representation of graph */
	private ArrayList<ODLink> odLinks;
	
	/** URL to the normalized metamodel xmi file */
	private final URL metamodelURL;
	
	/** URL to the folder for placing graph outputs */
	private final String graphOutputsPath;
	
	/** Is the system in debug mode - in debug mode, graph states are output (as dot) at every transition */
	private final boolean debugmode;
	/** Keeps track of the number of transitions performed (used in names for output dot files) */
	private Integer transitionnumber = 0;
	
	/**
	 * @param graphOutputsPath  
	 * @throws IOException 
	 * @throws ParsingException 
	 * @throws ValidityException */
	public PluginModel(URL metamodelURL, URL gtsRulesURL, boolean debugmode, String graphOutputsPath)
	throws ValidityException, ParsingException, IOException {
		System.out.println("Model is alive");
		
		this.metamodelURL = metamodelURL;
		this.debugmode = debugmode;
		if (!graphOutputsPath.endsWith("/")) graphOutputsPath = graphOutputsPath + "/";
		this.graphOutputsPath = graphOutputsPath;
		
		umltograph = new UMLToGraph(metamodelURL);
		graphTransformer = new GraphTransformer(gtsRulesURL);
	}

	/** Returns objects in the object diagram representation of the graph */
	public ArrayList<ODObject> getODObjects() {
		return odObjs;
	}
	
	/** Returns links in the object diagram representation of the graph */
	public ArrayList<ODLink> getODLinks() {
		return odLinks;
	}
	
	/**
	 * Build the graph
	 * @param modelpath Filepath to the model file
	 * @param instancepath Filepath to the instance definition
	 */
	public void buildGraph(URL modelURL, URL instanceURL) throws Exception {
		//clear any data from previous animations
		odObjs = null;
		odLinks = null;
    	ODObject.resetRandomLocator();
		
		graph = umltograph.buildGraph(modelURL, instanceURL);
		graphTransformer.setGraph(graph);
		
		transitionnumber = 0;
		
		//if system is in debug mode, output graph as dot file
		if (debugmode) outputdot(graph, graph);
		
		//Construct the object diagram representation of the new graph
		constructObjDiag();
	}
	
	/** Apply a GTS rule and construct object diagram of new system state.
	 * For example: obj.getName()="m1", actionName="ActorSendSignal", actionParam = "cook" */
	public void transition(ODObject obj, String actionName, String actionParam) throws Exception {
		//Clone graph for post-transition comparison
		Graph oldGraph = new Graph(graph);
		
		graph = graphTransformer.transition(obj, actionName, actionParam);
		
		transitionnumber++;
		if (debugmode) outputdot(graph, oldGraph);
			
		//check for mode-change: (accept event -> action mode, complete activity -> event mode)
		if (actionName.equals("AcceptEventAction")) {
			obj.setEventMode(false);
		} else if (obj.activityComplete() && !actionName.equals("ActorSendSignal")) {
			obj.setEventMode(true);
		}
		constructObjDiag();
	}
	
	private void constructObjDiag() {
		
		// Get the names of objects not in event mode.
		ArrayList<String> objectsInActionMode = new ArrayList<String>();
		if(odObjs != null) {
			for(ODObject odObj : odObjs) {
				if(!odObj.getEventMode()) {
					// assuming all objects have a unique name
					objectsInActionMode.add(odObj.getName());
				}
			}
		}
		
		// Get the current locations of objects
		HashMap<String,Point> locations = new HashMap<String,Point>();
		if(odObjs != null) {
			for(ODObject odObj : odObjs) {
				locations.put(odObj.getName(), odObj.getLocation());
			}
		}
				
		graph.generateObjectDiagram();
				
		odObjs = graph.getODObjects();
		odLinks = graph.getODLinks();
		
		for(ODObject odObj : odObjs) {
			if(objectsInActionMode.contains(odObj.getName()) && !odObj.activityComplete()) {
				odObj.setEventMode(false);
			}
		}
		for(ODObject odObj : odObjs) {
			if (locations.containsKey(odObj.getName())) {
				odObj.setLocation(locations.get(odObj.getName()));
			}
		}
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
			BufferedWriter out = new BufferedWriter(new FileWriter(graphOutputsPath + "state" + transitionnumber + ".dot"));
			out.write(res);
			out.close();
			System.out.println("Dot code written");
		} catch (Exception e) {
			System.err.println("Error writing dot file: " + e.getMessage());
		}
	}
}
