package model.objectDiagram;

import java.util.ArrayList;
import java.util.Random;

import org.eclipse.swt.graphics.Point;

import model.Node;

/** 
 *   ODObject
 *   An object in the object diagram. Note that the state attribute
 *   will be null in objects of classes without state machines.
 *
 *	 @author Oscar Wood
 *	 @author Kevin O'Shea 
 *   
 */
public class ODObject {

	private String name;

	private ODClass instantiation;

	private ArrayList<ODAttribute> attributes;

	private Node graphNode;

	private Node stateNode;

	private ArrayList<ODAction> actionPool;
	private ArrayList<ODEvent> eventPool;
	private ArrayList<ODEvent> externalEvents;

	// If not eventMode then it is in action mode
	private Boolean eventMode;
	
	/** Location of the object on the display */
	private Point location = new Point(0,0);
	
	/** Random number generator used in initial placement of objects on screen */
	private static final int initseed = 987654321;
	private static Random randomGenerator = new Random(initseed);
	
	public ODObject(ODClass instantiation, Node graphNode, Boolean eventMode) {
		this.eventMode = eventMode;

		this.instantiation = instantiation;
		attributes = new ArrayList<ODAttribute>();
		this.graphNode = graphNode;
		name = graphNode.getName();
		stateNode = null;
		actionPool = new ArrayList<ODAction>();
		eventPool = new ArrayList<ODEvent>();
		externalEvents = new ArrayList<ODEvent>();
		
		//Set initial location to random point on screen (more structured placement would be better) 
		setLocation(randomGenerator.nextInt(400),randomGenerator.nextInt(300));
	}

	public static void resetRandomLocator() {
		randomGenerator = new Random(initseed);
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point loc) {
		setLocation(loc.x,loc.y);
	}
	public void setLocation(int x, int y) {
		location.x = x; location.y = y;
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


	public Node getGraphNode() {
		return graphNode;
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

	public String getName() {
		return name;
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
	 * assume that it is some other part of the graph other
	 * than an attribute.
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

	public void setState(Node stateNode) {
		this.stateNode = stateNode;
	}

	public Node getState() {
		return stateNode;
	}

	/**
	 * Creates an ODAction based on a node and adds it to this.
	 * 
	 * @param The node that represents the action.
	 */
	public void addAction(Node node) {
		ODAction action = new ODAction(node);
		actionPool.add(action);		
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
	
	//TODO - When in activity mode, this method indicates if there are no more actions left to
	//TODO   execute in the activity - needed to determine when to change modes from activity->event.
	//TODO Oscar - The PluginModel code is set up to call this when appropriate. If you can write the
	//TODO code for this function then the mode-switching should work automatically.
	
	// This will work eventually. However, at the moment all actions are always !active
	public boolean activityComplete() {
		//if activity does not even contain any actions then activity is immediately complete
		if (actionPool.size()==0) return true;
		
		//TODO Oscar, are you sure this is the right way to do it? Should probably make sure
		//TODO that it is never possible for all non-executed actions to be non-enabled at the
		//TODO same time.
		//TODO (eg they could all be waiting for something to happen - caused by another object)
		
		for(ODAction action : actionPool) {
			if(action.getActive()) {
				return true;
			}
		}
		
		return false;
	}
}
