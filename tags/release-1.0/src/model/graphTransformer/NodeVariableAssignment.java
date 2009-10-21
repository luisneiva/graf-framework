package model.graphTransformer;

import model.Edge;
import model.Graph;
import model.Node;


/** 
 * A grouping of a variable node and a constant node.
 *
 * The existence of an object of this class means either:
 *   	-the variable might equal the constant 
 *   		and we're checking it or
 *   	-the variable has been assigned to 
 *          the constant, and this keeps track of assignments
 * It is the former if originalVariable equals ruleNode.getName().
 * Otherwise it's the latter.
 *
 *	@author Oscar Wood 
 *   
 */

public class NodeVariableAssignment extends VariableAssignment {
	// Node that is in the rule.
	// Was a variable, but may have been changed into a constant.
	Node ruleNode;

	// The constant node in the input.
	public Node inputNode;

	// The name of ruleNode the variable before it was changed
	// into inputNode.
	final public String originalVariable;

	public NodeVariableAssignment(Node ruleNode, Node inputNode) {
		this.ruleNode = ruleNode;
		this.inputNode = inputNode;
		originalVariable = ruleNode.getName();
	}

	public String toString() {
		if(ruleNode.getName().equals(inputNode.getName())) {
			return "(node) "+originalVariable + " := " + inputNode.getName();
		}
		else {
			return ruleNode.getName() + " =?= " + inputNode.getName();
		}
	}

	/**
	 * Tests whether the nodes could be equivalent according to 
	 * the edges directly connected to them.
	 *   
	 * @param The graph that inputNode appears in.
	 * @param The before graph that ruleNode appears in.
	 * @return True if the assignments is possible.
	 */
	public Boolean verifyEdges(Graph input, Graph before) {

		input.resetCheck();
		before.resetCheck();

		if(ruleNode.getTo().size() > inputNode.getTo().size()) {
			//System.out.println("1 " + toString());			
			return false;
		}

		if(ruleNode.getFrom().size() > inputNode.getFrom().size()) { 
			//System.out.println("2 " + toString());	
			return false;
		}

		for(Edge rEdge : ruleNode.getFrom()) {

			// Make sure they have the same nodes on the ends
			// of the edges (or at least variables that could
			// equal).
			Boolean match = false;
			for(Edge inEdge : inputNode.getFrom()) {				
				if(inEdge.getLabel().equals(rEdge.getLabel())) {
					String inTo = inEdge.getToName();
					String rTo = rEdge.getToName();

					if(inTo.contains("#")) {
						Integer endIndex = inTo.indexOf('#');
						inTo = inTo.substring(0, endIndex);
						//System.out.println(inEdge.toName + " -> " + inTo);
					}

					if(rTo.contains("#")) {
						Integer endIndex = rTo.indexOf('#');
						rTo = rTo.substring(0, endIndex);
						//System.out.println(rEdge.toName + " -> " + rTo);
					}

					if(inTo.equals(rTo)) {
						//	System.out.println("5"+toString());

						match = true;
						break;
					}
					else if(rEdge.toIsVariable) {
						//	System.out.println("6"+toString());

						match = checkSize(rEdge, inEdge, true);
						input.resetCheck();
						before.resetCheck();
						if(match) {
							break;
						}
					}
				}
			}
			if(!match) {
				//System.out.println("3 " + toString());	
				return false;
			}
		} 

		for(Edge rEdge : ruleNode.getTo()) {
			Boolean match = false;
			for(Edge inEdge : inputNode.getTo()) {
				if(inEdge.getLabel().equals(rEdge.getLabel())) {
					String inFrom = inEdge.getFromName();
					String rFrom = rEdge.getFromName();

					if(inFrom.contains("#")) {
						Integer endIndex = inFrom.indexOf('#');
						inFrom = inFrom.substring(0, endIndex);
						//System.out.println(inEdge.fromName + " -> " + inFrom);
					}

					if(rFrom.contains("#")) {
						Integer endIndex = rFrom.indexOf('#');
						rFrom = rFrom.substring(0, endIndex);
						//System.out.println(rEdge.fromName + " -> " + rFrom);
					}

					if(inFrom.equals(rFrom)) {
						match = true;
						break;
					} else if(rEdge.fromIsVariable) {	
						match = checkSize(rEdge, inEdge, false);
						input.resetCheck();
						before.resetCheck();
						if(match) {
							break;
						}
					}
				}
			}
			if(!match) {
				//System.out.println("4 " + toString());	
				return false;
			}
		}
		return true;
	}

	private Boolean checkSize(Edge ruleEdge, Edge inputEdge, Boolean checkToNotFrom) {

		Integer ruleSize;
		Integer inputSize;
		if(checkToNotFrom) {
			ruleEdge.getFrom().check();
			inputEdge.getFrom().check();
			ruleSize = ruleEdge.getTo().getSize();
			inputSize = inputEdge.getTo().getSize();
			//	System.out.println("!" + ruleEdge.toName + " " + ruleSize);
			//	System.out.println("!" + inputEdge.toName + " " + inputSize);
		} else {
			ruleEdge.getTo().check();
			inputEdge.getTo().check();
			ruleSize = ruleEdge.getFrom().getSize();
			inputSize = inputEdge.getFrom().getSize();
			//	System.out.println("!!" + ruleEdge.fromName + " " + ruleSize);
			//	System.out.println("!!" + inputEdge.fromName + " " + inputSize);
		}

		//System.out.println(toString() + " " + inputSize + " >= " + ruleSize);
		return inputSize >= ruleSize;
	}

	/**
	 * Tests equality based on the original variable. It makes no 
	 * difference whether ruleNode has been changed into a constant
	 * or not.
	 */
	public boolean equals(Object o) {
		if(!(o instanceof NodeVariableAssignment)) {
			return false;
		}

		NodeVariableAssignment other = (NodeVariableAssignment) o;

		return other.originalVariable.equals(originalVariable)
		&& other.inputNode.getName().equals(inputNode.getName());
	}

}