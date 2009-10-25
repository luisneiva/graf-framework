import java.util.ArrayList;
import java.util.Iterator;

/* 
 *   Rule
 *   A Rule is a before Graph and an after Graph.
 *   If the input matches the before Graph, by being 
 *   greater than or equal to it (see Graph.java), then
 *   it can be transformed into the after Graph. However,
 *   the output will be the after Graph PLUS the the content
 *   on the input, not on the before Graph.
 *
 *   For example: if the input is N1 - N2 (the - is some irrelevant edge)
 *   and the before graph is N1
 *   and the after graph is N3
 *   then the output will be N3 - N2
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
 *   Contents of Rule
 *
 *     1. Rule() 
 *     -description: at the moment just constructs one extremely basic rule for testing.
 *     -inputs: at the moments none, but later it should be possible
 *     		to use a before and after graph as inputs
 *     -output: constructed rule
 *     2. evaluate() 
 *     -description: incomplete. It only checks if a rule could be applied at the moment.
 *     -inputs: the graph to apply this rule to
 *     -outputs: a graph that is the rule applied to the input
 *     3. trace()
 *     -description: determines if before is a subgraph of the input by recursing 
 *     				through nodes.
 *     -inputs: The graph to compare to the before graph.
 *     -outputs: true if it's a subgraph
 *     
 *     NOTE it is assumed the input and output graph have
 *     at least one node in common
 */


public class Rule {
	Graph before;
	Graph after;

	public Rule(Graph before, Graph after) {
		this.before = before;
		this.after = after;
	}
	
	public Rule() {

		before = new Graph();
		after = new Graph();

		// TEST RULE NUMBER 1
		// this rule is: if there is a node "Class" that
		// doesn't have a node from itself to itself
		// then add that node
		/*
		before = new Graph();
		after = new Graph();

		Node classnode = new Node("Class");
		before.add(classnode);

		Node classnode2 = new Node("Class");
		Edge classtoclass = new Edge(EdgeType.i);
		classtoclass.connect(classnode2, classnode2);

		after.add(classnode2);
		 */		 
		/*
		// TEST RULE NUMBER 2
		// if there is a node of *any* name that has
		// an edge labelled unknown pointing to Class,
		// then delete it

		Node classnode = new Node("Class");
		after.add(classnode);

		Node classnode2 = new Node("Class");
		Node blanknode = new Node(null);
		Edge classToBlank = new Edge(EdgeType.unknown);
		classToBlank.connect(classnode2, blanknode);
		before.add(classnode2);
		before.add(blanknode);
		 */

		// TEST RULE NUMBER 3
		// delete node State which has an i edge pointing
		// to Class,
		// also add node Object which has an i edge 
		// pointing to class
		// Finally, remove the edge from class to state
		// of type unknown

		Node classnode = new Node("Class");
		Node statenode = new Node("State");
		Edge iedge = new Edge();
		iedge.connect(classnode, statenode);
		Edge edgeToRemove = new Edge("unknown edge");
		edgeToRemove.connect(statenode, classnode);
		before.add(classnode);
		before.add(statenode);

		Node afclass = new Node("Class");
		Node afOb = new Node("Object");
		Edge i2edge = new Edge();
		i2edge.connect(afclass, afOb);
		after.add(afclass);
		after.add(afOb);

	}

