package model.objectDiagram;

/** 
 * An event that, in a state machine, moves an object from one state to another.
 * 
 * @author Oscar Wood 
 *
 *
 */

import model.Node;

public class ODEvent {

	private String name;

	/** the name of the node that instantiates SignalOccurence
	 * that is in the pool of the object. Not needed for external events
	 */
	private String occurence;

	ODEvent(Node eventNode, Node occurenceNode) {
		name = eventNode.getName();
		if(occurenceNode != null) {
			occurence = occurenceNode.getName();
		}
		//System.out.println(name + " for " + occurence);
	}

	public String getName() {
		return name;
	}

	public String getOccurence() {
		return occurence;
	}
}
