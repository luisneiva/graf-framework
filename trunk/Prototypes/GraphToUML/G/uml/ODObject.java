package uml;
/* 
 *   ODObject
 *   An object in the object diagram. Note that the state attribute
 *   will be null in objects of classes without state machines.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of ODObject
 *   
 *     1. setAttributeNames()
 *     -description: Assuming the class object that this object object instantiates
 *     				 has been initialised, this method copies the attribute names 
 *     				 of the class object to this object. It leaves the values of those
 *     				 attributes blank.
 *     -inputs: None.
 *     -outputs: None.
 *     
 *     2. setAttributeValue()
 *     -description: Finds the attribute with the given name and sets it to the given value.
 *     				 However, if there is no attribute with that name then we ignore it 
 *     				 because we assume that it is some other part of the graph other
 *     				 than an attribute.
 *     -inputs: The name of the attribute and the value to set it to.
 *     -outputs: None.
 *     
 *     3. addAction()
 *     -description: Creates an ODAction based on a node and adds it to this.
 *     -inputs: The node.
 *     -outputs: None.
 *     
 *     4. addEvent()
 *     -description: Creates an OEvent based on a node and adds it to this.
 *     -inputs: The node.
 *     -outputs: None.
 *
 */




import graph.Node;

import java.util.ArrayList;


public class ODObject {

	private String name;

	private ODClass instantiation;

	private ArrayList<ODAttribute> attributes;

	private Node graphNode;

	private Node stateNode;

	private ArrayList<ODAction> actionPool;
	private ArrayList<ODEvent> eventPool;
	private Boolean eventMode;


	public ODObject(ODClass instantiation, Node graphNode, Boolean eventMode) {
		this.eventMode = eventMode;

		this.instantiation = instantiation;
		attributes = new ArrayList<ODAttribute>();
		this.graphNode = graphNode;
		name = graphNode.getName();
		stateNode = null;
		actionPool = new ArrayList<ODAction>();
		eventPool = new ArrayList<ODEvent>();
	}

	public ArrayList<ODAttribute> getAttributes() {
		return attributes;
	}

	public ODClass getTheClass() {
		return instantiation;
	}
	public ArrayList<ODEvent> getEventPool() {
		return eventPool;
	}
	public ArrayList<ODAction> getActionPool() {
		return actionPool;
	}

	public Node getGraphNode() {
		return graphNode;
	}

	public String toString() {
		String str = name + " : " + 
		instantiation.getName() + "\n" + stateNode.getName() +"\n----------";
		for(ODAttribute attr : attributes) {
			str += "\n  " + attr;
		}
		str += "\n----------";
		for(ODEvent event : eventPool) {
			str += "\n  Event: " + event.getName();
			if(event.getActive()) {
				str += " (selectable)";
			}
			else {
				str += " (can't be selected)";
			}
			if(!eventMode) {
				str += "(hidden)";
			}
		}
		str += "\n----------";
		for(ODAction action : actionPool) {
			str += "\n  Action: " + action.getName();
			if(action.getActive()) {
				str += " (can be selected)";
			}
			else {
				str += " (can't be selected)";
			}
			if(eventMode) {
				str += " (hidden)";
			}
		}
		str += "\n----------";

		return str;		
	}

	public String getName() {
		return name;
	}

	public void setAttributeNames() {
		for(String name : instantiation.getAttributeNames()) {
			attributes.add(new ODAttribute(name));
		}
	}

	public Boolean setAttributeValue(String attrName, String attrValue) {
		for(ODAttribute attr : attributes) {
			if(attr.unset() && attr.getName().equals(attrName)) {
				attr.setValue(attrValue);
				return true;
			}
		}
		return false;
	}

	public void setState(Node stateNode) {
		this.stateNode = stateNode;
	}

	public Node getState() {
		return stateNode;
	}

	public void addAction(Node node) {
		ODAction action = new ODAction(node);
		actionPool.add(action);		
	}
	
	public void addEvent(Node transNode, Node objectNode) {
		ODEvent event = new ODEvent(transNode, objectNode);
		eventPool.add(event);
	}
}
