package view;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;

public class ClickableLabel extends Label {
	private Object data = null;
	public void setData(Object data) {this.data = data;}
	public Object getData() {return data;}
	ClickableLabel() {super(); addListener();}
	ClickableLabel(Image i) {super(i); addListener();}
	ClickableLabel(String s) {super(s); addListener();}
	ClickableLabel(String s, Image i) {super(s,i); addListener();}
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
