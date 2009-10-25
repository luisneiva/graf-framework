package graph;
/* 
 *   Graph
 *   A list of edges, where each edge is connected to 2 nodes.
 *   
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of Node
 *
 *     1. getNodeWithName()
 *     -description: Finds the node in the graph with the input
 *     				 name. If no such node exists, returns null.
 *     -inputs: The name of a node.
 *     -outputs: The named node or null.
 *     
 *     2. generateObjectDiagram()
 *     -description: Fills the attributes objects and associations
 *     				 with the ODObject and ODAssociation objects
 *     			     needed to draw the object diagram.
 *     -inputs: None.
 *     -outputs: A string which describes the object diagram (for testing).
 */


import java.util.ArrayList;

import uml.ODClass;
import uml.ODLink;
import uml.ODObject;


public class Graph extends ArrayList<Edge> {

	ArrayList<ODObject> objects;
	ArrayList<ODLink> associations;

	public Node getNodeWithName(String name) {
		for(Edge edge : this) {
			if(edge.getTo().getName().equals(name)) {
				return edge.getTo();
			}
			if(edge.getFrom().getName().equals(name)) {
				return edge.getFrom();
			}
		}
		return null;
	}

	public String toString() {
		String str = "";
		for(Edge edge : this) {
			str += edge.toString() + "\n";
		}
		return str;
	}

//	note class node must have capital c "Class"
	public String generateObjectDiagram() {
		String result = "";

		objects = new ArrayList<ODObject>();

		// First find the Class node
		Node classNode = null;
		for(Edge edge : this) {
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
			return "the graph has no node class";
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
		associations = new ArrayList<ODLink>();
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

		//	Integer size;

		//	do {
		//		size = associations.size(); System.out.println(size);
		if(associations.size() > 1) {
			for(int n = 0; n < associations.size(); n++) {
				associations.get(n).combine(associations);
			}
		}
//		} while(associations.size() != size);

		// Follow execution and activeState edges to find 
		// the current state.
		for(ODObject object : objects) {
			Node node = object.getGraphNode();
			Node classifierBehaviourExectution = null;
			for(Edge edgeFrom : node.getFrom()) {
				if(edgeFrom.getLabel().equals("execution")) {
					classifierBehaviourExectution = edgeFrom.getTo();
				}
			}

			if(classifierBehaviourExectution == null) {
				System.out.println(object.getName() + " has no edge called execution");
			}
			else {
				for(Edge edgeFrom : classifierBehaviourExectution.getFrom()) {
					if(edgeFrom.getLabel().equals("activeState")) {
						object.setState(edgeFrom.getTo());
						break;
					}
				}

			}
		}

		// Now find the activities by following edges from the state node.
		for(ODObject object : objects) {
			if(object.getGraphNode() != null) {
				Node stateNode = object.getState();
				Node doEntryOrExit = null;
				for(Edge edgeFromState : stateNode.getFrom()) {
					
					if(edgeFromState.getLabel().equals("doActivity")
							|| edgeFromState.getLabel().equals("entry")
							|| edgeFromState.getLabel().equals("exit")) {

						doEntryOrExit = edgeFromState.getTo();
						
						for(Edge edgeFromGroup : doEntryOrExit.getFrom()) {
							if(edgeFromGroup.getLabel().equals("node")) {
								object.addAction(edgeFromGroup.getTo());
							}
						}
					}
				}
			}
		}
		
		// Find the events similarly to how actions were found
		for(ODObject object : objects) {
			if(object.getGraphNode() != null) {
				Node stateNode = object.getState();
				for(Edge edgeToState : stateNode.getTo()) {
					if(edgeToState.getLabel().equals("source")) {
						Node eventNode = edgeToState.getFrom();
						object.addEvent(eventNode, object.getGraphNode());
						break;
					}
				}
			}
		}

		for(ODObject obj : objects) {
			result += "\n\n" + obj.toString();
		}

		result += "\n\n";

		for(ODLink assoc : associations) {
			result += assoc.toString() + "\n";
		}

		return result;
	}
	
}
