package model.modelTransformer.objectDisplay;
/** 
 *   The attribute of an object in the object diagram. This includes the name of an
 *   attribute and its value.
 *
 * 	 @author Oscar Wood   
 *
 */

public class ODAttribute {
	private String name;
	private String value;
	
	private final static String unsetString = "(not set)";
	
	public ODAttribute(String name) {
		this.name = name;
		value = unsetString;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Boolean unset() {
		return value.equals(unsetString);
	}
	
	public String toString() {
		return name + " = " + value;
	}

}
