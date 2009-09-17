package model;

import java.util.ArrayList;

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
 * @author Oscar Wood
 *
 */
public class ListGraph extends Graph {

	
	final private Type nodeType;
	final private Type edgeType;
	final private AttrHandler javaHandler;
	
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
	
	public ListGraph(Graph toCopy) {
		this();
		
		for(Arc arc : toCopy.getArcsList()) {
			String label = getName(arc);
			String to = getName(arc.getTarget());
			String from = getName(arc.getSource());
			
			addEdge(from, label, to);
		}
	}

	public static String getName(GraphObject graphObj) {
		ValueMember value = (ValueMember) graphObj.getAttribute().getMemberAt("name");

		String str = value.toString();

		if(str == null || str.equals(""))
			return "(noname)";
		
		if(str.charAt(0) == '\"' && 
				str.charAt(str.length()-1) == '\"') {
			str = str.substring(1, str.length()-1);
		}
		
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
		System.out.println("node " + str + " not found");
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
	
	public void addIEdge(String from, String to) {
		addEdge(from, "i", to);
	}
	
	/**
	 * @param edgeNames Edges to follow in order. Make sure they begin and end with "
	 * @param here The node to start on.
	 * @return
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
