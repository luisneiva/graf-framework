package model.modelTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.ListGraph;
import model.exceptions.GraphToModelException;
import model.modelTransformer.objectDisplay.DisplayObject;
import model.modelTransformer.objectDisplay.ODClass;
import model.modelTransformer.objectDisplay.ODLink;
import model.modelTransformer.objectDisplay.ODObject;
import model.modelTransformer.objectDisplay.ObjDiag;
import model.modelTransformer.objectDisplay.ObjectDisplay;

import org.eclipse.swt.graphics.Point;

import controller.GrafTimer;
import controller.Properties;

import agg.xt_basis.Arc;
import agg.xt_basis.Node;

public class GraphToObjDiag implements GraphToModel {

	/** Object diagram representation of graph */
	private ObjDiag objdiag;

	public void reset() {
		objdiag = new ObjDiag();
		ODObject.resetRandomLocator();
	}

	public ObjectDisplay generateDisplayObjects(ListGraph graph) throws GraphToModelException {

		long time = System.currentTimeMillis();

		ArrayList<DisplayObject> objects = objdiag.getODObjs();
		ArrayList<ODLink> odLinks = objdiag.getODLinks();

		// Get the names of objects not in event mode, current locations, and hide/show values
		HashMap<String,Point> locations = new HashMap<String,Point>();
		HashMap<String,Boolean[]> hideshow = new HashMap<String,Boolean[]>();
		if(objects != null) {
			if (objects.size()>0 && !(objects.get(0) instanceof ODObject)) {
				throw new GraphToModelException("Found object that is not an ODObject");
			}
			for(DisplayObject object : objects) {
				ODObject odObj = (ODObject)object;
				locations.put(odObj.getName(), odObj.getLocation());
				hideshow.put(odObj.getName(), new Boolean[]{
					odObj.isAttributesShowing(),odObj.isRuntimePoolShowing()});
			}
		}
		
		objdiag = generateObjectDiagram(graph);   

		objects = objdiag.getODObjs();
		odLinks = objdiag.getODLinks();

		//Re-set remembered object properties
		for(DisplayObject object : objects) {
			ODObject odObj = (ODObject)object;
			if (locations.containsKey(odObj.getName())) {
				odObj.setLocation(locations.get(odObj.getName()));
			}
			if (hideshow.containsKey(odObj.getName())) {
				odObj.setAttributesShowing(hideshow.get(odObj.getName())[0]);
				odObj.setRuntimePoolShowing(hideshow.get(odObj.getName())[1]);
			}
		}

		objdiag.setObjs(objects);
		objdiag.setODLinks(odLinks);
		long time1 = System.currentTimeMillis();
		long totalTime = time1 - time;
		Boolean printTiming = Boolean.parseBoolean(Properties.getProperty("PrintTiming"));
		if(printTiming) {
			System.out.println("Total model loading time: " + totalTime + " milliseconds\n");
		}
		return objdiag;
	}
	

