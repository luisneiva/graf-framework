package model;
/** 
 * This class contains a list of Edges.
 *
 * @author Oscar Wood 
 *     				
 */

import java.util.ArrayList;

import model.graphTransformer.PatternMatch;

public class Graph extends ArrayList<Edge> {

	public Graph() {
	}

	/**
	 * Copy constructor.
	 */
	public Graph(Graph toCopy) {
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
		ArrayList<String> varEdges = new ArrayList<String>();

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
			
			if(e.edgeIsVariable) {
				if(!varEdges.contains(e.getLabel())) {
					varEdges.add(e.getLabel());
				}
			}
		}

		return vars.size() + varEdges.size();
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

}