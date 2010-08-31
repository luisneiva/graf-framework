package model.modelTransformer.objectDisplay;

import java.util.ArrayList;

import model.ListGraph;
import agg.xt_basis.Arc;
import agg.xt_basis.Node;

//import agg.xt_basis.Node;

public class ODMethod {
	
	private String name;
	private Boolean enabled;
	private String type;

	/**
	 * @param node The node that instantiates the method
	 */
	public ODMethod(Node node) {
		name = ListGraph.getName(node);
		
		type = "CallOperationAction";
		
		enabled = true ;
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
