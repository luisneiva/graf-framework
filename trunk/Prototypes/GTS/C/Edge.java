/* 
 *   Edge
 *   This class represents a line between graphs.
 *   It contains references to the node on either side.
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
 *   Contents of Edge
 *
 *     1. connect() 
 *     -description: sets the attributes on Node and Edge objects.
 *                  Once this has been called on an Edge you can usually 
 *                  ignore the Edge object and focus on the Node objects
 *                  which will reference the Edge objects.
 *	   -inputs: 2 nodes, the first will be from, the second to
 *     -outputs: nothing, but "this" and the inputs will have been updated
 *     2. equals() 
 *     -description: to be equal, 2 Edges don't have to be the same object in Java memory.
 *     -inputs: another edge
 *     -outputs: true if this and the input equal
 *     3. toEquals()
 *     -description: use if the node that this edge points to has null name
 *     -inputs: another edge
 *     -outputs: true if this edge and the thing it points to is equal
 *     4. fromEquals()
 *     -description: use if the node that this edge points from has null name
 *     -inputs: another edge
 *     -outputs: true if this edge and the thing it points from is equal
 
 */


public class Edge {
	
	String label;
	Node to;
	Node from;

	public Edge() {
		label = "i";
	}
	
	//Copy constructor
	// creates an edge identical to input,
	// but pointers are different
	//
	// is this the correct way to make these?
	public Edge(Edge toCopy) {
		label = toCopy.label;
		Node oldTo = toCopy.to;
		Node oldFrom = toCopy.from;
		connect(oldTo, oldFrom);
		to = oldTo;
		from = oldFrom;
	}

	public Edge(String label) {
		this.label = label;
	}

	public void connect(Node to, Node from) {
		this.to = to;
		this.from = from;
		to.edgesToHere.add(this);
		from.edgesFromHere.add(this);
	}

	public Boolean equals(Edge edge) {
			return label.equals(edge.label)
			&& to.equals(edge.to)
			&& from.equals(edge.from);		
	}
	
	//when we know *from* is a null name node so
	// checking it would cause an endless loop
	public Boolean toEquals(Edge edge) {
		return label.equals(edge.label)
		&& to.equals(edge.to);
	}
	
	//when we know *to* is a null name node so
	// checking it would cause an endless loop
	public Boolean fromEquals(Edge edge) {
		return label.equals(edge.label)
		&& from.equals(edge.from);
	}
	
	public String toString() {
		return "from " + from.name + " to " + to.name +
		" of type " + label;
	}
}
