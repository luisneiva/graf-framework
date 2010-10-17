package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import model.PluginModel;
import model.modelTransformer.objectDisplay.ObjectDisplay;

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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import view.contentDrawer.ContentDrawer;
import view.contentDrawer.ObjectDiagDrawer;
import controller.Controller;
import controller.Properties;

/**
 * Provides display for system.
 * 
 * @author Kevin O'Shea
 */

public class View extends ViewPart {

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

	/** Button to choose model to animate */
	//	private Button newAnimButton = new Button("New");

	private ClickableLabel undo = new ClickableLabel("<");
	private ClickableLabel redo = new ClickableLabel(">");
	private ClickableLabel reset = new ClickableLabel("<<");

	/** Default constructor called by plugin runtime. Do not call this if standalone.
	 * @see View(plugin)
	 */
	//public View() {
	//	this(true);
	//}

	public Controller controller;
	/** Construct View
	 * @param plugin Is the system running as a plugin
	 */
	public View(boolean plugin, Shell parent) {
		contentDrawer = new ObjectDiagDrawer(this);
		try {
			Properties.readProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		parent.addDisposeListener(new DisposeListener (){
			public void widgetDisposed(DisposeEvent e){
				System.exit(0);
			}
		});
		controller = new Controller(this, plugin); 
		final Shell sh = parent.getShell();
		final Display d = parent.getDisplay();
		controller.addMenuBar(sh, d);
	}


	/**
	 * Open file chooser automatically, as soon as Graf starts.
	 */
	public void openFirst() {
		String instancepath = openFileChooser();

		// TODO!!! file path in string.
		Properties.filePathStr = instancepath;


		if (instancepath == null) 
			return;
		controller.animate(instancepath);		
	}

	/** Set the model for use by this view */
	public void setModel(PluginModel model) {
		this.model = model;
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

	/**
	 * Opening a file chooser should be just 2 lines of code. But that won't make the
	 * file chooser always on top. To ensure the file chooser is always on top, this
	 * unfortunate hack is needed 
	 */
	private class SelectListener implements ActionListener {
		String result;
		JFileChooser chooser;
		JFrame frame;
		
		public SelectListener(JFileChooser chooser, JFrame frame) {
			this.chooser = chooser;
			this.frame = frame;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				result = chooser.getSelectedFile().getAbsolutePath();
			} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
				result = null;
			}
			
			frame.dispose();
		//	JOptionPane.showMessageDialog(null, getSelectedFile());
		}
		
	}
	
	/** Open a file chooser and return path to chosen file, or null if none chosen */
	public String openFileChooser() {
/*
		JFrame frame = new JFrame("Select a modeltest file");

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("Models"));
		chooser.addChoosableFileFilter(new FileFilter() {
			
			public String getDescription() {
				return null;
			}
			public boolean accept(File arg0) {
				return arg0.getName().contains(".modeltest")
				|| arg0.isDirectory();
			}
		});
		
		SelectListener listener = new SelectListener(chooser, frame);
		
		chooser.addActionListener(listener);

		frame.add(chooser);
		frame.pack();

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);

		//make it wait for the user to make a selection
		chooser.showOpenDialog(frame);
		
		return listener.result;
*/
		FileDialog filedialog = new FileDialog(parent.getShell());
		filedialog.setText("Choose a uml model:");
		filedialog.setFilterPath("Models");
		filedialog.setFilterExtensions(new String[]{"*.modeltest"});
		return filedialog.open();
	}

	/** Open a file chooser and return path to chosen file, or null if none chosen */
	public String openFileChooser(String prompt) {
		//TODO: Add file chooser code
		FileDialog filedialog = new FileDialog(parent.getShell());
		filedialog.setText(prompt);
		filedialog.setFilterPath("Models");
		filedialog.setFilterExtensions(new String[]{"*.modeltest"});
		return filedialog.open();
	}

	/** Display error message to user */
	// TODO!!!
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

		// if not a plugin, need ability to choose a file to animate
		//		if (!isPlugin) {
		//			rootFigure.getLayoutManager().setConstraint(newAnimButton, new Rectangle(430,10,40,20));
		//			rootFigure.add(newAnimButton);
		//		}
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