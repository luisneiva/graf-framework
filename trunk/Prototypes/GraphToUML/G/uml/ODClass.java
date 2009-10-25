package uml;
/* 
 *   ODClass
 *   A class which at least one object in the object diagram is an instance of.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of ODClass
 *   
 *
 */


import graph.Node;

import java.util.ArrayList;


public class ODClass {
	private String name;
	private ArrayList<String> attributeNames;
	private Node graphNode;
	
	
	public ODClass(Node graphNode) {
		attributeNames = new ArrayList<String>();
		this.graphNode = graphNode;
		name = graphNode.getName();
	}
	
	public Node getGraphNode() {
		return graphNode;
	}
	
	public void addAttributeName(String name) {
		attributeNames.add(name);
	}
	
	public ArrayList<String> getAttributeNames() {
		return attributeNames;
	}
	
	public String getName() {
		return name;
	}		
}
