package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;

import agg.xt_basis.Arc;
import agg.xt_basis.Node;
import model.ListGraph;
import model.modelTransformer.objectDisplay.ODObject;
import model.modelTransformer.objectDisplay.ObjDiag;

public class GrafTimer {

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
	
	private Integer timerValue = 0;
	
	private Timer t;
	private Integer delaySec;  // milliseconds between timer ticks  initDelaySec
	private Boolean isFirst = true;
	
	//TODO add a listener attribute
	
	public GrafTimer(ListGraph graph, ArrayList<ODObject> objects, final ObjDiag objdiag) {
		//TODO set timedObjects:
		// timedObjects is the objects which have sent a message to the actor
		
		timedObjects = new ArrayList<ODObject>();
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
		
		ArrayList<String> findObjectRoute = new ArrayList<String>();
		findObjectRoute.add("pool");
		findObjectRoute.add("message");
		findObjectRoute.add("sender");
		
		ArrayList<String> findSetTimerRoute = new ArrayList<String>();
		findSetTimerRoute.add("pool");
		findSetTimerRoute.add("message");
		findSetTimerRoute.add("i");
		findSetTimerRoute.add("ownedAttribute");
		findSetTimerRoute.add("defaultValue");

		// Find objects
		ArrayList<Node> findObjects = new ArrayList<Node>();
		// Find Timer property: setTimer
		ArrayList<Node> findTimerProperty = new ArrayList<Node>();
		
		for(Node actorInstance : actorInstances) {
			findObjects = ListGraph.toTrace(findObjectRoute, actorInstance);
//			System.out.println("Timer: Find an object -> size: " + findObjects.size());
			for (Node objectNode : findObjects) {
//				System.out.println("Find an object -> name: " + ListGraph.getName(objectNode));
				//add object to timedObjects. add to timedObjects the ODObject in the input parameter "objects" that has the same name m1.
				for(ODObject odo : objects) {
					if(odo.getName().equals(ListGraph.getName(objectNode))) {
						timedObjects.add(odo);
						System.out.println("odo's name: " + odo.getName());
					}
				}
			}
			
			findTimerProperty = ListGraph.toTrace(findSetTimerRoute, actorInstance);
			for (Node timerPropNode : findTimerProperty) {
				timerValue = Integer.parseInt(ListGraph.getName(timerPropNode));
//				System.out.println("Find Timer property -> timer value: " + timerValue);
				delaySec = timerValue * 1000;
//				initDelaySec = delaySec;
				System.out.println("\tTimer STARTS:");
				
				class TimerListener implements ActionListener
				{
					ListGraph delayEventGraph;
					public TimerListener(ListGraph g) {
						delayEventGraph = g;
					}
					public void actionPerformed(ActionEvent event) {
						if(!isFirst) {
							System.out.println("ONLY ONCE.");
							for (ODObject odObj : timedObjects) {
								odObj.createDelayEvent(odObj, delayEventGraph);
							}
							t.stop();
							
						}
						else {
							isFirst = false;
						}
					}
				}

				TimerListener timerListener = new TimerListener(graph);
				t = new Timer(delaySec, timerListener);
				t.start();
			}
		}
		
		System.out.println("\tAll OVER.");
		
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
