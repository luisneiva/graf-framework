package model.graphTransformer;

import java.util.ArrayList;

/** 
 *   A pattern match is a mapping of constants onto variables.
 *   It is a list of variable assignments.
 *   Given a before graph, an input graph and their
 *   pattern match, you can eliminate the variables
 *   in before.
 *   One pair of input and before can have multiple pattern
 *   matches.
 *   To create a pattern match you need a before and an input.
 *   Creation of objects of this class is done in Rule.java.
 *
 *    @author Oscar Wood 
 *
 */

public class PatternMatch extends ArrayList<VariableAssignment> {
	
	public String toString() {
		String str = "";
		for(VariableAssignment va : this) {
			str += "\n" + va;
		}
		return str;
	}
	
	/*public boolean equals(Object o) {
		if(!(o instanceof PatternMatch)) {
			return false;
		}
		
		PatternMatch other = (PatternMatch) o;
		
		if(other.size() != size()) {
			return false;
		}
		
		for(VariableAssignment va : this) {
			Boolean match = false;
			for(VariableAssignment otherva : other) {
				if(otherva.equals(va)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}
		
		for(VariableAssignment otherva : other) {
			Boolean match = false;
			for(VariableAssignment va : this) {
				if(otherva.equals(va)) {
					match = true;
					break;
				}
			}
			if(!match) {
				return false;
			}
		}
		
		return true;
	}*/

}
