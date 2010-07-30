package model.modelTransformer.objectDisplay;

/** 
 *   An action that an object with a state machine executes after it changes state.
 *
 *   The active attribute refers to whether the action can be done.
 *   This means that it has the input pins satisfied.
 *   If active then in the GUI you should be able to click on it.
 *   If not active then in the GUI it will be greyed out.
 *
 *	 @author Oscar Wood 
 *
 */

import java.util.ArrayList;
import java.util.Iterator;

import model.ListGraph;
import agg.xt_basis.Arc;
import agg.xt_basis.Node;

public class ODAction {

	/** whether this is clickable in the GUI */
	private Boolean enabled;
	private String name;
	/** 
	 * The name of the node that the i edge 
	 * from this points to.
	 * There should be a rule that has the same name as this
	 * string.
	 */
	private String type;

	
	/** temporary */
	public ODAction(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @param node The node that instantiates the actionnode itself.
	 * @param actionBehEx The node(s) that is the behavior execution executing this action
	 */
	ODAction(Node node, ArrayList<Node> actionBehEx) {
		name = ListGraph.getName(node);
		enabled = false;

		for(Arc fromEdge : node.getOutgoingArcsVec()) {
			if(ListGraph.getName(fromEdge).equals("i")) {
				type = ListGraph.getName(fromEdge.getTarget());
				break;
			}
		}

		// Find the input pin nodes first
		ArrayList<Node> potentialInputPins = new ArrayList<Node>();

		ArrayList<String> traceToInputPin1 = new ArrayList<String>();
		ArrayList<String> traceToInputPin2 = new ArrayList<String>();
		ArrayList<String> traceToInputPin3 = new ArrayList<String>();

		// There may be other edges that lead to input pins...		
		traceToInputPin1.add("target");
		traceToInputPin1.add("fromAction");
		traceToInputPin1.add("result");

		traceToInputPin2.add("object");
		traceToInputPin2.add("fromAction");
		traceToInputPin2.add("result");

		traceToInputPin3.add("argument");
		traceToInputPin3.add("fromAction");
		traceToInputPin3.add("result");

		potentialInputPins.addAll(ListGraph.toTrace(traceToInputPin1,node));
		potentialInputPins.addAll(ListGraph.toTrace(traceToInputPin2,node));
		potentialInputPins.addAll(ListGraph.toTrace(traceToInputPin3, node));

		ArrayList<String> iTrace = new ArrayList<String>();
		iTrace.add("i");

		Iterator<Node> inIt = potentialInputPins.iterator();
		while(inIt.hasNext()) {
			Node next = inIt.next();
			Node typeNode = ListGraph.toTrace(iTrace,next).get(0);

			if(!ListGraph.getName(typeNode).equals("ActionInputPin") 
					&& !ListGraph.getName(typeNode).equals("InputPin")
					&& !ListGraph.getName(typeNode).equals("OutputPin")) {
				inIt.remove();
			}
		}

		// Find the behavior execution
		/*	Node behEx;
		ArrayList<String> traceToBehEx = new ArrayList<String>();
		traceToBehEx.add("executable");
		try {
			behEx = ListGraph.fromTrace(traceToBehEx,node).get(0);
		} catch(IndexOutOfBoundsException ioobe) {
			System.out.println("error in odAction there was no executable edge to a behavior execution");
			return;
		}*/

		if(actionBehEx.size() == 0) {
			enabled = false;
		}
		else {

			for(Node actionBehExInst : actionBehEx) {
				// Check if any edges from behavior execution are pins
				for(Arc potentialPinInst : actionBehExInst.getOutgoingArcsVec()) {
					Iterator<Node> inIt2 = potentialInputPins.iterator();
					while(inIt2.hasNext()) {
						String nameToFind = ListGraph.getName(inIt2.next());
						if(nameToFind.equals(ListGraph.getName(potentialPinInst))) {
							inIt2.remove();
						}
					}
				}
			}
			enabled = potentialInputPins.size() == 0;
		}

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
