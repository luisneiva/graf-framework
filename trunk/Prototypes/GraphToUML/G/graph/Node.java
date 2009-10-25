package graph;
/* 
 *   Node
 *   A node in a graph. Contains edges to and from it.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of Node
 *
 *    1. toTrace()
 *     -description: Returns the node at the end of the series
 *     				 of edges given in the input. This ignores
 *     				 node names and only looks in the to
 *     				 direction.
 *     -inputs: A list of edge names, in order to be traversed.
 *     -outputs: The node, if it exists, otherwise null.
 */



import java.util.ArrayList;


public class Node {
	
	private String name;
	private ArrayList<Edge> edgesFrom;
	private ArrayList<Edge> edgesTo;
	
	public Node(String name) {
		this.name = name;
		edgesFrom = new ArrayList<Edge>();
		edgesTo = new ArrayList<Edge>();
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
