package graphTransformer;


/* 
 *   Edge
 *   This class represents a line between graphs.
 *    
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of Edge
 *
 *     1. Edge() 
 *     -description: Standard constructor. Note that to make
 *     				 an edge a variable, make the first 
 *     				 character of the node's name a question
 *     				 mark. For example Edge("State","?state")
 *     				 will have a variable edge called state.
 *     -inputs: Attributes.
 *     -output: Created edge.
 *
 *	   2. equals()
 *	   -description: 2 edges are equal if they have the same
 *					 node names and edge labels. However, if 
 *					 one of the compared nodes has a variable
 *					 node, that will always be considered equal
 *					 to the equivalent node.
 *	   -inputs: And edge to compare to this.
 *	   -outputs: True if they are equal.
 *
 *	   3. applyPatterMatch()
 *	   -description: Replaces variable nodes with constants
 *					 according to the input pattern.
 *     -inputs: A list of variable assignments.
 *     -outputs: None.
 */

public class Edge {

	private Node to;
	private Node from;
	private String label;
	
	private String toName;//deliberate redundancy
	private String fromName;// Node to, from also contain these fields

	Boolean toIsVariable;
	Boolean fromIsVariable;
	Boolean edgeIsVariable;

	//Used in checking for subgraph by Rule
	Boolean checked;

	public Edge(String toName, String fromName, String label, Graph graph) {
		this.toName = toName;
		this.fromName = fromName;
		this.label = label;

		toIsVariable = toName.charAt(0) == '?';
		fromIsVariable = fromName.charAt(0) == '?';
		edgeIsVariable = label.charAt(0) == '?';

		checked = false;

		from = graph.getNodeWithName(fromName);
		to = graph.getNodeWithName(toName);

		if(from == null) {
			from = new Node(fromName);
		}
		if(to == null) {
			to = new Node(toName);
		}

		to.getTo().add(this);
		from.getFrom().add(this);
	}

	public Edge(String toName, String fromName, Graph graph) {
		this.toName = toName;
		this.fromName = fromName;
		label = "i";

		toIsVariable = toName.charAt(0) == '?';
		fromIsVariable = fromName.charAt(0) == '?';
		edgeIsVariable = label.charAt(0) == '?';

		checked = false;

		from = graph.getNodeWithName(fromName);
		to = graph.getNodeWithName(toName);

		if(from == null) {
			from = new Node(fromName);
		}
		if(to == null) {
			to = new Node(toName);
		}

		to.getTo().add(this);
		from.getFrom().add(this);
	}

	public String toString() {
		return fromName + " " + label + " " + toName;
	}

	public boolean equals(Object o) {

		if(!(o instanceof Edge))
			return false;

		Edge e = (Edge) o;

		String thisToName = toName;
		String thisFromName = fromName;
		String otherToName = e.toName;
		String otherFromName = e.fromName;

		if(thisToName.contains("#")) {
			Integer endIndex = thisToName.indexOf('#');
			thisToName = thisToName.substring(0, endIndex);
			//System.out.println(toName + " -> " + thisToName);
		}
		if(thisFromName.contains("#")) {
			Integer endIndex = thisFromName.indexOf('#');
			thisFromName = thisFromName.substring(0, endIndex);
			//System.out.println(fromName + " -> " + thisFromName);
		}
		if(otherToName.contains("#")) {
			Integer endIndex = otherToName.indexOf('#');
			otherToName = otherToName.substring(0, endIndex);
			//System.out.println(e.toName + " -> " + otherToName);
		}
		if(otherFromName.contains("#")) {
			Integer endIndex = otherFromName.indexOf('#');
			otherFromName = otherFromName.substring(0, endIndex);
			//System.out.println(e.fromName + " -> " + otherFromName);
		}

		Boolean toMatch; 
		toMatch = toIsVariable || thisToName.equals(otherToName);

		Boolean fromMatch;
		fromMatch = fromIsVariable || thisFromName.equals(otherFromName);

		return label.equals(e.label)
		&& toMatch
		&& fromMatch;
	}

	public void applyPatternMatch(PatternMatch pattern) {

		if(toIsVariable) {
			for(VariableAssignment varAssign : pattern) {
				if(varAssign instanceof NodeVariableAssignment) {
					NodeVariableAssignment assign = (NodeVariableAssignment) varAssign;
					if(toName.equals(assign.originalVariable)) {
						String newName = assign.inputNode.getName();
						toName = newName;
						
						to.replace(toName, newName);
						
						toIsVariable = newName.charAt(0) == '?';
						break;
					}
				}
			}
		}
		if(fromIsVariable) {
			for(VariableAssignment varAssign : pattern) {
				if(varAssign instanceof NodeVariableAssignment) {
					NodeVariableAssignment assign = (NodeVariableAssignment) varAssign;
					if(fromName.equals(assign.originalVariable)) {
						String newName = assign.inputNode.getName();
						fromName = newName;
						
						from.replace(fromName, newName);
												
						fromIsVariable = newName.charAt(0) == '?';
						break;
					}
				}
			}
		}

		if(edgeIsVariable) {
			for(VariableAssignment va : pattern) {
				if(va instanceof EdgeVariableAssignment) {
					EdgeVariableAssignment eva = (EdgeVariableAssignment) va;

					if(eva.originalVariable.equals(label)) {
						
						String newName = eva.inputEdge.label;
						label = newName;
						
						edgeIsVariable = newName.charAt(0) == '?';
						break;
					}
				}
			}
		}

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
	
	public String getToName() {
		return toName;
	}
	
	public String getFromName() {
		return fromName;
	}
	
	public void setToName(String toName) {
		this.toName = toName;
	}
	
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	/*
	 * the following should absolutely only ever
	 * be called from the graph to uml module, never
	 * from the GTS module
	 */
	
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

}