	public Graph evaluate(Graph graph) {

		Boolean possible = graph.subgraphOf(before);
		if(!possible) {
			System.out.println("can't apply rule because " +
			"input doesn't match before graph");
			return null;
		}

		if(before.subgraphOf(graph)) {
			System.out.println("input is identical to before so output is exactly after\n");
			return new Graph(after);
		}

		System.out.println("ok to apply rule\n");
		Graph result = null;//new Graph(after);

		if(result != null)
			result.describe();

		/*
		 * The approach to rule evaluation below is based
		 * on comparing before and after to come up with single
		 * steps to apply to input
		 */

		ArrayList<Node> nodesToDelete = new ArrayList<Node>();
		ArrayList<Node> nodesToAdd = new ArrayList<Node>();

		ArrayList<Edge> edgesToAdd = new ArrayList<Edge>();
		ArrayList<Edge> edgesToRemove = new ArrayList<Edge>();

		for(Node bef : before) {
			Boolean match = false;
			for(Node aft : after) {
				if(aft.equals(bef)) {
					match = true;
					break;
				}
			}
			if(!match) {//found a node in before not after
				nodesToDelete.add(bef);
			}
		}

		for(Node aft : after) {
			Boolean match = false;
			for(Node bef : before) {
				if(aft.equals(bef)) {
					match = true;
					break;
				}
			}
			if(!match) {//found a node in after not before
				nodesToAdd.add(aft);
			}
		}

		//find edges to remove
		for(Node befNode : before) {
			for(Edge befEdge : befNode.edgesFromHere) {
				Boolean match = false;
				for(Node aftNode : after) {
					for(Edge aftEdge : aftNode.edgesFromHere) {
						if(befEdge.equals(aftEdge)) {
							match = true;
							break;
						}
					}
					if(!match && !edgesToRemove.contains(befEdge)) {
						edgesToRemove.add(befEdge);
					}
				}
			}
		}

		//find edges to add
		for(Node afterNode : after) {
			for(Edge afterEdge : afterNode.edgesToHere) {
				Boolean match = false;
				for(Node beforeNode : before) {
					for(Edge beforeEdge : beforeNode.edgesToHere) {
						if(beforeEdge.equals(afterEdge)) {
							match = true;
							break;
						}
					}
					if(!match && !edgesToAdd.contains(afterEdge)) {
						edgesToAdd.add(afterEdge);
					}
				}
			}
		}	

		for(Node node : nodesToAdd) {
			System.out.println("Add node " + node.name);	
		}
		for(Node node : nodesToDelete) {
			System.out.println("Remove node " + node.name);
		}
		for(Edge edge : edgesToAdd) {
			System.out.println("Add edge " + edge.toString());	
		}
		for(Edge edge : edgesToRemove) {
			System.out.println("Remove edge " + edge.toString());	
		}

		result = graph;

		Iterator<Node> nodeIterator = result.iterator();

		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.next();

			for(Node deleteNode : nodesToDelete) {
				if(deleteNode.equals(node)) {
					nodeIterator.remove();
					break;
				}
			}

			for(Node addNode : nodesToAdd) {
				// add nodes
			}
		}

		for(Node node : result) {
			Iterator<Edge> edgeIterator = node.edgesFromHere.iterator();
			while(edgeIterator.hasNext()) {
				Edge edge = edgeIterator.next();
				for(Edge remove : edgesToRemove) {
					if(edge.equals(remove)) {
						edgeIterator.remove();
					}
				}
			}

			Iterator<Edge> edgeIterator2 = node.edgesToHere.iterator();
			while(edgeIterator2.hasNext()) {
				Edge edge = edgeIterator2.next();
				for(Edge remove : edgesToRemove) {
					if(edge.equals(remove)) {
						edgeIterator2.remove();
					}
				}
			}
		}

		// add edges

		result.removeDanglingEdges();

		return result;


		/*
		 * The commented code below is an approach that did not
		 * work.
		 *  The idea was to take the after graph and add stuff
		 *  from the input onto it.
		 */
		/*
		//need to do this to prevent concurrent modification exception
		ArrayList<Edge> fromEdgesToMatch = new ArrayList<Edge>();
		ArrayList<Edge> toEdgesToMatch = new ArrayList<Edge>();

		for(Node oldNode : graph) {
			for(Node newNode : result) {
				if(newNode.equals(oldNode)) {
					for(Edge oldEdge : oldNode.edgesFromHere) {
						Boolean match = false;
						for(Edge newEdge : newNode.edgesFromHere) {
							if(newEdge.toEquals(oldEdge)) {
								match = true;
								break;
							}
						}
						if(!match) {//found an edge from input that we need to add to output
							fromEdgesToMatch.add(oldEdge);
							//result.matchOldEdge(oldEdge, true);
						}
					}
					for(Edge oldEdge : oldNode.edgesToHere) {
						Boolean match = false;
						for(Edge newEdge : newNode.edgesToHere) {
							if(newEdge.fromEquals(oldEdge)) {
								match = true;
								break;
							}
						}
						if(!match) {//found an edge from input that we need to add to output
							toEdgesToMatch.add(oldEdge);
							//result.matchOldEdge(oldEdge, false);
						}	
					}
				}
			}
		}

		System.out.println(fromEdgesToMatch.size() + " " + toEdgesToMatch.size() + "\n]]]]");

		for(Edge edge : fromEdgesToMatch)
			result.matchOldEdge(edge, true);

		for(Edge edge : toEdgesToMatch)
			result.matchOldEdge(edge, false);

					return result;
		 */

	}


	//TODO Kevin's method
	public Boolean trace(Graph input) {
		Boolean value = false;
		Boolean broken = false;
		for(Node n : before) {
			if(n != null) {
				for(Node in : input) {
					if(in.equals(n)) {					
						value = n.trace(in);
						broken = true;
						break;
					}
				}
				if(broken) {
					break;
				}
			}
		}
		
		before.resetTraceCheck();
		input.resetTraceCheck();
		
		return value;
	}


}
