package view;

import java.util.ArrayList;
import java.util.HashMap;

import model.PluginModel;
import model.objectDiagram.ODAction;
import model.objectDiagram.ODAttribute;
import model.objectDiagram.ODEvent;
import model.objectDiagram.ODLink;
import model.objectDiagram.ODObject;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import controller.Controller;

/**
 * Provides display for system.
 * 
 * @author Kevin O'Shea
 */

public class View extends ViewPart {

	/** Is the system running as a plugin or stand-alone. True if plugin, False if standalone */
	private Boolean isPlugin;
	
	/** Reference to system model */
	private PluginModel model;

	/** Parent control that is being drawn to */
	private Composite parent;

	/** Container for menus, controls and content container */
	private Figure rootFigure;
	
	/** Holds model contents */
	private ScalableFreeformLayeredPane contents;
	
	//Random button for testing
	private Button button = new Button("Animate \"microwaveTest1.umltest\"");
	/** Button to choose model to animate */
	private Button newAnimButton = new Button("New");
	
	/** The action listener that is the response to when a user clicks on some event/action */
	private MouseListener transListener;

	/** The action listener that is the response to when a user clicks on a popup menu item */
	private Listener popupListener;
	
	/** Default constructor called by plugin runtime. Do not call this if standalone.
	 * @see View(plugin)
	 */
	public View() {
		this(true);
	}

	public View(boolean plugin) {
		System.out.println("This system is running " + (plugin?"as a plugin":"standalone"));
		System.out.println("View is alive");
		isPlugin = plugin;
		@SuppressWarnings("unused")
		Controller controller = new Controller(this, plugin);
	}

	public void setModel(PluginModel model) {
		this.model = model;
	}
	
	public void addAnimateListener(ActionListener listener) {
		button.addActionListener(listener);
	}

	public void addNewAnimListener(ActionListener listener) {
		newAnimButton.addActionListener(listener);
	}

	public void setTransitionListener(MouseListener mouseListener) {
		transListener = mouseListener; 
	}

	public void setPopupListener(Listener listener) {
		popupListener = listener;
	}

	/** Reset display for new animation */
	public void reset() {
		contents.removeAll();
		contents.setScale(1.0);
	}
	
	/** Open a file chooser and return path to chosen file, or null if none chosen */
	public String openFileChooser() {
		FileDialog filedialog = new FileDialog(parent.getShell());
		filedialog.setFilterExtensions(new String[]{"*.umltest"});
		return filedialog.open();
	}
	
	/** Display error message to user */
	public void showError(String msg) {
		MessageDialog.openError(parent.getShell(), "Animator Error", msg);
	}
	
	/** Creates the initial view - ie the view upon Animator startup */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		FigureCanvas canvas = new FigureCanvas(parent);
		canvas.setLayout(new RowLayout(SWT.VERTICAL));

		LightweightSystem lws = new LightweightSystem(canvas);
		rootFigure = new Figure();
		contents = new ScalableFreeformLayeredPane();
		contents.setSize(3000,3000);
		rootFigure.setLayoutManager(new XYLayout());
		contents.setLayoutManager(new XYLayout());
		rootFigure.setBackgroundColor(new Color(null,255,255,255));
		rootFigure.setOpaque(true);
		contents.setSize(3000,3000);
		rootFigure.add(contents);
		lws.setContents(rootFigure);
		
