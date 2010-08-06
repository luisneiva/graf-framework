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
	 * @param name The name of this node
	 */
	public ODMethod(Node node) {
		name = ListGraph.getName(node);
		
		type = "MethodCall";

//		for(Arc fromEdge : node.getOutgoingArcsVec()) {
//			if(ListGraph.getName(fromEdge).equals("i")) {
//				type = ListGraph.getName(fromEdge.getTarget());
//				break;
//			}
//		}
		
		// The method will be available for execution when:
		// 1) it has no behaviorExecution node (it has not been executed before)
		// 2) it has a bE node, but that node has no executable edges (it has been
		//    executed before, but the execution is complete)
		ArrayList<String> traceToBE = new ArrayList<String>();
		traceToBE.add("behavior");
		
		ArrayList<Node> possibleBeNodes = ListGraph.fromTrace(traceToBE, node);
		
		ArrayList<String> traceToBeEdges = new ArrayList<String>();
		traceToBeEdges.add("executable");
		
		if (possibleBeNodes.isEmpty()) {
//			System.out.println("NO BE Node found for " + this.getName());
			enabled = true ;
		} else if (ListGraph.toTrace(traceToBeEdges, possibleBeNodes.get(0)).isEmpty()) {
//			System.out.println("BE Node found for " + this.getName() + " but it has no ExEdges!");
			enabled = true ;
		} else {
//			System.out.println("BE Node found for " + this.getName() + " and it has an ExEdge!");
			enabled = false ;
		}
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
