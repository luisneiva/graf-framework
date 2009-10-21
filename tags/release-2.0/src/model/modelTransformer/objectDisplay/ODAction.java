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

import model.Edge;
import model.Graph;
import model.Node;

public class ODAction {

	private Boolean enabled;
	private String name;
	private String type;

	ODAction(Node node) {
		name = node.getName();
		enabled = false;

		for(Edge fromEdge : node.getFrom()) {
			if(fromEdge.getLabel().equals("i")) {
				type = fromEdge.getToName();
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
		
		potentialInputPins.addAll(node.toTrace(traceToInputPin1));
		potentialInputPins.addAll(node.toTrace(traceToInputPin2));
			
		ArrayList<String> iTrace = new ArrayList<String>();
		iTrace.add("i");
		
		Iterator<Node> inIt = potentialInputPins.iterator();
		while(inIt.hasNext()) {
			Node next = inIt.next();
			Node typeNode = next.toTrace(iTrace).get(0);
			// There may be other valid types...
			if(!typeNode.getName().equals("ActionInputPin") 
					&& !typeNode.getName().equals("InputPin")
					&& !typeNode.getName().equals("OutputPin")) {
				inIt.remove();
			}
		}
		
		// Find the behavior execution
		Node behEx;
		ArrayList<String> traceToBehEx = new ArrayList<String>();
		traceToBehEx.add("executable");
		try {
		behEx = node.fromTrace(traceToBehEx).get(0);
		} catch(IndexOutOfBoundsException ioobe) {
			System.out.println("error in odAction there was no executable edge to a behavior execution");
			return;
		}
		
		// Check if any edges from behavior execution are pins
		for(Edge potentialPinInst : behEx.getFrom()) {
			Iterator<Node> inIt2 = potentialInputPins.iterator();
			while(inIt2.hasNext()) {
				String nameToFind = inIt2.next().getName();
				if(nameToFind.equals(potentialPinInst.getLabel())) {
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
