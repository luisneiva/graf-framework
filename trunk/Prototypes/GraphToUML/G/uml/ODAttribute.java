package uml;
/* 
 *   ODAttribute
 *   The attribute of an object in the object diagram. This includes the name of an
 *   attribute and its value.
 *
 *   Version description - version 1.6 (Final)
 *   This will not be modified again.
 *   Changes will be made to Implementation.
 *   
 *   Date 17/7/09
 *
 *   Oscar
 *   Contents of ODAttribute
 *   
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
