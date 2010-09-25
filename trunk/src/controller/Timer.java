package controller;

import java.util.ArrayList;

import agg.xt_basis.Arc;
import agg.xt_basis.Node;
import model.ListGraph;
import model.modelTransformer.objectDisplay.ODObject;

public class Timer {

	/** 
	 * current time must be static because a new timer object
	 * is created for each transition and we want to keep timers
	 * going over multiple transitions
	 * 
	 * TODO to make it possible to time multiple objects, we'll have to move this
	 * attribute into ODObject or something
	 */
	private static Integer time;
	
	/**
	 * This is only the ODObjects that have sent timer signals to the actor
	 * and so are waiting for replies 
	 */
	private ArrayList<ODObject> timedObjects;
	
	//TODO add a listener attribute
	
	public Timer(ListGraph graph, ArrayList<ODObject> objects) {
		//TODO set timedObjects
		// timedObjects is the objects which have sent a message to the actor
		
		//TODO set time by looking at the property of the received signal
	}

	/**
	 * Tests a graph to see if there's a actor node acting as a timer which has
	 * received a timer-related event.
	 * 
	 * At the moment we take the naive view (though this can be changed later) 
	 * that a timed event will exist if there's a pool edge going out from an
	 * actor instance.
	 * 
	 * TODO This should also check if there's a switch somewhere to use xtUML
	 * and if that switch is false then this always returns false
	 */
	public static Boolean isTimerNeeded(ListGraph graph) {
		
		// First find the Actor node
		Node actor = null;
		for(Arc edge : graph.getArcsList()) {
			if(ListGraph.getName(edge.getTarget()).equals("Actor")) {
				actor = (Node)edge.getTarget();
				break;
			}
			if(ListGraph.getName(edge.getSource()).equals("Actor")) {
				actor = (Node)edge.getSource();
				break;
			}
		}
		
		ArrayList<String> fromInstances = new ArrayList<String>();
		fromInstances.add("i");
		
		ArrayList<Node> actorInstances = ListGraph.fromTrace(fromInstances, actor);
		
		ArrayList<String> checkPool = new ArrayList<String>();
		checkPool.add("pool");
		
		for(Node actorInstance : actorInstances) {
			if(ListGraph.toTrace(checkPool, actorInstance).size() > 0) {
				return true;
			}
		}
		
		return false;
	}
}
