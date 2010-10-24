package model.modelTransformer.objectDisplay;

import java.util.Random;
import org.eclipse.swt.graphics.Point;
import agg.xt_basis.Node;

/** An object to be displayed */
public abstract class DisplayObject {

	/** Name of the object */
	protected String name;
	
	/** The node in the graph that is the root for this object */
	protected Node graphNode;
	
	/** Location of the object on the display */
	protected Point location = new Point(0,0);
	
	/** Get the name of the object */
	public String getName() {
		return name;
	}
	
	/** Get the node in the graph that is the root for this object */
	public Node getGraphNode() {
		return graphNode;
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

	/** Random number generator used in initial placement of objects on screen */
	private static final int initseed = 987654321;
	protected static Random randomGenerator = new Random(initseed);
	public static void resetRandomLocator() {
		randomGenerator = new Random(initseed);
	}
}
