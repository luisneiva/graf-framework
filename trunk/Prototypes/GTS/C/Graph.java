/* 
 *   Graph
 *   This class contains a list of Nodes.
 *   It is assumed all Nodes have been initialised with Edges.
 *   It is also assumed that all nodes are linked together
 *   by some combination of Edges.
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
 *   Contents of Graph
 *
 *     1. describe() 
 *     -description: prints a long description of every Node in the Graph.
 *     -inputs:
 *     -outputs:
 *    
 *     2. subgraphOf() (doesn't work)
 *     -description: checks whether a Graph matches the before Graph of a Rule.
 *	   -inputs: another graph to compare to this one
 *	   -outputs: true if this (not the input) is a lesser version of the input
 *	  
 *	   3. matchOldEdge() (currently not used)
 *     -description: (only called by Rule.java)
 *     				Context: we have applied the after graph of
 *     				a rule and now we need to add to this
 *     				new graph all of the nodes which are
 *     				on the input but not the before or
 *     				after graphs
 *     -inputs: An edge from old graph, not on new graph, but
 *     			one of the nodes of this edge is in the new graph.
 *     			And a boolean whether this is from or to the old graph
 *     -outputs: none, but input graph will have input node attached to it
 *    
 *     4. removeDanglingEdges()
 *     -description: checks for any edges in any node in this,
 *     that have an edge that connects to a node not in this,
 *     and removes the edge. That is, it removes dangling edges.
 *     -inputs: none
 *     -outputs: none
 *     
 *     5. resetTraceCheck()
 *     -description: called by rule.trace(). Resets all visited
 *     				 attributes on nodes.
 *     -inputs: none
 *     -outputs: none
 *     
 *     6. toObjectDiagram()
 *     -description: generates a string describing what an object diagram of the 
 *     				 graph would look like.
 *     -inputs: none
 *     -outputs: a string
 *     
 *     7. getNodeWithName()
 *     -description: searches the list for a node with a name equal to the input string
 *     -inputs: string of a node name
 *     -outputs: null if it doesn't exist, otherwise the node
 */

import java.util.ArrayList;
import java.util.Iterator;

public class Graph extends ArrayList<Node> {

	public Graph() {
	}

	//Copy constructor
	// creates a graph identical to input,
	// but pointers are different
	public Graph(Graph toCopy) {

		for(Node node : toCopy) {
			String newname = new String(node.name);
			ArrayList<Edge> toEdges = new ArrayList<Edge>();
			ArrayList<Edge> fromEdges = new ArrayList<Edge>();

			for(Edge edge : node.edgesToHere) {
				toEdges.add(new Edge(edge));
			}

			for(Edge edge : node.edgesFromHere) {
				fromEdges.add(new Edge(edge));	
			}

			Node newnode = new Node(newname);
			newnode.edgesFromHere = fromEdges;
			newnode.edgesToHere = toEdges;
			add(newnode);
		}
	}

	public void describe() {
		for(Node node : this) {
			System.out.println("There is a node called " + node.name + " ("+node+")");
			if(node.edgesToHere.size() > 0)	{
				System.out.println("The node has the following arrows pointing to it");
				for(Edge edge : node.edgesToHere) {
					System.out.println("Of type " + edge.label + " from " + edge.from.name + " ("+edge.from+")");
				}
			}
			if(node.edgesFromHere.size() > 0) {
				System.out.println("The node has the following arrows pointing from it");
				for(Edge edge : node.edgesFromHere) {
					System.out.println("Of type " + edge.label + " to " + edge.to.name + " (" +edge.to+ ")");
				}
			}
			System.out.println();
		}
	}

