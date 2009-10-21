package model;
/** 
 * This class contains a list of Edges.
 *
 * @author Oscar Wood 
 *     				
 */



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import model.graphTransformer.PatternMatch;
import model.objectDiagram.ODClass;
import model.objectDiagram.ODLink;
import model.objectDiagram.ODObject;

public class Graph extends ArrayList<Edge> {

	// only used for graph to uml, not GTS
	private ArrayList<ODObject> objects;
	private ArrayList<ODLink> associations;

	public Graph() {
		objects = null;
		associations = null;
	}

	public Graph(Graph toCopy) {
		objects = null;
		associations = null;

		for(Edge e : toCopy) {
			add(new Edge(e.getToName(), 
					e.getFromName(), e.getLabel(),this));
		}
	}

	public ArrayList<ODObject> getODObjects() {
		return objects;
	}

	public ArrayList<ODLink> getODLinks() {
		return associations;
	}

	public String toString() {
		String str = "The graph is:\n";
		for (Edge e : this) {
			str = str + " " + e.toString() + "\n";
		}
		return str;
	}

	/** Check if the graph contains an edge with the same labels as this */
	public boolean containsEdge(Edge edge) {
		for (Edge e : this) {
			if (e.equals(edge)) {
				return true;
			}
		}
		return false;
	}
	/** Check if the graph contains a node with the same label as this */
	public boolean containsNode(String name) {
		for (Edge e : this) {
			if (e.getFromName().equals(name) || e.getToName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if there are any variable nodes. (so that we 
	 * know whether to fit rule to graph)
	 * 
	 * @param A graph to test.
	 * @return True if there's one or more variable node.
	 */
	public Boolean containsVariableNodes() {
		for(Edge edge : this) {
			if(edge.toIsVariable || edge.fromIsVariable) {
				return true;
			}
		}
		return false;
	}

	/**          
	 * Changes all instances of a variable node
	 * into another, non-variable node.
	 * 
	 * @param Node to replace and what to replace it with.
	 */
	public void replaceNode(String target, String replacement) {
		if(target.charAt(0) != '?') {
			System.out.println("ERROR in graph.java replaceNode() with " + target + ">> "+ replacement);
			//if this has happened, we must have tried to
			// replace the same target twice
			return;
		}

		for(Edge e : this) {

			e.getTo().replace(target, replacement);
			e.getFrom().replace(target, replacement);

			if(e.getFromName().equals(target)) {
				e.fromIsVariable = false;
				e.setFromName(replacement);
			}
			if(e.getToName().equals(target)) {
				e.toIsVariable = false;
				e.setToName(replacement);
			}
		}
	}

	/**
	 * Used by Rule. After checking for subgraph, reset the attributes 
	 * on each edge to see if it has already been checked.
	 */
	public void resetCheck() {
		for(Edge e : this) {
			e.checked = false;
			e.getTo().resetCheck();
			e.getFrom().resetCheck();
		}
	}

	/**
	 * Finds in the graph a node with the given name.
	 * @param The name of the node to find.
	 * @return The node with the name.
	 */
	public Node getNodeWithName(String name) {
		for(Edge edge : this) {
			if(edge.getToName().equals(name)) {
				return edge.getTo();
			}
			if(edge.getFromName().equals(name)) {
				return edge.getFrom();
			}
		}
		return null;
	}

	/**
	 * Adds an i edge and nodes.
	 * @param from The name of the node the edge is from.
	 * @param to The name of the node the edge points to.
	 */
	public void addIEdge(String from, String to) {
		add(new Edge(to,from,this));	
	}

	/**
	 * Adds an edge and nodes.
	 * @param from The name of the node the edge is from.
	 * @param label The label of the edge
	 * @param to The name of the node the edge points to.
	 */
	public void addEdge(String from, String label, String to) {
		add(new Edge(to,from,label,this));
	}

	/**
	 * Applies a set of variable assignments to this graph. It's 
	 * assumed this method will only be called on graphs that fit
	 * the pattern.
	 * @param pattern The pattern match to apply.
	 */
	public void applyPatternMatch(PatternMatch pattern) {
		for(Edge edge : this) {
			if(edge.toIsVariable 
					|| edge.fromIsVariable
					|| edge.edgeIsVariable) {
				edge.applyPatternMatch(pattern);
			}
		}
	}

	public Integer numberOfVariables() {
		ArrayList<String> vars = new ArrayList<String>();

		for(Edge e : this) {
			if(e.toIsVariable) {
				if(!vars.contains(e.getToName())) {
					vars.add(e.getToName());
				}
			}
			if(e.fromIsVariable) {
				if(!vars.contains(e.getFromName())) {
					vars.add(e.getFromName());
				}
			}
		}

		return vars.size();
	}

	/**
	 * Generates a new graph which is this dealgebrized 
	 * (see the wiki page graphs).
	 * @return This, dealgebrised
	 */
	public Graph dealg() {
		Graph result = new Graph();

		Integer counter = 0;
		for(Edge e : this) {
			String to = e.getToName();
			String from = e.getFromName();
			String label = e.getLabel();

			//result.addEdge(to, label+"#"+counter, "to");
			//result.addEdge(from, label+"#"+counter, "from");

			result.addEdge(label+"#"+counter, "to", to);
			result.addEdge(label+"#"+counter, "from", from);

			counter++;
		}

		return result;
	}

	/**
	 * Undoes the effects of dealg().
	 * 
	 * @return A graph that is the same as this but not in dealg form
	 */
	public Graph realg() {
		Graph result = new Graph();

		for(Edge e : this) {
			Node n = e.getFrom();

			if(n.getChecked()) {
				continue;
			}

			if(!(n.getFrom().size() == 2
					&& n.getTo().size() == 0)) {
				System.out.println("ERROR graph not in proper dealg form");
			}

			String to = null;
			String from = null;
			for(Edge e2 : n.getFrom()) {
				if(e2.getLabel().equals("to")) {
					to = e2.getToName();
				}
				else if(e2.getLabel().equals("from")) {
					from = e2.getToName();
				}
			}

			if(to == null || from == null) {
				System.out.println("ERROR dealg labels incorrect");
			}

			String label = n.getName();
			Integer hashPos = label.indexOf('#');
			if(hashPos == -1) {
				System.out.println("ERROR not a dealged label");
			}

			label = label.substring(0, hashPos);

			n.check();

			//result.addEdge(to, from, label);
			result.addEdge(from, label, to);
		}

		resetCheck();

		return result;
	}

	/** 
	 * Says if there are variable edges (so that we know to dealg).
	 * 
	 * @return Whether there is at least one variable edge.
	 */
	public Boolean containsVariableEdges() {
		for(Edge e : this) {
			if(e.edgeIsVariable) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This is copied out of Kevin's ModelToGraph.java
	 * with a few changes to make it work here.
	 * 
	 * @param name The file name of the dot file to be created.
	 */
	public void outputdot(String name) {

		//for now at least
		Integer numModelElems = 0;
		String formerlyGreen = "black";

		String res = "digraph "+name+" {\n";
		for (Edge edge : this) {
			res += "\t\"" + edge.getFromName() + "\" -> \"" + edge.getToName() +
			"\" [label=\"" + edge.getLabel() + (edge.getLabel().equals("i")?"\", color=\"grey\"":"\"") + "]\n";
		}
		//colour nodes, but work from the bottom up since lower node colours overwrite previous ones
		for (int i = numModelElems; i < size(); i++) {
			res += "\t\"" + get(i).getFromName() + "\"" + " [color=\""+formerlyGreen+"\", fontcolor=\""+formerlyGreen+"\"]" + "\n";
			res += "\t\"" + get(i).getToName() + "\"" + " [color=\""+formerlyGreen+"\", fontcolor=\""+formerlyGreen+"\"]" + "\n";
		}
		for (int i = 0; i < numModelElems; i++) {
			res += "\t\"" + get(i).getFromName() + "\"" + " [color=\"red\", fontcolor=\"red\"]" + "\n";
			res += "\t\"" + get(i).getToName() + "\"" + " [color=\"red\", fontcolor=\"red\"]" + "\n";
		}
		res += "}\n";
		try {
			BufferedWriter out = 
				new BufferedWriter(new FileWriter("GraphOutputs/"+name+".dot"));
			out.write(res);
			out.close();
			System.out.println("Dot code written");
		} catch (Exception e) {
			System.err.println("Error writing dot file");
		}
	}

	/**
	 * Fills the attributes objects and associations with objects for the GUI.
	 * 
	 */
	public void generateObjectDiagram() {
		//String result = "";

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
			return;
			//return "the graph has no class node";
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

		// Dig down into the tree of the activity for all actions.
		for(ODObject object : objects) {
			
			ArrayList<Node> activityTops = new ArrayList<Node>();
			
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
								activityTops.add(edgeFromGroup.getTo());
								//object.addAction(edgeFromGroup.getTo());
							}
						}
					}
				}
			}
			
			ArrayList<Node> actionNodes = new ArrayList<Node>();
			for(Node activityTop : activityTops) {
				actionNodes = findAction(activityTop, 5);
			}
			for(Node actionNode : actionNodes) {
				object.addAction(actionNode);
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



			/*ArrayList<Node> activeEventSignals = object.getGraphNode().toTrace(fromObjectToEvent);

			ArrayList<Node> activeEvents = new ArrayList<Node>();
			for(Node n : activeEventSignals) {
				activeEvents.addAll(n.fromTrace(fromSignalToReceive));
			}

			for(Node evNode : activeEvents) {
				object.addEvent(evNode);
			}*/

		}

		/*for(ODObject obj : objects) {
			result += "\n\n" + obj.toString();
		}

		result += "\n\n";

		for(ODLink assoc : associations) {
			result += assoc.toString() + "\n";
		}

		return result;*/
	}

	/**
	 * Recurses through the tree of actions and finds any nodes that represent actual actions.
	 * 
	 * @param activityTop The node to start the recursion from. Initially, an instance of Activity.
	 * @param hope In order to not loop forever, we must give up eventually. Ie, when hope is zero. Start it at about 5.
	 * @return All action nodes in this activity.
	 */
	private ArrayList<Node> findAction(Node activityTop, Integer hope) {
				
		ArrayList<Node> result = new ArrayList<Node>();
		if(hope < 1) {
			return result;
		}
		
		/**
		 * TODO
		 * This is not a complete list.
		 * What are all the possible actions?
		 * Maybe we could just check if the name contains the string Action
		 * Alternatively, we could just check the fromAction edge. Maybe that's a better idea.
		 */
		ArrayList<String> actionTypes = new ArrayList<String>();
		actionTypes.add("AddStructuralFeatureAction");
		actionTypes.add("ReadStructuralFeatureAction");
		actionTypes.add("SendSignalAction");
		
		// Edges that are often used between actions.
		ArrayList<String> likelyEdges = new ArrayList<String>();
		likelyEdges.add("value");
		likelyEdges.add("target");
		
		hope--;
		
		for(Edge edge : activityTop.getFrom()) {
			Boolean isAction = actionTypes.contains(edge.getToName());
			if(isAction) {
				Boolean isLikely = likelyEdges.contains(edge.getLabel());
				if(isLikely) {
					hope++;
				}
				result.add(edge.getFrom());
			}
			else {
				result.addAll(findAction(edge.getTo(),hope));
			}
		}
		
		return result;
	}
}
