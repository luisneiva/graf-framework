package model.modelTransformer.objectDisplay;

import model.ListGraph;
import agg.xt_basis.Node;

public class ODCall {
	
	private String name;

	ODCall(Node callNode) {
		name = ListGraph.getName(callNode);
	}

	public String getName() {
		return name;
	}
}
