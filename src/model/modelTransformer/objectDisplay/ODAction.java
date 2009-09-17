package model.modelTransformer.objectDisplay;

/** 
 *   An action that an object with a state machine executes after it changes state.
 *
 *   The active attribute refers to whether the action can be done.
 *   This means that it has the input pins satisfied and/or
 *   it fits the before image of a rule.
 *   If active then in the GUI you should be able to click on it.
 *   If not active then in the GUI it will be greyed out or invisible.
 *   I don't know how to determine the value.
 *
 *	 @author Oscar Wood 
 *
 */

import java.util.ArrayList;
import java.util.Iterator;

import model.ListGraph;

import org.eclipse.draw2d.graph.Edge;

import agg.xt_basis.Arc;
import agg.xt_basis.Node;

public class ODAction {

	private Boolean enabled;
	private String name;
	private String type;

	ODAction(Node node) {
		name = ListGraph.getName(node);
		enabled = false;

		for(Arc fromEdge : node.getOutgoingArcsVec()) {
			if(ListGraph.getName(fromEdge).equals("i")) {
				type = ListGraph.getName(fromEdge.getTarget());
				break;
			}
		}

		/*Graph test = null;
		try {
			GraphTransformer gt = new GraphTransformer(new URL("file:" + "GTSRules.xml"));
			gt.setGraph(new Graph(graph));
			test = gt.transition(inst, type, node.getName());
		}
		catch(RuleException re)
		{
		}
		catch(MalformedURLException mue)
		{
			mue.printStackTrace();
		}

		enabled = test != null;*/

		
		// Find the input pin nodes first
		ArrayList<Node> potentialInputPins = new ArrayList<Node>();
		
		ArrayList<String> traceToInputPin1 = new ArrayList<String>();
		ArrayList<String> traceToInputPin2 = new ArrayList<String>();
		
		// There may be other edges that lead to input pins...		
		traceToInputPin1.add("target");
		traceToInputPin1.add("fromAction");
		traceToInputPin1.add("result");
		
		traceToInputPin2.add("object");
		traceToInputPin2.add("fromAction");
		traceToInputPin2.add("result");
		
		potentialInputPins.addAll(ListGraph.toTrace(traceToInputPin1,node));
		potentialInputPins.addAll(ListGraph.toTrace(traceToInputPin2,node));
			
		ArrayList<String> iTrace = new ArrayList<String>();
		iTrace.add("i");
		
		Iterator<Node> inIt = potentialInputPins.iterator();
		while(inIt.hasNext()) {
			Node next = inIt.next();
			Node typeNode = ListGraph.toTrace(iTrace,next).get(0);
			// There may be other valid types...
			if(!ListGraph.getName(typeNode).equals("ActionInputPin") 
					&& !ListGraph.getName(typeNode).equals("InputPin")
					&& !ListGraph.getName(typeNode).equals("OutputPin")) {
				inIt.remove();
			}
		}
		
		// Find the behavior execution
		Node behEx;
		ArrayList<String> traceToBehEx = new ArrayList<String>();
		traceToBehEx.add("executable");
		try {
		behEx = ListGraph.fromTrace(traceToBehEx,node).get(0);
		} catch(IndexOutOfBoundsException ioobe) {
			System.out.println("error in odAction there was no executable edge to a behavior execution");
			return;
		}
		
		// Check if any edges from behavior execution are pins
		for(Arc potentialPinInst : behEx.getOutgoingArcsVec()) {
			Iterator<Node> inIt2 = potentialInputPins.iterator();
			while(inIt2.hasNext()) {
				String nameToFind = ListGraph.getName(inIt2.next());
				if(nameToFind.equals(ListGraph.getName(potentialPinInst))) {
					inIt2.remove();
				}
			}
		}
		
		enabled = potentialInputPins.size() == 0;
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
