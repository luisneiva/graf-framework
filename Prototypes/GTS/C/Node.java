/* 
 *   Node
 *   This class represents a graph node. It has a label and a list of Edges
 *   to and from it.
 *
 *   Version description - version 1.3
 *   	Extracts object diagrams (as strings) from graphs. 
 *   		But this will be extended in prototype G.
 *   	Loads .triple files.
 *   	Tests for subgraphs using tracing in a very limited way.
 *   	Doesn't do much else.
 *   
 *   Date 3/6/09
 *
 *   Oscar
 *   Contents of Node
 *
 *     1. equals()
 *     -description: to be equal, 2 Nodes don't need to be the same Java object,
 *                 they just need the same name.
 *     -inputs: another node to compare to this one
 *     -outputs: true if the nodes are equal else false
 *     2. hasSameEdges() -
 *     -description: used for evaluating null name nodes,
 *     				simply compares edges,
 *     -inputs: another node
 *     -outputs: true if all edges are the same 
 *     				(includes where they point to and from)
 *     3.trace()
 *     -description: (should only be called from rule.trace() 
 *     				 or this.trace()) 
 *     				 (To call trace(), *this* should be in a before graph) 
 *     				 (Assumes all nodes in graph connected)
 *     				 This tests that *this* equals the input then recurses to 
 *     				 neighbouring nodes. It uses the variable
 *     				 visited to avoid visiting the same place twice.
 *     -inputs: Node in input graph to compare.
 *     -outputs: Whether this node is equivalent to input.
 *     
 *     NOTE: hasSameEdges will fail (I think) if there are 2
 *     null name nodes connected by an edge
 *     NOTE: it doesn't make sense to use a null name edge
 *     in a graph that is not a before or after rule
 *     NOTE: using a null name edge in an after graph is a bad
 *     idea at the moment
 */

import java.util.ArrayList;


public class Node {

	ArrayList<Edge> edgesToHere;
	ArrayList<Edge> edgesFromHere;
	String name;
	
	//Used for tracing graph to make sure we don't backtrack.
	Boolean visited;

	public Node(String name) {
		visited = false;
		this.name = name;
		edgesFromHere = new ArrayList<Edge>();
		edgesToHere = new ArrayList<Edge>();
	}

	// if the two nodes are equal in terms
	// of graph transformations
	// That is, do they have the same name.
	// If name is unimportant in comparing a node,
	// then that node will have name null
	// and it will be counted as equal if edges match.
	public Boolean equals(Node other) {

		if(name != null && other.name != null) {
			return name.equals(other.name);
		}
		else if(name == null
				|| other.name == null) {
			if(name == null
					&& other.name == null) {
				System.out.println("Error in node.equals: both nodes have null names");
				System.out.println("Only one node (the one in the graph rule) should have a null name");
				return false;
			}
			else if(name == null) {
				return hasSameEdges(other);
			}
			else {
				return other.hasSameEdges(this);
			}
		}
		else
			return false;
	}

	/*public String toString() {
		if(name != null)
			return name;
		else
			return "(no name)";
	}*/

	// returns false if there is an edge in *this*
	// that *other* does not have
	private Boolean hasSameEdges(Node other) {
		for(Edge out : edgesFromHere) {
			Boolean match = false;
			for(Edge otherOut : other.edgesFromHere) {
				if(otherOut.toEquals(out)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}

		for(Edge in : edgesToHere) {
			Boolean match = false;
			for(Edge otherIn : other.edgesToHere) {
				if(otherIn.fromEquals(in)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}

		return true;
	}
	
	public Boolean trace(Node inputNode) {
		
		System.out.println("trace arrives at " + name + " / " + inputNode.name);
		
		visited = true;
		for(Edge toEdge : edgesToHere) {
			Boolean match = false;
			for(Edge inToEdge : inputNode.edgesToHere) {
				if(inToEdge.equals(toEdge)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}
		Boolean value = true;
		for(Edge fromEdge : edgesFromHere) {
			Boolean match = false;
			for(Edge inFromEdge : inputNode.edgesFromHere) {
				if(inFromEdge.equals(fromEdge)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}
		
		for(Edge toEdge : edgesToHere) {
			if(!toEdge.from.visited) {
				for(Edge inToEdge : inputNode.edgesToHere) {
					if(inToEdge.equals(toEdge)) {
						value = value && toEdge.from.trace(inToEdge.from);
						break;
					}
				}
			}
		}
		
		for(Edge fromEdge : edgesFromHere) {
			if(!fromEdge.to.visited) {
				for(Edge inFromEdge : inputNode.edgesFromHere) {
					if(inFromEdge.equals(fromEdge)) {
						value = value && fromEdge.to.trace(inFromEdge.to);
						break;
					}
				}
			}
		}
		
		return value;
		
	}
}
