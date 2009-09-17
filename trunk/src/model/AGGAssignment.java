package model;

/**
 * Used for setting input parameters
 */
public class AGGAssignment {

	public String var;
	public String cons;
	
	public AGGAssignment(String variable, String constant) {
		var = variable;
		cons = constant;
	}
	
	public String toString() {
		return var + " := " + cons;
	}
}
