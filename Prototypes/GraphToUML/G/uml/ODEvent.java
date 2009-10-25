package uml;

/* 
 *   ODEvent
 *   An event that, in a state machine, moves an object from one state to another.
 *   
 *   The active attribute refers to whether the event can be accepted.
 *   This means that it has actually been sent.
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
 *   Contents of ODEvent
 *
 */

import graph.Node;

import java.util.ArrayList;

public class ODEvent {

	private Boolean active;
	private String name;
	private String type;
	
	public ODEvent(Node transNode, Node objectNode) {
		name = transNode.getName();
		
		active = false;
		
		ArrayList<String> actionOrder = new ArrayList<String>();
		actionOrder.add("trigger");
		actionOrder.add("event");
		actionOrder.add("signal");
		
		Node actionEnd = transNode.toTrace(actionOrder);
		
		ArrayList<String> poolOrder = new ArrayList<String>();
		poolOrder.add("pool");
		poolOrder.add("message");
		poolOrder.add("i");
		
		Node farEnd = objectNode.toTrace(poolOrder);
		
		
		active = farEnd != null
				&& actionEnd != null
				&& farEnd.getName().equals(actionEnd.getName());

		ArrayList<String> typeOrder = new ArrayList<String>();
		typeOrder.add("pool");
		typeOrder.add("i");
		Node typeNode = objectNode.toTrace(typeOrder);
		if(typeNode != null) {
			type = typeNode.getName();
		}
		else {
			type = "((this event isn't in the pool))";
		}

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
