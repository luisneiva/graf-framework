package model.graphTransformer;

import model.Edge;
import model.Graph;

/** 
 * Represents a pairing of a variable edge
 * and a constant edge.
 *   
 * This class is used differently from NodeVariableAssignment.
 * Dealg means at first edge var assignments will be made
 * as node variable assignments. After realg, the node var
 * assignments that are actually for edges will be translated
 * into objects of this class.
 *
 * @author Oscar Wood 
 */


public class EdgeVariableAssignment extends VariableAssignment {

	Edge ruleEdge;
	public Edge inputEdge;
	public final String originalVariable;

	String constantToUpdate;
	private Boolean needsUpdate;

	/*public EdgeVariableAssignment(Edge ruleEdge, Edge inputEdge) {
		this.ruleEdge = ruleEdge;
		this.inputEdge = inputEdge;
		originalVariable = ruleEdge.label;
		needsUpdate = false;
	}*/

	public EdgeVariableAssignment(String var, String constant) {

		String varName = var;
		String consName = constant;

		Integer ruleEndIndex = varName.indexOf("#");
		varName = varName.substring(0, ruleEndIndex);

		Integer inputEndIndex = consName.indexOf("#");
		consName = consName.substring(0, inputEndIndex);

		constantToUpdate = consName;
		originalVariable = varName;

		needsUpdate = true;
	}

	/*public EdgeVariableAssignment(Node var, Node constant) {
		if(!var.getName().contains("#")
				|| !var.getName().contains("?")
				|| !constant.getName().contains("#")
				|| var.getFrom().size() != 2
				|| var.getTo().size() != 0
				|| constant.getFrom().size() != 2
				|| constant.getTo().size() != 0) {
		//	System.out.println(var.getName() + " " + constant.getName());
			System.out.println("error input in edge variable assignment");
		}
		String ruleEdgeName = var.getName();
		String inputEdgeName = constant.getName();

		Integer ruleEndIndex = ruleEdgeName.indexOf("#");
		ruleEdgeName = ruleEdgeName.substring(0, ruleEndIndex);

		Integer inputEndIndex = inputEdgeName.indexOf("#");
		inputEdgeName = inputEdgeName.substring(0, inputEndIndex);

		originalVariable = ruleEdgeName;
		constantToUpdate = inputEdgeName;
		needsUpdate = false;
	}*/


	public String toString() {
		try {
			if(ruleEdge.getLabel().equals(inputEdge.getLabel())) {
				return "(edge) "+
				originalVariable + " := " + inputEdge.getLabel();
			} else {
				return "(edge) "+
				ruleEdge.getLabel() + " =?= " + inputEdge.getLabel();
			}
		} catch(NullPointerException npe) {
			return "error: call update() on this edge assignment ";
		}
	}

	public Boolean getNeedsUpdate() {
		return needsUpdate;
	}

	/**
	 * Construction of this class takes place two in steps. 
	 * The constructor gets the string names of the edges 
	 * (before realg) and this function gets the object 
	 * references (after realg).
	 * 
	 * @param before The before graph that ruleEdge appears in, realged. 
	 * @param input The graph that inputEdge appears in, realged
	 */
	public void update(Graph before, Graph input) {
		needsUpdate = false;

		for(Edge e : before) {
			if(e.getLabel().equals(originalVariable)) {
				ruleEdge = e;
				break;
			}
		}

		for(Edge e : input) {
			if(e.getLabel().equals(constantToUpdate)) {
				inputEdge = e;
				break;
			}
		}

		if(ruleEdge == null 
				|| inputEdge == null) {
			System.out.println("error couldn't update var edge");
		}
	}
}