	/*
	 * a rule can only be applied if the graph matches the
	 * before graph
	 * 
	 * returns true if this is similar to before but with extra 
	 * nodes and edges
	 * 
	 * return false if before has edges and nodes not on this
	 * 
	 * In order to match, all nodes in other must be in this
	 * and they must be linked in the same way
	 * 
	 * It is okay for this to have nodes and edges not in other
	 */
	public Boolean subgraphOf(Graph before) {
		// first, just check nodes
		for(Node othernode : before) {
			Boolean match = false;
			for(Node thisnode : this) {
				if(othernode.equals(thisnode)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}

		// now check edges match up
		for(Node othernode : before)
			for(Node equivalent : this) {
				if(equivalent.equals(othernode)) {
					for(Edge fromedge : othernode.edgesFromHere) {
						Boolean match = false;
						for(Edge thisedge : equivalent.edgesFromHere) {
							if(thisedge.equals(fromedge)) {
								match = true;
								break;
							}
						}
						if(!match) {
							return false;
						}
					}

					for(Edge toedge : othernode.edgesToHere) {
						Boolean match = false;
						for(Edge thisedge : equivalent.edgesToHere)	{
							if(thisedge.equals(toedge))	{
								match = true;
								break;
							}
						}
						if(!match)	{
							return false;
						}
					}
				}
			}

		return true;
	}

	public void matchOldEdge(Edge oldEdge, Boolean toNotFrom) {

		Node nodeInQuestion = oldEdge.from;
		if(toNotFrom) {
			nodeInQuestion = oldEdge.to;
		}

		if(nodeInQuestion.name == null) {
			System.out.println("ERROR ASSERT FALSE");
			System.out.print("I don't know how to match a null node");
			System.out.print("Big error in Graph.java");
			System.exit(1);
		}

		for(Node node : this)
			if(nodeInQuestion.equals(node)) {
				System.out.println("ERROR ASSERT FALSE");
				System.out.println("matchOldEdge assert failure");
				System.out.println("2 nodes with same name or something bad");
				System.exit(1);
			}

		Node nodeToAddTo = oldEdge.to;
		if(toNotFrom) {
			nodeToAddTo = oldEdge.from;
		}

		Boolean foundIt = false;
		for(Node node : this)
			if(nodeToAddTo.name.equals(node.name)) {
				nodeToAddTo = node;
				foundIt = true;
			}
		if(!foundIt) {
			System.out.println("ERROR ASSERT FALSE");
			System.out.println("graph.java couldn't get a grip on where to add new edges");
			System.exit(1);
		}

		Edge edge = new Edge(oldEdge.label);

		if(toNotFrom) {
			edge.connect(nodeToAddTo, nodeInQuestion);
		}
		else {
			edge.connect(nodeInQuestion, nodeToAddTo);
		}

		add(nodeInQuestion);
	}

	public void removeDanglingEdges() {
		for(Node node : this) {
			Iterator<Edge> edgeIterator = node.edgesFromHere.iterator();
			while (edgeIterator.hasNext()) {
				Edge edge = edgeIterator.next();
				if(!contains(edge.to)) {
					edgeIterator.remove();
				}
			}

			Iterator<Edge> edgeIterator2 = node.edgesToHere.iterator();
			while (edgeIterator2.hasNext()) {
				Edge edge = edgeIterator2.next();
				if(!contains(edge.from)) {
					edgeIterator2.remove();
				}
			}
		}
	}

	public void resetTraceCheck() {
		for(Node node : this) {
			node.visited = false;
		}
	}

	// NOTE THIS IS NOT UP TO DATE
	// prototype G contains all the new object diagram stuff
	public String toObjectDiagram() {
		String result = "";

		Node classNode = null;
		for(Node node : this) {
			if(node.name.toLowerCase().equals("class")) {
				classNode = node;
				break;
			}
		}

		if(classNode == null) {
			return "the graph has no node class";
		}

		ArrayList<Node> classes = new ArrayList<Node>();
		for(Edge edgeToClass : classNode.edgesToHere) {
			if(edgeToClass.label.equals("i")) {
				Node clas = edgeToClass.from;
				classes.add(clas);
			}
		}

		ArrayList<Node> objects = new ArrayList<Node>();
		for(Node clas : classes) {
			for(Edge edgeToClas : clas.edgesToHere) {
				if(edgeToClas.label.equals("i")) {
					Node obj = edgeToClas.from;
					objects.add(obj);
					result += obj.name + " : " + clas.name + "\n";
				}
			}
		}

		for(Node object : objects) {
			result += "-------------\n"+object.name+"\n-------------\n";
			/*for(Edge edgeToAttr : object.edgesToHere) {
				String attrValue = edgeToAttr.from.name;
				String attrName = edgeToAttr.label;
				result += "\t " + attrName + " = " + attrValue + "\n";
			}*/
			for(Edge edgeFromAttr : object.edgesFromHere) {
				if(!edgeFromAttr.label.equals("i")) {
					String attrValue = edgeFromAttr.to.name;
					String attrName = edgeFromAttr.label;
					result += "\t " + attrName + " = " + attrValue + "\n";
				}
			}
		}

		return result;
	}

	public Node getNodeWithName(String from) {
		for(Node node : this) {
			if(node.name.equals(from)) {
				return node;
			}
		}	

		return null;
	}

}
