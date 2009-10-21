package view.contentDrawer;

import java.util.ArrayList;
import java.util.HashMap;

import model.modelTransformer.objectDisplay.ODEvent;
import model.modelTransformer.objectDisplay.ODObject;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import view.ClickableLabel;

public class ObjectDiagFigure extends Figure {
	public static Color classColor = new Color(null, 255, 255, 206);
	
	/** The object that the figure relates to */
	private ODObject odObj;
	
	static final Font classNameFont = new Font(null, "Arial", 10, SWT.BOLD);
	static final Font classTextFont = new Font(null, "Arial", 7, SWT.NORMAL);
	
	private CompartmentFigure attributeFigure = new CompartmentFigure();
	/** Will hold either events or actions, depending on the current mode of the object */
	private CompartmentFigure runtimePoolFigure;
	
	private ArrayList<ExternalEventHandler> externalEvents = new ArrayList<ExternalEventHandler>();
	private class ExternalEventHandler {
		String name; Listener popupListener; Object data;
		ExternalEventHandler(String name, Listener popupListener, Object data) {
			this.name = name; this.popupListener = popupListener; this.data = data;
		}
	}
	
	/** (objName can be null or "" if you want) 
	 * @param contents 
	 * @param pluginIndent */
	public ObjectDiagFigure(ODObject object) {
		odObj = object;
		
		String objName = odObj.getName();
		String className = odObj.getTheClass().getName();
		String state = odObj.getState().getName();
		
		runtimePoolFigure = new CompartmentFigure(new GridLayout(2,true));

		ToolbarLayout layout = new ToolbarLayout();	//arranges components in a single column
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setOpaque(true);
		
		Label nameLabel = new Label((objName==null?"":objName) + " : " + className);
		nameLabel.setFont(classNameFont);
		add(nameLabel);					//(including the name) (a Label is a Figure)
		add(new Label("  {" + state + "}  "));
		add(attributeFigure);	//each of these are seperate figures (each in their own 'row' of the UMLClassFigure)
		add(runtimePoolFigure);

		//identify as in activity mode by changing the colour of the lower compartment 
		if (!object.getEventMode()) {
			runtimePoolFigure.setBackgroundColor(new Color(null, 230, 230, 175));
			runtimePoolFigure.setOpaque(true);
		}
		final MouseDownListener mouseDownListener = new MouseDownListener();
		addMouseListener(mouseDownListener);
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent me) {
				if (mouseDownListener.mouseDown) {
					//Mouse location relative to the parent control ('me' is wrt entire shell)
					Point localMouseLoc = new Point(
							me.x - getParent().getBounds().x,
							me.y - getParent().getBounds().y);
					//Point scaledMouseLoc = new Point(
					//		localMouseLoc.x,
					//		(int)(localMouseLoc.y*((ScalableFreeformLayeredPane)getParent()).getScale()));
					odObj.setLocation(
							localMouseLoc.x - mouseDownListener.relStartX,
							localMouseLoc.y - mouseDownListener.relStartY
					);
					getParent().getLayoutManager().setConstraint(ObjectDiagFigure.this,
							new Rectangle(odObj.getLocation().x,odObj.getLocation().y,-1,-1));
					revalidate();
				}
			}
			public void mouseEntered(MouseEvent me) {}
			public void mouseExited(MouseEvent me) {}
			public void mouseMoved(MouseEvent me) {}
			public void mouseHover(MouseEvent me) {}
		});
		
		attributeFigure.showHideLabel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent me) {}
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				odObj.setAttributesShowing(!odObj.isAttributesShowing());
			}
		});
		runtimePoolFigure.showHideLabel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent me) {}
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				odObj.setRuntimePoolShowing(!odObj.isRuntimePoolShowing());
			}
		});
	}
	
	class MouseDownListener implements MouseListener {	//TODO
		boolean mouseDown = false;
		int relStartX=0, relStartY=0;
		public void mousePressed(MouseEvent me) {
			if (me.button == 1) {
		        mouseDown = true;
		        relStartX = me.x - getLocation().x;
		        relStartY = me.y - getLocation().y;
			}
		}
		public void mouseReleased(MouseEvent me) {
			mouseDown = false;
		}
		public void mouseDoubleClicked(MouseEvent me) {}
	}
	
	public ODObject getObj() {
		return odObj;
	}
	
	public void addAttribute(String name) {
		Label label = new Label(name);
		label.setFont(classTextFont);
		attributeFigure.add(label);
	}

	public void addAttribute(String name, String value) {
		Label label = new Label(name + " = " + value);
		label.setFont(classTextFont);
		attributeFigure.add(label);
	}

	
	/**
	 * Add an event and the listener to register with clicking on it
	 * @param name Name of the event, to be displayed on GUI
	 * @param transListener Listener to register as response to clicking on this event
	 * @param data Data to be associated with this event label.
	 */
	public void addEvent(String name, MouseListener transListener, Object data) {
		ClickableLabel label = new ClickableLabel(name);
		label.setFont(classTextFont);
		label.addMouseListener(transListener);
		label.setData(data);
		runtimePoolFigure.add(label);
	}
	
	/** Add an event and the listener to register with clicking on it
	 * @param name Name of the action, to be displayed on GUI
	 * @param transListener Listener to register as response to clicking on this action
	 * @param active Is this this action active
	 * @param data Data to be associated with this action label.
	 */
	public void addAction(String name, MouseListener transListener, Boolean active, Object data) {
		ClickableLabel label = new ClickableLabel(name);
		label.setFont(classTextFont);
		label.setEnabled(active);
		label.addMouseListener(transListener);
		label.setData(data);
		runtimePoolFigure.add(label);
	}
	
	/**
	 * Add an external event, the listener to register with clicking on it, and the
	 * data to be associated with the action menu item. 
	 * @param name Name of the event, to be displayed on GUI
	 * @param popupListener Listener to register as response to clicking on this external event.
	 * @param data Data to be associated with this event.
	 */
	public void addExternalEvent(String name, Listener popupListener, Object data) {
		externalEvents.add(new ExternalEventHandler(name, popupListener, data));
	}
	
	/** 
	 * Build menu items and add to given menu.
	 */
	public void buildPopupMenu(Menu menu) {
        for (ExternalEventHandler externalEvent : externalEvents) {
        	MenuItem item = new MenuItem (menu, SWT.PUSH);
        	item.setText(externalEvent.name);
        	item.setData(externalEvent.data);
        	item.addListener(SWT.Selection, externalEvent.popupListener);
        }
        if (externalEvents.size()==0) {
        	MenuItem item = new MenuItem (menu, SWT.PUSH);
        	item.setText("None");
        	item.setEnabled(false);
        }
	}

	public void hideAttributes() {
		attributeFigure.hide();
	}
	public void hideRuntimePool() {
		runtimePoolFigure.hide();
	}
}

