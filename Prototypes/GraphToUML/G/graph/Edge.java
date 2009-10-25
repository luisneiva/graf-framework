package graph;

/* 
 *   Edge
 *   A directed connection between 2 nodes.
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
 */


public class Edge {
	
	private Node to;
	private Node from;
	private String label;
	
	public Edge(Node from, String label, Node to) {
		this.from = from;
		this.label = label;
		this.to = to;
		
		to.getTo().add(this);
		from.getFrom().add(this);
	}
	
	public Edge(Node from, Node to) {
		this.from = from;
		label = "i";
		this.to = to;
		
		to.getFrom().add(this);
		to.getTo().add(this);
	}
	
	public Node getTo() {
		return to;
	}
	
	public Node getFrom() {
		return from;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return from.getName() + " " + label + " " + to.getName();
	}

}
