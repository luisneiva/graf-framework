package graphTransformer;
/* 
 *   Node
 *   A node in a graph. Contains edges to and from it.
 *
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of Node
 *   1. replace()
 *	   -description: Changes the name of this node.
 *					 Only if the current name is in the input.
 *	   -inputs: Name to change from, and name to change to.
 *	   -outputs: None.
 *
 *   2. calculateCertainty()
 *   -description: Does nothing if certainty is not null.
 *   			   Otherwise it calculates an integer in order
 *   			   to compare between nodes how good an idea
 *   			   it is to make a replacement.
 *   -inputs: None.
 *   -outputs: None.
 *   
 *   3. getSize()
 *   -description: Recurses through the edges and nodes to find
 *   			   the total size of the graph from a set 
 *   			   starting point.
 *   -inputs: None.
 *   -outputs: The size of the bit explored by this part of
 *   		   the recursion.
 *   
 *   4. deleteNodeReferences()
 *   -description: In the edges to and from here, if the edge 
 *   			   on the other side is in the input list then
 *   			   delete that node and edge.
 *   -inputs: A list of node names to be deleted.
 *   -outputs: None. 
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

	public void replace(String target, String replacement) {
		if(name.equals(target)) {
			name = replacement;
			variable = name.charAt(0) == '?';
		}
	}

	public String toString() {
		return getName();
	}

	public void calculateCertainty() {

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

	public Integer getSize() {
		if(checked) {
			return 0;
		}
		checked = true;

		Integer count = 0;
		for(Edge fromEdge : edgesFrom) {
			Node other = fromEdge.getTo();
			count += other.getSize();

			/*if(max != null && count > max) {
				System.out.println("HAPENSZ");
				//Big enough already. No need to continue over the
				// entire graph.
				return 1+max;
			}*/
		}

		for(Edge toEdge : edgesTo) {
			Node other = toEdge.getFrom();
			count += other.getSize();

			/*if(max != null && count > max) {
				System.out.println("HAPENSZ");
				//Big enough already. No need to continue over the
				// entire graph.
				return 1+max;
			}*/
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
	
	/*
	 * the following methods are only used in the 
	 * graph to uml module
	 */
	
	public Node toTrace(ArrayList<String> edgeNames) {
		if(edgeNames.size() == 1) {
			String x = edgeNames.get(0);
			for(Edge edgeFrom : edgesFrom) {
				if(edgeFrom.getLabel().equals(x)) {
					return edgeFrom.getTo();
				}
			}
			return null;
		}
		
		String x = edgeNames.remove(0);
				
		for(Edge edgeFrom : edgesFrom) {
			if(edgeFrom.getLabel().equals(x)) {
				return edgeFrom.getTo().toTrace(edgeNames);
			}
		}
		return null;
	}
	
	public Node fromTrace(ArrayList<String> edgeNames) {
		if(edgeNames.size() == 1) {
			String x = edgeNames.get(0);
			for(Edge edgeTo : edgesTo) {
				if(edgeTo.getLabel().equals(x)) {
					return edgeTo.getFrom();
				}
			}
			return null;
		}
		
		String x = edgeNames.remove(0);
				
		for(Edge edgeTo : edgesTo) {
			if(edgeTo.getLabel().equals(x)) {
				return edgeTo.getFrom().fromTrace(edgeNames);
			}
		}
		return null;
	}
}
