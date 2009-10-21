package model.objectDiagram;

/** 
 *   An action that an object with a state machine executes after it changes state.
 *
 *   The active attribute refers to whether the action can be done.
 *   This means that it has the input pins satisfied and/or
 *   it fits the before image of a rule.
 *   If active then in the GUI you should be able to click on it.
 *   If not active then in the GUI it will be greyed out or invisible.
 *   I don't know how to determine the value.
 *
 *	 @author Oscar Wood 
 *
 */

import java.util.ArrayList;

import model.Edge;
import model.Node;

public class ODAction {

	private Boolean active;
	private String name;
	private String type;
	
	ODAction(Node node) {
		name = node.getName();
		active = false;
		
		for(Edge fromEdge : node.getFrom()) {
			if(fromEdge.getLabel().equals("i")) {
				type = fromEdge.getToName();
				break;
			}
		}
		
		ArrayList<String> traceOrder = new ArrayList<String>();
		// TODO replace "activated with the name of the edge we'll use
		traceOrder.add("activated");
		active = node.fromTrace(traceOrder).size() > 0;
	}

	public String getName() {
		return name;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public String getType() {
		return type;
	}
}
