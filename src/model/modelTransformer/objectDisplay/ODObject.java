package model.modelTransformer.objectDisplay;

import java.util.ArrayList;

import model.ListGraph;
import agg.xt_basis.Graph;
import agg.xt_basis.Node;

/** 
 *   ODObject
 *   An object in the object diagram. Note that the state attribute
 *   will be null in objects of classes without state machines.
 *
 *	 @author Oscar Wood
 *	 @author Kevin O'Shea 
 * 	 @author Frank Su
 *   
 */
public class ODObject extends DisplayObject {

	private ODClass instantiation;

	private ArrayList<ODAttribute> attributes;

	private ArrayList<Node> stateNodes;

	private ArrayList<ODAction> actionPool;
	private ArrayList<ODEvent> eventPool;
	private ArrayList<ODEvent> externalEvents;
	private ArrayList<ODCall> callPool;

	private ArrayList<ODMethod> methods;

	// If not eventMode then it is in action mode
	private Boolean eventMode;

	/** Is the attributes compartment showing */
	private boolean attributesShowing = true;
	/** Is the event/action compartment showing */
	private boolean runtimePoolShowing = true;
	
	public ODObject(ODClass instantiation, Node graphNode, Boolean eventMode) {
		this.eventMode = eventMode;

		this.instantiation = instantiation;
		attributes = new ArrayList<ODAttribute>();
		this.graphNode = graphNode;
		name = ListGraph.getName(graphNode);
		actionPool = new ArrayList<ODAction>();
		callPool = new ArrayList<ODCall>();
		eventPool = new ArrayList<ODEvent>();
		externalEvents = new ArrayList<ODEvent>();
		methods = new ArrayList<ODMethod>();

		stateNodes = new ArrayList<Node>();

		//Set initial location to random point on screen (more structured placement would be better) 
		setLocation(randomGenerator.nextInt(400),randomGenerator.nextInt(300));		
	}

	public boolean isAttributesShowing() {
		return attributesShowing;
	}
	public void setAttributesShowing(boolean show) {
		attributesShowing = show;
	}
	public boolean isRuntimePoolShowing() {
		return runtimePoolShowing;
	}
	public void setRuntimePoolShowing(boolean show) {
		runtimePoolShowing = show;
	}

	public Boolean getEventMode() {
		return eventMode;
	}

	public void setEventMode(Boolean eventMode) {
		this.eventMode = eventMode;
	}

	public ArrayList<ODAttribute> getAttributes() {
		return attributes;
	}

	public ODClass getTheClass() {
		return instantiation;
	}
	public ArrayList<ODAction> getActionPool() {
		return actionPool;
	}
	public ArrayList<ODEvent> getEventPool() {
		return eventPool;
	}
	public ArrayList<ODEvent> getExternalEvents() {
		return externalEvents;
	}
	public ArrayList<ODCall> getCallPool() {
		return callPool;
	}

	public ArrayList<ODMethod> getMethods() {
		return methods;
	}

	public String toString() {
		return name;
		/*String str = name + " : " + 
		instantiation.getName() + "\n" + stateNode.getName() +"\n----------";
		for(ODAttribute attr : attributes) {
			str += "\n  " + attr;
		}
		str += "\n----------";
		for(ODEvent event : eventPool) {
			str += "\n  Event: " + event.getName();
			if(!eventMode) {
				str += "(hidden)";
			}
		}
		str += "\n----------";
		for(ODAction action : actionPool) {
			str += "\n  Action: " + action.getName();
			if(action.getActive()) {
				str += " (can be selected)";
			}
			else {
				str += " (can't be selected)";
			}
			if(eventMode) {
				str += " (hidden)";
			}
		}
		str += "\n----------";

		return str;		*/
	}

	/**
	 * Assuming the class object that this object object 
	 * instantiates has been initialised, this method 
	 * copies the attribute names of the class object 
	 * to this object. It leaves the values of those
	 * attributes blank.
	 */
	public void setAttributeNames() {
		for(String name : instantiation.getAttributeNames()) {
			attributes.add(new ODAttribute(name));
		}
	}

	/**
	 * Finds the attribute with the given name and sets 
	 * it to the given value. However, if there is no
	 * attribute with that name then we ignore it because we 
	 * assume that it is some other part of the graph not
	 * an attribute.
	 * 
	 * @param attrName The name of the attribute.
	 * @param attrValue The value of the named attribute.
	 */
	public Boolean setAttributeValue(String attrName, String attrValue) {
		for(ODAttribute attr : attributes) {
			if(attr.unset() && attr.getName().equals(attrName)) {
				attr.setValue(attrValue);
				return true;
			}
		}
		return false;
	}

