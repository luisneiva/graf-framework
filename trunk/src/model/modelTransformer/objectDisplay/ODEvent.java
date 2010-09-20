package model.modelTransformer.objectDisplay;

/** 
 * An event that, in a state machine, moves an object from one state to another.
 * 
 * @author Oscar Wood 
 */

import model.ListGraph;
import agg.xt_basis.Node;

public class ODEvent {

	private String name;

	/** the name of the node that instantiates SignalOccurence
	 * that is in the pool of the object. Not needed for external events
	 */
	private String occurence;

	ODEvent(Node eventNode, Node occurenceNode) {
		name = ListGraph.getName(eventNode);
		if(occurenceNode != null) {
			occurence = ListGraph.getName(occurenceNode);
		}
	}

	public String getName() {
		return name;
	}

	public String getOccurence() {
		return occurence;
	}
}
