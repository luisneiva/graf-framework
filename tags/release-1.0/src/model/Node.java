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

	// The number of constants that this connects to
	// (either to or from) minus the number of variables
	// this connects to.
	// Used in Rule.java sortPairs()
	private Integer certainty;

	public Node(String name) {
		this.name = name;
		edgesFrom = new ArrayList<Edge>();
		edgesTo = new ArrayList<Edge>();
		variable = name.charAt(0) == '?';
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
	public void calculateCertainty(ArrayList<String> likelyNodes) {

		// The same node may appear in many VariableAssignments
		// but this method gets called for each VariableAssignments.
		// So just skip it if it's already been done.
		if(certainty != null) {
			return;
		}

		Integer score = 0;

		// If the variable only connects to constants
		// then it will be very easy to solve.
		Boolean noVariables = true;

		// If the variable only connects to variables
		// then it will be extremely difficult to solve.
		Boolean allVariables = true;

		// If the variable has an edge to node x
		// where node x has NO OTHER edges.
		//	Boolean dedicatedNode = false;

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

			/*if(!dedicatedNode) {
				Node farNode = toEdge.from;
				Integer farNodeEdges;
				farNodeEdges = farNode.getTo().size() 
				+ farNode.getFrom().size();
				if(farNodeEdges < 2) {
					System.out.println("yes1 " + toString() + farNodeEdges);
					dedicatedNode = true;
				}
			}*/
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

			/*if(!dedicatedNode) {
				Node farNode = fromEdge.to;
				Integer farNodeEdges;
				farNodeEdges = farNode.getTo().size() 
				+ farNode.getFrom().size();
				if(farNodeEdges < 2) {
					dedicatedNode = true;
					System.out.println("yes " 
							+ toString() +" "
							+ farNodeEdges +" "+farNode.getName());
				}
			}*/
		}

		if(noVariables) {
			// Test 8 gives an example of why these
			// nodes are so dangerous.
			score -= 2000;
		}

		if(allVariables) {
			score -= 1000;
		}

		/*if(dedicatedNode) {
			score += 1000;
		}*/

		certainty = score;
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
					//return edgeTo.getFrom();
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
				//return edgeTo.getFrom().fromTrace(edgeNames);
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
