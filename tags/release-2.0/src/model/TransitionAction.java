package model;

import model.modelTransformer.objectDisplay.DisplayObject;

/**
 * Holds parameters for making a system transition.
 * 
 * @author Kevin O'Shea
 */
public class TransitionAction {
	
	private DisplayObject displayObj;
	private String actionName;
	private String actionParam;

	public TransitionAction(DisplayObject displayObj, String actionName, String actionParam) {
		this.displayObj = displayObj;
		this.actionName = actionName;
		this.actionParam = actionParam;
	}
	
	public DisplayObject getObject() {
		return displayObj;
	}
	public String getActionName() {
		return actionName;
	}
	public String getActionParam() {
		return actionParam;
	}
}
