package model;

import java.util.ArrayList;
import java.util.Iterator;

import agg.attribute.facade.impl.DefaultInformationFacade;
import agg.attribute.handler.AttrHandler;
import agg.attribute.impl.ValueMember;
import agg.xt_basis.Arc;
import agg.xt_basis.BaseFactory;
import agg.xt_basis.GraGra;
import agg.xt_basis.Graph;
import agg.xt_basis.GraphObject;
import agg.xt_basis.Node;
import agg.xt_basis.Type;
import agg.xt_basis.TypeException;

/**
 * 
 * The representation of graphs in GRAF.
 * We don't use AGG's graph because it lacks certain methods.
 * 
 * This class also contains the various static methods needed
 * to quickly interact with nodes and arcs in AGG.
 * 
 * @author Oscar Wood
 *
 */
public class ListGraph extends Graph {

	/** nodes added with this character at the beginning will
	 * have their name changed */
	final private static String renameIndicator = "#";

	final private Type nodeType;
	final private Type edgeType;
	final private AttrHandler javaHandler;

	/** used to ensure uniqueness in unique names from # */
	private static Integer uniqueNumber = 0;

	/**
	 * begins empty
	 */
	public ListGraph() {
		super();
		GraGra graphGrammar = BaseFactory.theFactory().createGraGra();
		nodeType = graphGrammar.createType(true);
		edgeType = graphGrammar.createType(true);
		nodeType.setStringRepr("");
		nodeType.setAdditionalRepr("[NODE]");
		edgeType.setStringRepr("");
		edgeType.setAdditionalRepr("[EDGE]");

		javaHandler = DefaultInformationFacade.self().getJavaHandler();
		nodeType.getAttrType().addMember(javaHandler,"String", "name");
		edgeType.getAttrType().addMember(javaHandler,"String", "name");
	}

	/**
	 * Copy constructor.
	 * The edges in the new Graph will have the same names
	 * as those in the one being copied but they won't have the
	 * same references/pointers.
	 */
	public ListGraph(Graph toCopy) {
		this();

		for(Arc arc : toCopy.getArcsList()) {
			String label = getName(arc);
			String to = getName(arc.getTarget());
			String from = getName(arc.getSource());

			addEdge(from, label, to);
		}
	}

	/**
	 * Returns the name of the node or arc.
	 * If it is a variable, a ? will be inserted before the
	 * first character
	 */
	public static String getName(GraphObject graphObj) {
		ValueMember value = (ValueMember) graphObj.getAttribute().getMemberAt("name");

		String str = value.toString();

		if(str == null || str.equals(""))
			return "(noname)";

		if(str.charAt(0) == '\"' && 
				str.charAt(str.length()-1) == '\"') {
			str = str.substring(1, str.length()-1);
		}
		else
			str = "?"+str;

		return str;
	}

	public static Boolean isVariable(GraphObject graphObj) {
		try {
			ValueMember val = (ValueMember) graphObj.getAttribute().getMemberAt("name");
			return val.toString().charAt(0) != '\"';
		} catch(StringIndexOutOfBoundsException e) {
			return true;
		}
	}

	public Node getNodeByName(String str) {
		str = "\"" + str + "\"";
		for(Node node : getNodesList()) {
			if(getName(node).equals(str)) {
				return node;
			}
		}
		return null;
	}

	public String toString() {

		String str = "The graph:\n";
		for(Arc edge : getArcsList()) {
			str += getName(edge.getSource()) +" "+ getName(edge)
			+" "+ getName(edge.getTarget()) + "\n";
		}

		return str;
	}

	/**
	 * only needed for debugging
	 */
	public void deleteEdge(String from, String label, String to) {
		Iterator<Arc> arcit = itsArcs.iterator();
		while(arcit.hasNext()) {
			Arc arc = arcit.next();
			if(getName(arc).equals(label)) {
				if(getName(arc.getTarget()).equals(to) &&
						getName(arc.getSource()).equals(from)) {
					
					removeArc(arc);
					
					return;
				}
			}
		}
	}

	/**
	 * Adds an edge.
	 * If the from or to node names are the same as names of nodes
	 * already in the graph then new nodes will NOT be created.
	 * The old nodes will be used.
	 */
	public void addEdge(String from, String label, String to) {

		Node fromNode = null;
		Node toNode = null;

		for(Node node : getNodesList()) {
			String str = getName(node);
			if(str.equals(from)) {
				fromNode = node;
			}
			if(str.equals(to)) {
				toNode = node;
			}
		}

		try {
			if(fromNode == null) {
				fromNode = createNode(nodeType);
				((ValueMember)fromNode.getAttribute().getMemberAt("name")).setExprAsObject(from);
			}

			if(toNode == null) {
				toNode = createNode(nodeType);
				((ValueMember)toNode.getAttribute().getMemberAt("name")).setExprAsObject(to);
			}

			Arc edge = createArc(edgeType, fromNode, toNode);
			((ValueMember)edge.getAttribute().getMemberAt("name")).setExprAsObject(label);			

		} catch (TypeException e) {
			System.out.println("exception adding edge");
			e.printStackTrace();
		}
	}