	private ObjDiag generateObjectDiagram(ListGraph graph) throws GraphToModelException {

		ArrayList<ODObject> objects = new ArrayList<ODObject>();

		// First find the Class node
		Node classNode = null;
		for(Arc edge : graph.getArcsList()) {
			if(ListGraph.getName(edge.getTarget()).equals("Class")) {
				classNode = (Node)edge.getTarget();
				break;
			}
			if(ListGraph.getName(edge.getSource()).equals("Class")) {
				classNode = (Node)edge.getSource();
				break;
			}
		}

		if(classNode == null) {
			throw new GraphToModelException("the graph has no class node");
		}

		// Find the classes.
		ArrayList<ODClass> classes = new ArrayList<ODClass>();
		for(Arc edgeToClass : classNode.getIncomingArcsVec()) {
			if(ListGraph.getName(edgeToClass).equals("i")) {
				Node node = (Node)edgeToClass.getSource();				
				ODClass clas = new ODClass(node);
				classes.add(clas);
			}
		}

		// Find the objects of the classes.
		for(ODClass clas : classes) {
			Node node = clas.getGraphNode();
			for(Arc edgeToClass : node.getIncomingArcsVec()) {
				if(ListGraph.getName(edgeToClass).equals("i")) {
					Node objNode = (Node)edgeToClass.getSource();
					ODObject object = 
						new ODObject(clas, objNode, true);
					objects.add(object);
				}
			}
		}

		// Find the attributes for each class.
		for(ODClass clas : classes) {
			Node node = clas.getGraphNode();
			for(Arc edgeFromClass : node.getOutgoingArcsVec()) {				
				if(ListGraph.getName(edgeFromClass).equals("ownedAttribute")) {
					Node attributeName = (Node)edgeFromClass.getTarget();
					clas.addAttributeName(ListGraph.getName(attributeName));
				}
			}
		}

		// Copy attributes from classes into instantiating objects
		for(ODObject obj : objects) {
			obj.setAttributeNames();
		}

		ArrayList<Object[]> possibleAssociations = new ArrayList<Object[]>();
		for(ODObject obj : objects) {
			Node node = obj.getGraphNode();
			for(Arc edgeFromAttr : node.getOutgoingArcsVec()) {

				if(!ListGraph.getName(edgeFromAttr).equals("i")) {
					String attrValue = ListGraph.getName(edgeFromAttr.getTarget());
					String attrName = ListGraph.getName(edgeFromAttr);

					//This could be either a attribute or an association					
					Boolean wasAttribute = obj.setAttributeValue(attrName, attrValue);

					if(!wasAttribute) {
						Object[] possibility = {edgeFromAttr.getTarget(), attrName, obj};
						possibleAssociations.add(possibility);
					}
				}				
			}
		}

		// Deal with the possible associations found when looking for attributes.
		ArrayList<ODLink> associations = new ArrayList<ODLink>();
		for(Object[] assoc : possibleAssociations) {

			Node leftNode = (Node) assoc[0];
			String leftLabel = (String) assoc[1];
			ODObject right = (ODObject) assoc[2];

			ODObject left = null;

			for(ODObject obj : objects) {
				if(obj.getGraphNode().equals(leftNode)) {
					left = obj;
					break;
				}
			} 

			//Check if the object name is the name of any already found object.
			for(ODObject obj : objects) {
				if(obj.getName().equals(ListGraph.getName(leftNode))) {
					associations.add(new ODLink(left,leftLabel,right));
				}
			}
		}

		if(associations.size() > 1) {
			for(int n = 0; n < associations.size(); n++) {
				associations.get(n).combine(associations);
			}
		}

		// Follow execution and activeState edges to find the current state.
		for(ODObject object : objects) {
			Node node = object.getGraphNode();

			ArrayList<String> toState = new ArrayList<String>();
			toState.add("execution");
			toState.add("activeState");
			ArrayList<Node> states = ListGraph.toTrace(toState,node);

			if (states.size() != 0) {
				for(Node n : states) {
					object.addState(n);
				}
			}
		}

		// Collect actions - collect the executable actions from the executing behaviour
		for (ODObject object : objects) {

			if(object.getGraphNode() != null) {

				// actionBehExes solves google code defect 1
				ArrayList<Node> actionBehExes = new ArrayList<Node>();
				
				ArrayList<String> traceOrderPartOne = new ArrayList<String>();
				traceOrderPartOne.add("execution");
				ArrayList<Node> potentialActionBehExes = ListGraph.toTrace(traceOrderPartOne, object.getGraphNode());
				
				// All we're doing here is looking behaviour execution node
				// that does not have an active state edge
				for(Node potentialActionBehEx : potentialActionBehExes) {
					Boolean thisIsIt = true;
					for(Arc arc : potentialActionBehEx.getOutgoingArcsVec()) {
						if(ListGraph.getName(arc).equals("activeState")) { //not the best way to contrast the 2 types of behavior executions, but it works
							thisIsIt = false;
							break;
						}
					}
					if(thisIsIt) {
						actionBehExes.add(potentialActionBehEx);
					}
				}
				
				ArrayList<String> traceOrderPartTwo = new ArrayList<String>();
				traceOrderPartTwo.add("execution");
				traceOrderPartTwo.add("executable");
				ArrayList<Node> executableActions = ListGraph.toTrace(traceOrderPartTwo,object.getGraphNode());
				for (Node action : executableActions) {
					object.addAction(action, actionBehExes, graph);
				}
			}
		}
		
		// Find the methods
		for (ODObject object : objects) {
			
			if (object.getGraphNode() != null) {
				
				// Get the type node of the object (there should only be one)
				ArrayList<String> traceToType = new ArrayList<String>();
				traceToType.add("i");
				Node objectTypeNode = ListGraph.toTrace(traceToType, object.getGraphNode()).get(0);
				
				// This sequence of edges leads from a class/'type' to a method node (if there is one)
				ArrayList<String> traceToMethod = new ArrayList<String>();
				traceToMethod.add("ownedOperation");
				traceToMethod.add("method");
				//traceToMethod.add("node");
				
				// Trace from the object's class node to any potential methods
				ArrayList<Node> foundMethods = ListGraph.toTrace(traceToMethod, objectTypeNode);
				
				for (Node method : foundMethods) {
					object.addMethod(method);
				}
			}
		}

		// Find the events
		ArrayList<String> eventOrder = new ArrayList<String>();
		eventOrder.add("source");

		for(ODObject object : objects) {
			
			// Find the calls first
			ArrayList<String> toExternalCalls = new ArrayList<String>();
			toExternalCalls.add("pool");
			ArrayList<Node> externalCalls = ListGraph.toTrace(toExternalCalls, object.getGraphNode());
			for(Node call : externalCalls) {
				ArrayList<String> toType = new ArrayList<String>();
				toType.add("i");
				for(Node callType : ListGraph.toTrace(toType, call)) {
					if(ListGraph.getName(callType).equals("CallEvent")) {
						object.addCall(call);
					}
				}
			}
			
			ArrayList<String> mainRegionTraceRoute = new ArrayList<String>();
			
			ArrayList<String> toTopRegion = new ArrayList<String>();
			ArrayList<String> toSubRegion = new ArrayList<String>();
			ArrayList<String> toEvent = new ArrayList<String>();
						
			toTopRegion.add("execution");
			toTopRegion.add("behavior");
			toTopRegion.add("region");
						
			toSubRegion.add("subvertex");
			toSubRegion.add("region");
						
			toEvent.add("transition");
			toEvent.add("trigger");
			toEvent.add("event");
						
			mainRegionTraceRoute.addAll(toTopRegion);
			
			ArrayList<Node> mainRegionTrace = ListGraph.toTrace(mainRegionTraceRoute, object.getGraphNode());
			
			ArrayList<Node> regionTrace = new ArrayList<Node>();
			ArrayList<Node> externalEvents = new ArrayList<Node>();

			// loop down to all sub-regions when there are multiple regions
			while(mainRegionTrace.size() > 0) {
				for(Node node : mainRegionTrace) {
					regionTrace = ListGraph.toTrace(toSubRegion, node);
					externalEvents.addAll(ListGraph.toTrace(toEvent, node));
				}
				mainRegionTrace = regionTrace;
			}
			
			Iterator<Node> duplicateRemover = externalEvents.iterator();
			ArrayList<String> found = new ArrayList<String>();
			while(duplicateRemover.hasNext()) {
				String current = ListGraph.getName(duplicateRemover.next());
				if(found.contains(current))
					duplicateRemover.remove();
				else
					found.add(current);
			}

			for(Node node : externalEvents) {
				object.addExternalEvent(node);
			}

			ArrayList<String> toReciept = new ArrayList<String>();
			toReciept.add("pool");

			ArrayList<String> fromRecieptToEvent = new ArrayList<String>();
			fromRecieptToEvent.add("message");
			fromRecieptToEvent.add("i");

			ArrayList<String> fromSignalToReceive = new ArrayList<String>();
			fromSignalToReceive.add("signal");

			ArrayList<Node> recieptNodes = ListGraph.toTrace(toReciept, object.getGraphNode());
			for(Node recieptNode : recieptNodes) {
				
				ArrayList<Node> activeEventSignals 
				= ListGraph.toTrace(fromRecieptToEvent, recieptNode);

				ArrayList<Node> activeEvents = new ArrayList<Node>();
				for(Node n : activeEventSignals) {
					ArrayList<Node> all = ListGraph.fromTrace(fromSignalToReceive, n);
					activeEvents.addAll(all);
				}

				ArrayList<String> check = new ArrayList<String>();
				check.add("i");
				for(Node evNode : activeEvents) {

					Node type = ListGraph.toTrace(check,evNode).get(0);
					if(ListGraph.getName(type).equals("ReceiveSignalEvent")) {
						object.addEvent(evNode, recieptNode);
					}
				}
			}
		}

		// Set modes - if contains (executable) actions then in action mode
		for(ODObject object : objects) {
			if (object.getActionPool().size()==0) {
				object.setEventMode(true);
			} else {
				object.setEventMode(false);
			}
		}

		ObjDiag result;
		
		if(GrafTimer.isTimerNeeded(graph)) {
			result = new ObjDiag(objects, associations, new GrafTimer(graph, objects, objdiag));  // TODO
		}
		else {
			result = new ObjDiag(objects, associations);
		}
		return result;
	}
}