		//set up the menu (button(s))
		//TODO - delete this 'button' - just for testing (standalone onlys)
		if (!isPlugin) {
			rootFigure.getLayoutManager().setConstraint(button, new Rectangle(250,50,220,30));
			rootFigure.add(button);
		}
		// if not a plugin, need ability to choose a file to animate
		if (!isPlugin) {
			rootFigure.getLayoutManager().setConstraint(newAnimButton, new Rectangle(450,10,40,20));
			rootFigure.add(newAnimButton);
		}
	}
	
	/** Draws new contents */
	public void update() {
		contents.removeAll();
		
		//get from model the object diagram representation of the graph.
		ArrayList<ODObject> odObjs = model.getODObjects();
		ArrayList<ODLink> odLinks = model.getODLinks();
		
		//if a model is not currently loaded then don't continue
		if (odObjs==null || odLinks==null) return;
		
		addNavControls();
		
		HashMap<ODObject, UMLClassFigure> classFigureMap = new HashMap<ODObject, UMLClassFigure>();
		for (ODObject odObj : odObjs) {
			UMLClassFigure classFigure = new UMLClassFigure(odObj);
			for (ODAttribute attr : odObj.getAttributes()) {
				classFigure.addAttribute(attr.getName(), attr.getValue());
			}
			if (odObj.getEventMode()) {
				for (ODEvent event : odObj.getEventPool()) {
					classFigure.addEvent(event.getName(), transListener, event);
				}
			} else {
				for (ODAction action : odObj.getActionPool()) {
					classFigure.addAction(action.getName(), transListener, action.getActive(), action);
				}
			}
			for (ODEvent extevent : odObj.getExternalEvents()) {
				classFigure.addExternalEvent(extevent, popupListener);
			}
			classFigure.addMouseListener(new MenuOpenListener(classFigure));
			classFigureMap.put(odObj, classFigure);
			contents.getLayoutManager().setConstraint(classFigure,
					new Rectangle(odObj.getLocation().x,odObj.getLocation().y,-1,-1));
			contents.add(classFigure);
		}
		//create links
		for (ODLink odLink : odLinks) {
			PolylineConnection c = new PolylineConnection();
			ChopboxAnchor sourceAnchor = new ChopboxAnchor(classFigureMap.get(odLink.getLeftObj()));
			ChopboxAnchor targetAnchor = new ChopboxAnchor(classFigureMap.get(odLink.getRightObj()));
			c.setSourceAnchor(sourceAnchor);
			c.setTargetAnchor(targetAnchor);
			// Add labels to the connections
			ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(c, true);
			targetEndpointLocator.setUDistance(5);		//distance from class box
			targetEndpointLocator.setVDistance(5);		//distance from association line
			Label targetMultiplicityLabel = new Label(odLink.getLinkLeftLabel());
			c.add(targetMultiplicityLabel, targetEndpointLocator);
			ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(c, false);
			sourceEndpointLocator.setUDistance(5);
			Label sourceMultiplicityLabel = new Label(odLink.getLinkRightLabel());
			c.add(sourceMultiplicityLabel, sourceEndpointLocator);
			ConnectionLocator middlelocator = new ConnectionLocator(c, ConnectionLocator.MIDDLE);
			middlelocator.setRelativePosition(PositionConstants.NORTH);
			middlelocator.setGap(5);
			Label relationshipLabel = new Label(odLink.getLinkCentreLabel());
			c.add(relationshipLabel, middlelocator);
			contents.add(c);
		}
		contents.revalidate();
	}
	
	class MenuOpenListener implements MouseListener {	//TODO
		private UMLClassFigure host;
		MenuOpenListener(UMLClassFigure host) {
			this.host = host;
		}
		public void mousePressed(MouseEvent me) {
			if (me.button == 3) {		//right-click: display popup menu
				Point menupos = new Point(me.x,me.y);
				menupos.x *= contents.getScale();
				menupos.y *= contents.getScale();
				if (isPlugin) {
					Point pluginViewInset = parent.getParent().getLocation();
					menupos.x += pluginViewInset.x;
					menupos.y += pluginViewInset.y;
				}
				menupos = parent.getShell().toDisplay(menupos);
		        Menu menu = new Menu (parent.getShell(), SWT.POP_UP);
		        host.buildPopupMenu(menu);
		        menu.setLocation(menupos);
		        menu.setVisible (true);
			}
		}
		public void mouseReleased(MouseEvent me) {}
		public void mouseDoubleClicked(MouseEvent me) {}
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
	
	/** Create and attach navigation controls to rootFigure */
	private void addNavControls() {
		ClickableLabel left = new ClickableLabel("<");
		ClickableLabel right = new ClickableLabel(">");
		ClickableLabel up = new ClickableLabel("/\\");
		ClickableLabel down = new ClickableLabel("\\/");
		ClickableLabel zoomin = new ClickableLabel("+");
		ClickableLabel zoomout = new ClickableLabel("-");
		Font boldfont = new Font(null, "Arial", 13, SWT.BOLD);
		Font largeboldfont = new Font(null, "Arial", 15, SWT.BOLD);
		left.setFont(boldfont); right.setFont(boldfont);
		up.setFont(boldfont); down.setFont(boldfont);
		zoomin.setFont(largeboldfont); zoomout.setFont(largeboldfont);
		Color cyan = new Color(null,0,255,255);
		left.setBackgroundColor(cyan); right.setBackgroundColor(cyan);
		up.setBackgroundColor(cyan); down.setBackgroundColor(cyan);
		zoomin.setBackgroundColor(cyan); zoomout.setBackgroundColor(cyan);
		left.setOpaque(true); right.setOpaque(true); up.setOpaque(true); down.setOpaque(true);
		zoomin.setOpaque(true); zoomout.setOpaque(true);
		final double moveunit = 20.0;
		final int move = (int)(moveunit/contents.getScale()+0.05);
		left.addMouseListener(new PanListener(new Point(move,0)));
		right.addMouseListener(new PanListener(new Point(-move,0)));
		up.addMouseListener(new PanListener(new Point(0,move)));
		down.addMouseListener(new PanListener(new Point(0,-move)));
		zoomin.addMouseListener(new ZoomListener(0.2));
		zoomout.addMouseListener(new ZoomListener(-0.2));
		final int l = 500;	//left of control
		final int t = 10;	//top of control
		final int w = 20;	//width of each 'button'
		rootFigure.getLayoutManager().setConstraint(left,    new Rectangle(l,t+w,w,w));
		rootFigure.getLayoutManager().setConstraint(right,   new Rectangle(l+w+w,t+w,w,w));
		rootFigure.getLayoutManager().setConstraint(up,      new Rectangle(l+w,t,w,w));
		rootFigure.getLayoutManager().setConstraint(down,    new Rectangle(l+w,t+w+w,w,w));
		rootFigure.getLayoutManager().setConstraint(zoomin,  new Rectangle(l+w*3+w/2,t+w/2,w,w));
		rootFigure.getLayoutManager().setConstraint(zoomout, new Rectangle(l+w*3+w/2,t+w/2+w,w,w));
		rootFigure.add(left);
		rootFigure.add(right);
		rootFigure.add(up);
		rootFigure.add(down);
		rootFigure.add(zoomin);
		rootFigure.add(zoomout);
	}
	
	private class PanListener implements MouseListener {
		Point translation;
		PanListener(Point translation) {
			this.translation = translation;
		}
		public void mouseDoubleClicked(MouseEvent me) {}
		public void mouseReleased(MouseEvent me) {}
		public void mousePressed(MouseEvent me) {
			ArrayList<ODObject> odObjs = model.getODObjects();
			for (ODObject odObj : odObjs) {
				Point currLoc = odObj.getLocation();
				odObj.setLocation(currLoc.x+translation.x, currLoc.y+translation.y);
			}
			update();
		}
	}
	private class ZoomListener implements MouseListener {
		Double zoomFactor;
		final double zoomMin = 0.1;
		final double zoomMax = 2.4;
		ZoomListener(Double zoomFactor) {
			this.zoomFactor = zoomFactor;
		}
		public void mouseDoubleClicked(MouseEvent me) {}
		public void mouseReleased(MouseEvent me) {}
		public void mousePressed(MouseEvent me) {
			Double zoom = contents.getScale() + zoomFactor;
			if (zoom < zoomMin) zoom = zoomMin;
			if (zoom > zoomMax) zoom = zoomMax;
			contents.setScale(zoom);
		}
	}
}