package model.modelTransformer.objectDisplay;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

/** Holds all objects to be displayed */
public abstract class ObjectDisplay {
	
	/** Objects in display */
	protected ArrayList<DisplayObject> objects;
	
	/** Get objects in display */
	public ArrayList<DisplayObject> getODObjs() {
		return objects;
	}
	/** Set objects to be displayed */
	public void setODObjs(ArrayList<DisplayObject> objects) {	//TODO - change name
		this.objects = objects;
	}
	
	/** Shift the display objects by the given translation vector */
	public void move(int x, int y) {
		for (DisplayObject object : objects) {
			Point currLoc = object.getLocation();
			object.setLocation(currLoc.x+x, currLoc.y+y);
		}
	}
}