	public void addState(Node sn) {
		stateNodes.add(sn);
	}

	/**
	 * Creates an ODAction based on a node and adds it to this.
	 * 
	 * @param The node that represents the action.
	 * @param The (optional) node(s) that represents the behavior execution executing actions
	 */
	public void addAction(Node node, ArrayList<Node> actionBehEx, Graph graph) {
		ODAction action = new ODAction(node, actionBehEx, graph);
		actionPool.add(action);
	}

	public void addCall(Node node) {
		ODCall call = new ODCall(node);
		callPool.add(call);
	}

	/**
	 * Creates an ODEvent based on a node and adds it to this.
	 * 
	 * @param eventNode The node for the event.
	 */
	public void addEvent(Node eventNode, Node occurenceNode) {
		ODEvent event = new ODEvent(eventNode, occurenceNode);
		eventPool.add(event);
	}

	/**
	 * Creates an ODEvent based on a node and adds it to this.
	 * 
	 * @param eventNode The node for the event.
	 */
	public void addExternalEvent(Node eventNode) {
		ODEvent event = new ODEvent(eventNode, null);
		externalEvents.add(event);
	}

	/**
	 * Creates an ODMethod based on a node and adds it to this.
	 * 
	 * @param methodNode The node for the method.
	 */
	public void addMethod(Node methodNode) {
		ODMethod method = new ODMethod(methodNode);
		methods.add(method);	
	}

	public ArrayList<Node> getStates() {
		return stateNodes;
	}

	/**
	 * 
	 * ABANDONED
	 * 
	 * See http://code.google.com/p/graf-framework/wiki/GRAFTimers for details
	 * 
	 * createDelayEvent can be deleted. It's only still here to give instructions
	 * on how to implement a better timer system
	 * 
	 * @author Oscar
	 */
	/*public void createDelayEvent(ODObject odObj, ListGraph graph) {
		String actParam = "";
		
		// First find the Actor node
		Node actor = null;
		for(Arc edge : graph.getArcsList()) {
			if(ListGraph.getName(edge.getTarget()).equals("Actor")) {
				actor = (Node)edge.getTarget();
				break;
			}
			if(ListGraph.getName(edge.getSource()).equals("Actor")) {
				actor = (Node)edge.getSource();
				break;
			}
		}
		
		ArrayList<String> fromInstances = new ArrayList<String>();
		fromInstances.add("i");
		
		ArrayList<Node> actorInstances = ListGraph.fromTrace(fromInstances, actor);
		
		ArrayList<String> findObjectRoute = new ArrayList<String>();
		findObjectRoute.add("pool");
		findObjectRoute.add("message");
		findObjectRoute.add("sender");
		
		ArrayList<String> backToSender = new ArrayList<String>();
		backToSender.add("sender");
		
		ArrayList<String> findSignalRoute = new ArrayList<String>();
		findSignalRoute.add("i");
		
		ArrayList<String> backToActionParam = new ArrayList<String>();
		backToActionParam.add("signal");
		
		// Find objects
		ArrayList<Node> findObjects = new ArrayList<Node>();
		ArrayList<Node> senderInstances = new ArrayList<Node>();
		ArrayList<Node> findSignals = new ArrayList<Node>();
		ArrayList<Node> findActions = new ArrayList<Node>();
		
		for(Node actorInstance : actorInstances) {
			findObjects = ListGraph.toTrace(findObjectRoute, actorInstance);
			for (Node objectNode : findObjects) {
				
				senderInstances = ListGraph.fromTrace(backToSender, objectNode);
				for(Node senderNode : senderInstances) {
					
					findSignals = ListGraph.toTrace(findSignalRoute, senderNode);
					for(Node sigalNode : findSignals) {
						
						findActions = ListGraph.fromTrace(backToActionParam, sigalNode);
						for(Node actParamNode : findActions){
							actParam = ListGraph.getName(actParamNode);
						}
					}
				}
			}
		}

		String gtsRulesPath = Properties.getProperty("gtsRulesPath");
		String gtsRulesSeqPath = Properties.getProperty("gtsRulesSeqPath");

		try {
			AGGTransformer transformer;
			transformer = new AGGTransformer(gtsRulesPath, gtsRulesSeqPath);
			transformer.setGraph(graph);
			graph = (ListGraph) transformer.transition(odObj.getName(), "ActorSendSignal", actParam);

			ObjectDisplay newDisplay = GraphToModelFactory.createGraphToModel().generateDisplayObjects(graph);
			plugin.refresh(newDisplay);

			if(Boolean.parseBoolean(Properties.getProperty("PrintDebug")))
				System.out.println("Timer transformation complete! Parameters: " + odObj.getName() + ", " + actParam);
		} catch (RuleException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
