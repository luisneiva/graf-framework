package model.modelTransformer.objectDisplay;
/** 
 *   A line connecting 2 objects in the object diagram.
 * 
 *   @author Oscar Wood 
 */


import java.util.ArrayList;
import java.util.Iterator;


public class ODLink {
	private ODObject left;
	private String leftLabel;

	private ODObject right;
	private String rightLabel;

	public ODLink(ODObject left, String rightLabel, ODObject right) {
		this.rightLabel = rightLabel;
		this.left = left;
		this.right = right;
	}

	public String toString() {
		if(leftLabel == null)
			return "(incomplete) To " + left.getName() + " with label " + leftLabel + " from " + right.getName();
		else
			return left.getName() + "<--" + leftLabel + "---------" + rightLabel +"-->"+ right.getName();
	}

	public ODObject getLeftObj() { 
		return left; 
	}
	public ODObject getRightObj() { 
		return right; 
	}
	/**
	 * Unimplemented
	 * At the moment GRAF doesn't take into consideration the possibility
	 * that a link could have a label on the middle (often R1, R2 etc), 
	 * not just the 2 ends
	 */
	public String getLinkCentreLabel() { 
		return ""; 
	}

	public String getLinkLeftLabel() { 
		return leftLabel; 
	}
	public String getLinkRightLabel() { 
		return rightLabel; 
	}


	/**
	 * At first we find only one side of the 
	 * association and a single line is represented
	 * by 2 objects of this class. This method
	 * combines 2 objects, deleting the input one.
	 *     
	 * @param associations All other associations
	 */
	public void combine(ArrayList<ODLink> associations) {

		Iterator<ODLink> assocIterator = associations.iterator();
		while(assocIterator.hasNext()) {

			ODLink other = assocIterator.next();

			if(equals(other)) {
				continue;
			}
			else if(other.left.equals(right)
					&& other.right.equals(left)) {
				leftLabel = other.rightLabel;
				assocIterator.remove();
			}
		}
	}

	public ArrayList<ODAction> getActions() {
		
		ArrayList<ODAction> testList = new ArrayList<ODAction>();
		
		return testList;
	}
}
