package view;

import model.PluginModel;
import model.modelTransformer.objectDisplay.ObjectDisplay;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
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

import view.contentDrawer.ContentDrawer;
import view.contentDrawer.ObjectDiagDrawer;
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
	
	/** Draws model contents */
	private ContentDrawer contentDrawer;
	
	//Random button for testing		//TODO - delete this
	private Button button = new Button("Animate \"microwaveTest1.modeltest\"");
	/** Button to choose model to animate */
	private Button newAnimButton = new Button("New");
	
	private ClickableLabel undo = new ClickableLabel("<");
	private ClickableLabel redo = new ClickableLabel(">");
	private ClickableLabel reset = new ClickableLabel("<<");
	
	/** Default constructor called by plugin runtime. Do not call this if standalone.
	 * @see View(plugin)
	 */
	public View() {
		this(true);
	}

	/** Construct View
	 * @param plugin Is the system running as a plugin
	 */
	public View(boolean plugin) {
		isPlugin = plugin;
		contentDrawer = new ObjectDiagDrawer(this);
		@SuppressWarnings("unused")
		Controller controller = new Controller(this, plugin);
	}

	/** Set the model for use by this view */
	public void setModel(PluginModel model) {
		this.model = model;
	}
	
	/** Add listener to the 'Animate modeltest' button */	//TODO - delete this
	public void addAnimateListener(ActionListener animListener) {
		button.addActionListener(animListener);
	}

	/** Add listener to the 'New' button */
	public void addNewAnimListener(ActionListener newListener) {
		newAnimButton.addActionListener(newListener);
	}

	/** Add listener to the 'Undo' button */
	public void addUndoListener(MouseListener resetListener) {
		undo.addMouseListener(resetListener);
	}
	/** Add listener to the 'Redo' button */
	public void addRedoListener(MouseListener resetListener) {
		redo.addMouseListener(resetListener);
	}
	/** Add listener to the 'Reset' button */
	public void addResetListener(MouseListener resetListener) {
		reset.addMouseListener(resetListener);
	}
	
	/** Set the listener for when transition-sources are pressed */
	public void setTransitionListener(MouseListener transListener) {
		contentDrawer.setTransListener(transListener); 
	}

	/** Set the listener for when popup menu items are selected */
	public void setPopupListener(Listener popupListener) {
		contentDrawer.setPopupListener(popupListener);
	}

	/** Reset display for new animation */
	public void reset() {
		contents.removeAll();
		contents.setScale(1.0);
	}
	
	/** Open a file chooser and return path to chosen file, or null if none chosen */
	public String openFileChooser() {
		FileDialog filedialog = new FileDialog(parent.getShell());
		filedialog.setFilterExtensions(new String[]{"*.modeltest"});
		return filedialog.open();
	}
	
	/** Display error message to user */
	public void showError(String msg) {
		if (parent == null) {
			MessageDialog.openError(null, "Animator Error", msg);
		} else {
			MessageDialog.openError(parent.getShell(), "Animator Error", msg);
		}
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
		
		contentDrawer.setContents(contents);
		
		//set up the menu (button(s))
		//TODO - delete this 'button' - just for testing (standalone onlys)
		if (!isPlugin) {
			rootFigure.getLayoutManager().setConstraint(button, new Rectangle(250,50,220,30));
			rootFigure.add(button);
		}
		// if not a plugin, need ability to choose a file to animate
		if (!isPlugin) {
			rootFigure.getLayoutManager().setConstraint(newAnimButton, new Rectangle(430,10,40,20));
			rootFigure.add(newAnimButton);
		}
	}
	
	private boolean firstLoaded = true;
	/** Draws new contents */
	public void update() {
		contents.removeAll();
		
		ObjectDisplay objdisplay = model.getObjectDisplay();
		
		//if a model is not currently loaded then don't continue
		if (objdisplay==null) return;
		
		if (firstLoaded) {
			addNavControls();
			firstLoaded = false;
		}
		if (model.getUndosRemaining()>0) undo.setEnabled(true);
		else undo.setEnabled(false);
		if (model.getRedosRemaining()>0) redo.setEnabled(true);
		else redo.setEnabled(false);
		undo.repaint();
		redo.repaint();
		
		contentDrawer.draw(objdisplay);
		
		contents.revalidate();
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
	
	/** Create menu at the specified location
	 * @return Reference to the menu created 
	 */
	public Menu createPopupMenu(Point menupos) {
		menupos.x *= contents.getScale();
		menupos.y *= contents.getScale();
		menupos = parent.toDisplay(menupos);
        Menu menu = new Menu (parent.getShell(), SWT.POP_UP);
        menu.setLocation(menupos);
        menu.setVisible (true);
        return menu;
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
		undo.setFont(boldfont); redo.setFont(boldfont); reset.setFont(boldfont);
		
		Color cyan = new Color(null,0,255,255);
		left.setBackgroundColor(cyan); right.setBackgroundColor(cyan);
		up.setBackgroundColor(cyan); down.setBackgroundColor(cyan);
		zoomin.setBackgroundColor(cyan); zoomout.setBackgroundColor(cyan);
		undo.setBackgroundColor(cyan); redo.setBackgroundColor(cyan);
		reset.setBackgroundColor(cyan);
		left.setOpaque(true); right.setOpaque(true); up.setOpaque(true); down.setOpaque(true);
		zoomin.setOpaque(true); zoomout.setOpaque(true);
		undo.setOpaque(true); redo.setOpaque(true); reset.setOpaque(true);
		
		final double moveunit = 20.0;
		final int move = (int)(moveunit/contents.getScale()+0.05);
		left.addMouseListener(new PanListener(new Point(move,0)));
		right.addMouseListener(new PanListener(new Point(-move,0)));
		up.addMouseListener(new PanListener(new Point(0,move)));
		down.addMouseListener(new PanListener(new Point(0,-move)));
		zoomin.addMouseListener(new ZoomListener(0.2));
		zoomout.addMouseListener(new ZoomListener(-0.2));
		//listeners for undo, redo, and reset are given through methods.
		
		final int l = 490;	//left of control
		final int t = 10;	//top of control
		final int w = 20;	//width of each 'button'
		rootFigure.getLayoutManager().setConstraint(left,    new Rectangle(l,t+w,w,w));
		rootFigure.getLayoutManager().setConstraint(right,   new Rectangle(l+w+w,t+w,w,w));
		rootFigure.getLayoutManager().setConstraint(up,      new Rectangle(l+w,t,w,w));
		rootFigure.getLayoutManager().setConstraint(down,    new Rectangle(l+w,t+w+w,w,w));
		rootFigure.getLayoutManager().setConstraint(zoomin,  new Rectangle(l+w*3+w/2,t+w/2-1,w,w));
		rootFigure.getLayoutManager().setConstraint(zoomout, new Rectangle(l+w*3+w/2,t+w/2+w,w,w));
		rootFigure.getLayoutManager().setConstraint(reset,   new Rectangle(l+w*5,t+w/2-1,w*2+1,w));
		rootFigure.getLayoutManager().setConstraint(undo,    new Rectangle(l+w*5,t+w/2+w,w,w));
		rootFigure.getLayoutManager().setConstraint(redo,    new Rectangle(l+w*6+1,t+w/2+w,w,w));
		rootFigure.add(left);
		rootFigure.add(right);
		rootFigure.add(up);
		rootFigure.add(down);
		rootFigure.add(zoomin);
		rootFigure.add(zoomout);
		rootFigure.add(undo);
		rootFigure.add(redo);
		rootFigure.add(reset);
	}
	
	/** Shifts the display by a given translation vector */
	private class PanListener implements MouseListener {
		Point translation;
		/** Construct listener to shift the display by the given translation vector */
		PanListener(Point translation) {
			this.translation = translation;
		}
		public void mouseDoubleClicked(MouseEvent me) {}
		public void mouseReleased(MouseEvent me) {}
		public void mousePressed(MouseEvent me) {
			model.getObjectDisplay().move(translation.x,translation.y);
			update();
		}
	}
	/** Zooms the display by a given zoom factor vector */
	private class ZoomListener implements MouseListener {
		Double zoomFactor;
		final double zoomMin = 0.1;
		final double zoomMax = 2.4;
		/** Construct listener to zoom the display by the given zoom factor */
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