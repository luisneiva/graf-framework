package model.modelTransformer.objectDisplay;

import model.ListGraph;
import agg.xt_basis.Node;

//import agg.xt_basis.Node;

public class ODMethod {
	
	private String name;
	private Boolean enabled;
	private String type;

	/**
	 * @param node The node that instantiates the method
	 * @param name The name of this node
	 */
	public ODMethod(Node node) {
		name = ListGraph.getName(node);
		
		//TODO - change constructor to accept a node - the node that instantiates the method
		// Then, assign value to type by tracing i-edge.
		
		//TODO - assign value to enabled by checking whether all the 'entry' actions have
		// been executed?
	}

	/**
	 * Constructor for test methods - to be depreciated on full implementation
	 * @param name
	 */
	public ODMethod(String theName) {
		this.name = theName ;
	}

	public String getName() {
		return name;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getType() {
		return type;
	}
}
