package view.contentDrawer;

import model.modelTransformer.objectDisplay.ObjectDisplay;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.widgets.Listener;

import view.View;

public abstract class ContentDrawer {
	
	/** The view containing the contents */
	protected View view;
	
	/** The parent figure being drawn onto */
	protected Figure contents;
	
	/** The action listener that is the response to when a user clicks on some event/action */
	protected MouseListener transListener;

	/** The action listener that is the response to when a user clicks on a popup menu item */
	protected Listener popupListener;
	
	public ContentDrawer(View view) {
		this.view = view;
	}
	
	/** Set the contents to be drawn to */
	public void setContents(Figure contents) {
		this.contents = contents;
	}
	
	/** Set the action listener that is the response to when a user clicks on some event/action */
	public void setTransListener(MouseListener transListener) {
		this.transListener = transListener;
	}

	/** Set the action listener that is the response to when a user clicks on a popup menu item */
	public void setPopupListener(Listener popupListener) {
		this.popupListener = popupListener;
	}
	
	/** Draw the objdisplay onto the contents figure */
	public abstract void draw(ObjectDisplay objdisplay);
}
