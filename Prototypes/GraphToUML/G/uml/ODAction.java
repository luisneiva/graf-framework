package uml;

/* 
 *   ODAction
 *   An action that an object with a state machine executes after it changes state.
 *
 *   The active attribute refers to whether the action can be done.
 *   This means that it has the input pins satisfied and/or
 *   it fits the before image of a rule.
 *   If active then in the GUI you should be able to click on it.
 *   If not active then in the GUI it will be greyed out or invisible.
 *   I don't know how to determine the value.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of ODAction
 *
 */

import graph.Node;

public class ODAction {

	private Boolean active;
	private String name;
	
	public ODAction(Node node) {
		name = node.getName();
		active = false;
		
		//TODO navigate neighbour nodes or test apply rule or something
		// in order to determine if this is active or not
	}

	public String getName() {
		return name;
	}
	
	public Boolean getActive() {
		return active;
	}
}
