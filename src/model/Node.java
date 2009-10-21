package model;
/** 
 *   Node
 *   A node in a graph. Contains edges to and from it.
 *
 *	@author Oscar Wood 
 *
 */

import java.util.ArrayList;
import java.util.Iterator;


public class Node {

	private String name;
	private ArrayList<Edge> edgesFrom;
	private ArrayList<Edge> edgesTo;

	private Boolean variable;
	private Boolean checked;

	/**
	 * The number of constants that this connects to
	 * (either to or from) minus the number of variables
	 * this connects to.
	 * Used in Rule.java sortPairs()
	 */
	private Integer certainty;

	public Node(String name) {
		this.name = name;
		edgesFrom = new ArrayList<Edge>();
		edgesTo = new ArrayList<Edge>();
		variable = name.startsWith("?");
		checked = false;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Edge> getFrom() {
		return edgesFrom;
	}

	public ArrayList<Edge> getTo() {
		return edgesTo;
	}

	public Boolean variable() {
		return variable;
	}

	/**
	 * Changes the name of this node.
	 * Only if the current name is in the input.
	 * @param target
	 * @param replacement
	 */
	public void replace(String target, String replacement) {
		if(name.equals(target)) {
			name = replacement;
			variable = name.charAt(0) == '?';
		}
	}

	public String toString() {
		return getName();
	}

	/**
	 * Does nothing if certainty is not null. Otherwise it 
	 * calculates an integer in order to compare between nodes
	 * how good an idea it is to make a replacement.
	 */
	public void calculateCertainty(ArrayList<String> likelyNodes, Boolean dealged) {

		// The same node may appear in many VariableAssignments
		// but this method gets called for each VariableAssignments.
		// So just skip it if it's already been done.
		if(certainty != null) {
			return;
		}

		// If there is only one edge to/from
		// and that (dealged) edge is a variable
		// then we need to wait until we've solved
		// the variable node with the same name
		// as the variable edge
		if(dealged) {
			Integer numToEdges = 0;
			for(Edge e : edgesTo) {
				if(e.getLabel().equals("to")) {
					numToEdges++;
				}
			}
			//System.out.println("[[[ " + name + " " + numToEdges);
			if(numToEdges == 1) {
				Node farNode = edgesTo.get(0).getFrom();
				if(farNode.variable) {
					//System.out.println("HERE " + name + " using " + farNode.name);
					certainty = -100000;
					return;
				}
			}
		}

		if(dealged && edgesFrom.size() == 0) {
			calculateDealgedCertainty(likelyNodes);
			//System.out.println("using dealg scoring system" + name);
			return;
		}
		//else System.out.println("using alg scoring system" + name);

		Integer score = 0;

		// If the variable only connects to constants
		// then it will be either very easy or very hard to solve.
		Boolean noVariables = true;

		// If the variable only connects to variables
		// then it will be difficult to solve.
		Boolean allVariables = true;

		for(Edge toEdge : edgesTo) {
			if(toEdge.fromIsVariable) {
				noVariables = false;
				score--;
			} else {
				score++;
				allVariables = false;
			}

			if(likelyNodes.contains(toEdge.getFromName())) {
				score +=10000;
			}
		}

		for(Edge fromEdge : edgesFrom) {
			if(fromEdge.toIsVariable) {
				noVariables = false;
				score--;
			} else {
				score++;
				allVariables = false;
			}


			if(likelyNodes.contains(fromEdge.getToName())) {
				score +=10000;
			}
		}

		if(noVariables) {
			// Test 8 in prototype D gives an example of why these
			// nodes are so dangerous.
			score -= 2000;
		}

		if(allVariables) {
			score -= 1000;
		}

		certainty = score;
	}

	/**
	 * If it is dealgebrized then old processes will find former edges as nodes.
	 * This is not useful for the code. We want to find nodes, not edges.
	 * Therefore, instead of looking one edge ahead, look 2 edges ahead to find 
	 * what used to be neighbouring edges before dealging.
	 * Only do this for nodes that were never edges.
	 */
	public void calculateDealgedCertainty(ArrayList<String> likelyNodes) {
		
		ArrayList<Node> actualToNodes = new ArrayList<Node>();
		ArrayList<Node> actualFromNodes = new ArrayList<Node>();

		for(Edge edgeto : edgesTo)
		{
			Node toNode = edgeto.getFrom();

			if(!(toNode.getFrom().size() == 2 && toNode.getTo().size() == 0)) {
				System.out.println("error in calculate dealged certainity in node.java");
				break;
			}

			Node farNode1 = toNode.getFrom().get(0).getTo();
			Node farNode2 = toNode.getFrom().get(1).getTo();

			Node actualNode;
			if(name.equals(farNode1.getName())) {
				actualNode = farNode2;
				actualFromNodes.add(actualNode);
			}
			else {
				actualNode = farNode1;
				actualToNodes.add(actualNode);
			}

			//actualToNodes.add(actualNode);

		}

		//System.out.println("------------------------------------------");
		//for(Node node : actualToNodes) {
		//	System.out.println(name + " to " + node);
		//}
		//for(Node node : actualFromNodes) {
		//	System.out.println(name + " from " + node);
		//}

		Boolean noVariables = true;
		Boolean allVariables = true;
		certainty = 0;

		for(Node node : actualToNodes) {
			if(node.variable) {
				noVariables = false;
				certainty--;
			}
			else {
				allVariables = false;
				certainty++;
			}
			if(likelyNodes.contains(node.getName())) {
				certainty +=10000;
			}
		}
		for(Node node : actualFromNodes) {
			if(node.variable) {
				noVariables = false;
				certainty--;
			}
			else {
				allVariables = false;
				certainty++;
			}
			if(likelyNodes.contains(node.getName())) {
				certainty +=10000;
			}
		}

		if(noVariables) {
			// Test 8 in prototype D gives an example of why these
			// nodes are so dangerous.
			certainty -= 2000;
		}

		if(allVariables) {
			certainty -= 1000;
		}
	}

	public Integer getCertainty() {
		return certainty;
	}

	public void resetCertainty() {
		certainty = null;		
	}

	/**
	 * Recurses through the edges and nodes to find the total 
	 * size of the graph from a set starting point.
	 */
	public Integer getSize() {
		if(checked) {
			return 0;
		}
		checked = true;

		Integer count = 0;
		for(Edge fromEdge : edgesFrom) {
			Node other = fromEdge.getTo();
			count += other.getSize();
		}

		for(Edge toEdge : edgesTo) {
			Node other = toEdge.getFrom();
			count += other.getSize();
		}

		return 1 + count;
	}

	public void check() {
		checked = true;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void resetCheck() {
		checked = false;		
	}

	/**
	 * In the edges to and from here, if the edge on the other 
	 * side is in the input list then delete that node and edge.
	 * @param deleteNodes A list of names of nodes to be deleted.
	 */
	public void deleteNodeReferences(ArrayList<String> deleteNodes) {
		Iterator<Edge> toEdgeIt = edgesTo.iterator();
		while (toEdgeIt.hasNext()) {
			Edge edge = toEdgeIt.next();
			if(deleteNodes.contains(edge.getFromName())) {
				toEdgeIt.remove();
			}
		}

		Iterator<Edge> fromEdgeIt = edgesFrom.iterator();
		while (fromEdgeIt.hasNext()) {
			Edge edge = fromEdgeIt.next();
			if(deleteNodes.contains(edge.getToName())) {
				fromEdgeIt.remove();
			}
		}
	}

	public ArrayList<Node> toTrace(ArrayList<String> edgeNames) {

		ArrayList<String> edgeNamesCopy = new ArrayList<String>();
		edgeNamesCopy.addAll(edgeNames);

		ArrayList<Node> result = new ArrayList<Node>();

		if(edgeNamesCopy.size() == 1) {
			String x = edgeNamesCopy.get(0);
			for(Edge edgeFrom : edgesFrom) {
				if(edgeFrom.getLabel().equals(x)) {
					result.add(edgeFrom.getTo());
				}
			}
			return result;
		}

		String x = edgeNamesCopy.remove(0);

		for(Edge edgeFrom : edgesFrom) {
			if(edgeFrom.getLabel().equals(x)) {
				ArrayList<Node> recursedResult = edgeFrom.getTo().toTrace(edgeNamesCopy);
				result.addAll(recursedResult);				
			}
		}
		return result;
	}

	public ArrayList<Node> fromTrace(ArrayList<String> edgeNames) {

		ArrayList<String> edgeNamesCopy = new ArrayList<String>();
		edgeNamesCopy.addAll(edgeNames);

		ArrayList<Node> result = new ArrayList<Node>();

		if(edgeNamesCopy.size() == 1) {
			String x = edgeNamesCopy.get(0);
			for(Edge edgeTo : edgesTo) {
				if(edgeTo.getLabel().equals(x)) {
					result.add(edgeTo.getFrom());
				}
			}
			return result;
		}

		String x = edgeNamesCopy.remove(0);

		for(Edge edgeTo : edgesTo) {
			if(edgeTo.getLabel().equals(x)) {
				ArrayList<Node> rescursedResult = 
					edgeTo.getFrom().fromTrace(edgeNamesCopy);
				result.addAll(rescursedResult);
			}
		}
		return result;
	}

	public void rename(Integer appendage) {
		if(name.charAt(0) != '*')
			return;

		String newName = null;
		for(Edge edge : edgesFrom) {
			if(edge.getLabel().equals("i")) {
				newName = edge.getToName();
				break;
			}
		}

		if(newName != null) {
			if(newName.charAt(0) == '?') {
				newName = newName.substring(1);
				newName = "var" + newName;
			}
			newName = newName.toLowerCase();
			newName = newName + appendage.toString();
			name = newName;
		}
		else {
			name = name.substring(1);
			name = name + appendage.toString();
		}
	}
}
