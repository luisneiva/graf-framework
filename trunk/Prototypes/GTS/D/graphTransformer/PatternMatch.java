package graphTransformer;

import java.util.ArrayList;

/* 
 *   PatternMatch
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
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of PatternMatch
 *
 *     1. equals() 
 *     -description: Two matches are equal if they have the same
 *     				 number of pattern matches and all of these
 *     				 are equal.
 *     -inputs: Another pattern match.
 *     -output: True if they have exactly the same edges.
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