	/**
	 * The same as addEdge() but the edge label is "i"
	 * @see addEdge
	 */
	public void addIEdge(String from, String to) {
		addEdge(from, "i", to);
	}

	private static void rename(GraphObject object, String newName) {

		object.getAttribute().setValueAt(newName, "name");
	}

	/**
	 * Applies generateUniqName() to all nodes in the graph.
	 * 
	 * @param graph A graph that is not part of the rule.
	 */
	public static void giveAddedNodesUniqueNames(Graph graph) {
		for(Node node : graph.getNodesList()) {
			try {
				if(getName(node).substring(0, renameIndicator.length()).equals(renameIndicator)) {
					generateUniqueName(node);
				}
			} catch(StringIndexOutOfBoundsException sioobe) {
			}
		}
	}

	private static void generateUniqueName(GraphObject obj) {
		String newName = "addedEdge"+getName(obj);

		if(obj instanceof Node) {
			Node node = (Node) obj;
			ArrayList<String> edgeNames = new ArrayList<String>();
			edgeNames.add("i");
			String type;
			try {
				type = getName(toTrace(edgeNames, node).get(0));
				newName = type.toLowerCase();

				if(type.substring(0, 1).toLowerCase().equals(type.substring(0, 1))) {
					newName = "var" + newName;
				}
			} catch(IndexOutOfBoundsException ioobe) {
				newName = newName.toLowerCase();
			}
		}
		uniqueNumber++;
		newName = newName + uniqueNumber;
		rename(obj, newName);
	}

	/**
	 * Follow edges by their names by the directions of the graph
	 * This ignores node names
	 * @param edgeNames Edges to follow in order. Make sure they begin and end with "
	 * @param here The node to start on.
	 * @return a list of every node that can be reached by following edges in the given order
	 */
	public static ArrayList<Node> toTrace(ArrayList<String> edgeNames, Node here) {

		ArrayList<String> edgeNamesCopy = new ArrayList<String>();
		edgeNamesCopy.addAll(edgeNames);

		ArrayList<Node> result = new ArrayList<Node>();

		if(edgeNamesCopy.size() == 1) {
			String x = edgeNamesCopy.get(0);
			for(Arc edgeFrom : here.getOutgoingArcsVec()) {
				if(getName(edgeFrom).equals(x)) {
					result.add((Node)edgeFrom.getTarget());
				}
			}
			return result;
		}

		String x = edgeNamesCopy.remove(0);

		for(Arc edgeFrom : here.getOutgoingArcsVec()) {
			if(getName(edgeFrom).equals(x)) {
				ArrayList<Node> recursedResult = toTrace(edgeNamesCopy, (Node)edgeFrom.getTarget());
				result.addAll(recursedResult);				
			}
		}
		return result;
	}

	/**
	 * Like toTrace() but we follow the edges backwards
	 * @see toTrace
	 */
	public static ArrayList<Node> fromTrace(ArrayList<String> edgeNames, Node here) {

		ArrayList<String> edgeNamesCopy = new ArrayList<String>();
		edgeNamesCopy.addAll(edgeNames);

		ArrayList<Node> result = new ArrayList<Node>();

		if(edgeNamesCopy.size() == 1) {
			String x = edgeNamesCopy.get(0);
			for(Arc edgeTo : here.getIncomingArcsVec()) {
				if(getName(edgeTo).equals(x)) {
					result.add((Node)edgeTo.getSource());
				}
			}
			return result;
		}

		String x = edgeNamesCopy.remove(0);

		for(Arc edgeTo : here.getIncomingArcsVec()) {
			if(getName(edgeTo).equals(x)) {
				ArrayList<Node> recursedResult = fromTrace(edgeNamesCopy, (Node)edgeTo.getTarget());
				result.addAll(recursedResult);				
			}
		}
		return result;
	}

	/**
	 * @param name The name of a node will need a ? as the first
	 * character if it is a variable
	 * @return true if there is a node with the name input
	 */
	public boolean containsNode(String name) {

		for(Node node : getNodesList()) {
			if(getName(node).equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This returns true if they have they same name.
	 */
	public boolean containsEdge(Arc edge) {

		String toFind = getName(edge);

		for(Arc arc : getArcsList()) {
			if(getName(arc).equals(toFind)) {
				return true;
			}
		}

		return false;
	}
}
