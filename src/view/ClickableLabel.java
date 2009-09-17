package view;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;

/** 
 * This is a regular label, but comes with a mouse-over hand icon indicating
 * it will respond to a mouse click. It also allows associated data to be stored
 * with it.
 * 
 * @author Kevin O'Shea
 */
public class ClickableLabel extends Label {
	private Object data = null;
	/** Set any data to be associated with this label */
	public void setData(Object data) {this.data = data;}
	public Object getData() {return data;}
	public ClickableLabel() {super(); addListener();}
	public ClickableLabel(Image i) {super(i); addListener();}
	public ClickableLabel(String s) {super(s); addListener();}
	public ClickableLabel(String s, Image i) {super(s,i); addListener();}
	private void addListener() {
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseHover(MouseEvent me) {}
			public void mouseDragged(MouseEvent me) {}
			public void mouseEntered(MouseEvent me) {
				getParent().setCursor(new Cursor(null, SWT.CURSOR_HAND));
			}
			public void mouseExited(MouseEvent me) {
				getParent().setCursor(new Cursor(null, SWT.CURSOR_ARROW));
			}
			public void mouseMoved(MouseEvent me) {}
		});
	}
}
