package model.modelTransformer;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

import model.Edge;
import model.Graph;
import model.Node;
import model.graphTransformer.GraphTransformer;
import model.modelTransformer.objectDisplay.DisplayObject;
import model.modelTransformer.objectDisplay.ODClass;
import model.modelTransformer.objectDisplay.ODLink;
import model.modelTransformer.objectDisplay.ODObject;
import model.modelTransformer.objectDisplay.ObjDiag;
import model.modelTransformer.objectDisplay.ObjectDisplay;

public class GraphToObjDiag implements GraphToModel {
	
	/** Object diagram representation of graph */
	private ObjDiag objdiag;
	
	public void reset() {
		objdiag = new ObjDiag();
    	ODObject.resetRandomLocator();
	}
	
	public ObjectDisplay generateDisplayObjects(Graph graph) {
		
		ArrayList<DisplayObject> objects = objdiag.getODObjs();
		ArrayList<ODLink> odLinks = objdiag.getODLinks();
		
		// Get the names of objects not in event mode, current locations, and hide/show values
		HashMap<String,Point> locations = new HashMap<String,Point>();
		HashMap<String,Boolean[]> hideshow = new HashMap<String,Boolean[]>();
		if(objects != null) {
			if (objects.size()>0 && !(objects.get(0) instanceof ODObject)) {
				return null;					//TODO - change to GraphToModelException
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
		
		//Re-set remembered object propertes
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
		
		objdiag.setODObjs(objects);
		objdiag.setODLinks(odLinks);
		return objdiag;
	}
	
	private ObjDiag generateObjectDiagram(Graph graph) {

		ArrayList<ODObject> objects = new ArrayList<ODObject>();

		// First find the Class node
		Node classNode = null;
		for(Edge edge : graph) {
			if(edge.getTo().getName().equals("Class")) {
				classNode = edge.getTo();
				break;
			}
			if(edge.getFrom().getName().equals("Class")) {
				classNode = edge.getFrom();
				break;
			}
		}

		if(classNode == null) {
			System.out.println("the graph has no class node");
		}

		// Find the classes.
		ArrayList<ODClass> classes = new ArrayList<ODClass>();
		for(Edge edgeToClass : classNode.getTo()) {
			if(edgeToClass.getLabel().equals("i")) {
				Node node = edgeToClass.getFrom();				
				ODClass clas = new ODClass(node);
				classes.add(clas);
			}
		}

		// Find the objects of the classes.
		for(ODClass clas : classes) {
			Node node = clas.getGraphNode();
			for(Edge edgeToClass : node.getTo()) {
				if(edgeToClass.getLabel().equals("i")) {
					Node objNode = edgeToClass.getFrom();
					ODObject object = 
						new ODObject(clas, objNode, true);
					objects.add(object);
				}
			}
		}

		// Find the attributes for each class.
		for(ODClass clas : classes) {
			Node node = clas.getGraphNode();
			for(Edge edgeFromClass : node.getFrom()) {				
				if(edgeFromClass.getLabel().equals("ownedAttribute")) {
					Node attributeName = edgeFromClass.getTo();
					clas.addAttributeName(attributeName.getName());
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
			for(Edge edgeFromAttr : node.getFrom()) {

				if(!edgeFromAttr.getLabel().equals("i")) {
					String attrValue = edgeFromAttr.getTo().getName();
					String attrName = edgeFromAttr.getLabel();

					//This could be either a attribute or an association					
					Boolean wasAttribute = obj.setAttributeValue(attrName, attrValue);

					if(!wasAttribute) {
						Object[] possibility = {edgeFromAttr.getTo(), attrName, obj};
						possibleAssociations.add(possibility);
					}
				}				
			}
		}

		// Deal with the possible associations found when
		// looking for attributes.
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

			//Check if the object name is the name of any already
			//found object.

			for(ODObject obj : objects) {
				if(obj.getName().equals(leftNode.getName())) {
					associations.add(new ODLink(left,leftLabel,right));
				}
			}
		}

		if(associations.size() > 1) {
			for(int n = 0; n < associations.size(); n++) {
				associations.get(n).combine(associations);
			}
		}

		// Follow execution and activeState edges to find 
		// the current state.
		for(ODObject object : objects) {
			Node node = object.getGraphNode();

			ArrayList<String> toState = new ArrayList<String>();
			toState.add("execution");
			toState.add("activeState");
			ArrayList<Node> states = node.toTrace(toState);
			object.setState(states.get(0));
		}

		// Collect actions - collect the executable actions from the executing behavior
		//Precondition: If an instance has multiple behavior executions, only one
		//of them may have executable edges.
		for (ODObject object : objects) {

			if(object.getGraphNode() != null) {
				
				ArrayList<String> traceOrder = new ArrayList<String>();
				traceOrder.add("execution");
				traceOrder.add("executable");
				ArrayList<Node> executableActions = object.getGraphNode().toTrace(traceOrder);
				for (Node action : executableActions) {
					object.addAction(action);
				}
			}
		}

		// Find the events
		ArrayList<String> eventOrder = new ArrayList<String>();
		eventOrder.add("source");

		for(ODObject object : objects) {

			ArrayList<String> toExternalEvent = new ArrayList<String>();
			toExternalEvent.add("execution");
			toExternalEvent.add("behavior");
			toExternalEvent.add("region");
			toExternalEvent.add("transition");
			toExternalEvent.add("trigger");
			toExternalEvent.add("event");
			ArrayList<Node> externalEvents = object.getGraphNode().toTrace(toExternalEvent);
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

			ArrayList<Node> recieptNodes = object.getGraphNode().toTrace(toReciept);
			for(Node recieptNode : recieptNodes) {

				ArrayList<Node> activeEventSignals 
				= recieptNode.toTrace(fromRecieptToEvent);

				ArrayList<Node> activeEvents = new ArrayList<Node>();
				for(Node n : activeEventSignals) {
					activeEvents.addAll(n.fromTrace(fromSignalToReceive));
				}

				ArrayList<String> check = new ArrayList<String>();
				check.add("i");
				for(Node evNode : activeEvents) {
					Node type = evNode.toTrace(check).get(0);
					if(type.getName().equals("ReceiveSignalEvent")) {
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
		
		ObjDiag result = new ObjDiag(objects, associations);
		return result;
	}
}
