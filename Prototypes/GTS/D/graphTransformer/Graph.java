package graphTransformer;
/* 
 *   Graph
 *   This class contains a list of Edges.
 *   
 *   Version description - version 2.8 (Final)
 *   Everything mostly works.
 *   This will not be modified again,
 *   Further updates to this will be in Implementation.
 *   
 *   Date 17/7/09
 *   
 *   Oscar
 *   Contents of Graph
 *
 *     1. containsVariableNodes() 
 *     -description: Checks if there are any variable nodes.
 *     				 (so that we know whether to fit rule to graph)
 *     -inputs: A graph to test.
 *     -output: True if there's one or more variable node.
 *          
 *     2. replaceNode
 *     -description: Changes all instances of a variable node
 *     				 into another, non-variable node.
 *     -inputs: Node to replace and what to replace it with.
 *     -outputs: None.
 *     
 *     3. resetCheck
 *     -description: Used by Rule. After checking for subgraph,
 *     				 reset the flags on each edge to see if it has
 *     				 already been checked.
 *     -inputs: None.
 *     -outputs: None.
 *     
 *     4 getNodeWithName()
 *     -description: Finds in the graph a node with the given
 *     			  	 name.
 *     -inputs: The name.
 *     -outputs: The node.
 *     
 *     5 addIEdge()
 *     -description: Adds an i edge and nodes.
 *     -inputs: The names of the nodes.
 *     -outputs: None.
 *     
 *     6 addEdge()
 *     -description: Adds an edge and nodes.
 *     -inputs: The names of the nodes and edge.
 *     -outputs: None.
 *     
 *     7 applyPatternMatch()
 *     -description: Applies a set of variable assignments
 *     				 to this graph. It's assumed this method
 *     				 will only be called on graphs that fit
 *     				 the pattern.
 *     -inputs: Pattern match to apply.
 *     -outputs: None.
 *     
 *     8. dealg()
 *     -description: Generates a new graph which is this
 *     				 dealgebrized (see the wiki page graphs).
 *     -inputs: None.
 *     -outputs: A graph the same as *this* but in dealg form.
 *     
 *     9. realg()
 *     -description: Undoes the effects of dealg().
 *     -inputs: None.
 *     -outputs: A graph that is the same as *this* but not
 *     			 in dealg form.
 *     
 *     10. containsVariableEdge()
 *     -description: Says if there are variable edges (so that
 *     			     we know to dealg).
 *     -inputs: None.
 *     -outputs: True if there is one or more variable edge.
 *     
 *     11. outputdot()
 *     -description: Modified from H2 prototype of Kevin.
 *     				  Generates a dot file of this graph.
 *     -inputs: File name.
 *     -outputs: None.
 *     				
 */



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class Graph extends ArrayList<Edge> {

	// only used for graph to uml, not GTS
	//ArrayList<ODObject> objects;
	//ArrayList<ODLink> associations;
	
	public Graph() {
		//objects = new ArrayList<ODObject>();
		//associations = new ArrayList<ODLink>();
	}

	public Graph(Graph toCopy) {
		//objects = new ArrayList<ODObject>();
		//associations = new ArrayList<ODLink>();
		
		for(Edge e : toCopy) {
			add(new Edge(e.getToName(), 
					e.getFromName(), e.getLabel(),this));
		}
	}

	public String toString() {
		String str = "The graph is:\n";
		for (Edge e : this) {
			str = str + " " + e.toString() + "\n";
		}
		return str;
	}

	public Boolean containsVariableNodes() {
		for(Edge edge : this) {
			if(edge.toIsVariable || edge.fromIsVariable) {
				return true;
			}
		}
		return false;
	}

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

	public void resetCheck() {
		for(Edge e : this) {
			e.checked = false;
			e.getTo().resetCheck();
			e.getFrom().resetCheck();
		}
	}
	
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

	public void addIEdge(String to, String from) {
		add(new Edge(to,from,this));	
	}

	public void addEdge(String to, String from, String label) {
		add(new Edge(to,from,label,this));
	}

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
	
	public Graph dealg() {
		Graph result = new Graph();
		
		Integer counter = 0;
		for(Edge e : this) {
			String to = e.getToName();
			String from = e.getFromName();
			String label = e.getLabel();
			
			result.addEdge(to, label+"#"+counter, "to");
			result.addEdge(from, label+"#"+counter, "from");

			counter++;
		}
		
		return result;
	}
	
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
			
			result.addEdge(to, from, label);
		}
		
		resetCheck();
				
		return result;
	}
	
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
				new BufferedWriter(new FileWriter("Generated dot files/"+name+".dot"));
			out.write(res);
			out.close();
			System.out.println("Dot code written");
		} catch (Exception e) {
			System.err.println("Error writing dot file");
		}
	}
	
	/*
	 * The methods below are only used in the graph to uml
	 * module, not the GTS module
	 */
	
	/*public String generateObjectDiagram() {
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
	}*/
}
