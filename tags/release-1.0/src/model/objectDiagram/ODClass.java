package model.objectDiagram;
/** 
 *   A class which at least one object in the object diagram is an instance of.
 *
 * 	 @author Oscar Wood   
 *
 */


import java.util.ArrayList;

import model.Node;


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
