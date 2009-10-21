package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import model.PluginModel;
import model.TransitionAction;
import model.modelTransformer.objectDisplay.ODAction;
import model.modelTransformer.objectDisplay.ODEvent;
import model.modelTransformer.objectDisplay.ODObject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import view.ClickableLabel;
import view.View;
import view.contentDrawer.ObjectDiagFigure;

/**
 * Coordinates the view and model major components.
 *  
 * @author Kevin O'Shea
 */
public class Controller {
	
	private PluginModel model;
	private View view;
	
	/** Is the system running as a plugin or stand-alone. True if plugin, False if standalone */
	private Boolean isPlugin;
	
	/** Reference to controller instance, needed when running as plugin */
	private static Controller inst;
	/** In debug mode, graph states are output (as dot) at every transition */
	private final boolean debugmode;
	/** Path to directory for storing graph states */
	private final String graphOutputsPath = "GraphOutputs/";
	private final String gtsRulesPath = "GTSRules.xml";
	
	/**
	 * Set up model and view. Create and register listeners in the view.
	 */
	public Controller(final View theview, boolean plugin) {
		System.out.println("Controller is alive");
		
		inst = this;
		view = theview;
		isPlugin = plugin;
		debugmode = !isPlugin;	//only output dot files if standalone
		
		URL gtsRulesURL = null;
		if (isPlugin) {
			gtsRulesURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(gtsRulesPath), null);
		} else {
			try {
				gtsRulesURL = new URL("file:" + gtsRulesPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		try {
			model = new PluginModel(gtsRulesURL, debugmode, graphOutputsPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.setModel(model);
		
		//Animation button action - TODO - delete this later
		view.addAnimateListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				animate("microwaveTest1.modeltest");
			}
		});
		
		// 'New' animation button action
		view.addNewAnimListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String instancepath = view.openFileChooser();
				if (instancepath == null) return;
				animate(instancepath);
			}			
		});
		
		//Undo (<) button
		view.addUndoListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				model.undoAction();
				view.update();
			}
			public void mouseReleased(MouseEvent me) {}
		});
		
		//Redo (>) button
		view.addRedoListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				model.redoAction();
				view.update();
			}
			public void mouseReleased(MouseEvent me) {}
		});
		
		//Reset (<<) button
		view.addResetListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				view.reset();
				model.reset();
				view.update();
			}
			public void mouseReleased(MouseEvent me) {}
		});
		
		//Clicking on an action
		view.setTransitionListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				try {
					ClickableLabel sourcelbl = (ClickableLabel)me.getSource();
					TransitionAction transAction = (TransitionAction)sourcelbl.getData();
					model.transition(transAction);
					view.update();
				} catch (Exception e) {
					showError(e);
				}
			}
			public void mouseReleased(MouseEvent me) {}
		});
		
		//Clicking on a popup menu item (for generating external events)
		view.setPopupListener(new Listener() {
			public void handleEvent(Event event) {
				try {
					MenuItem sourceitem = (MenuItem)event.widget;
					TransitionAction transAction = (TransitionAction)sourceitem.getData();
					model.transition(transAction);
					view.update();
				} catch (Exception e) {
					showError(e);
				}
			}
		});
	}
	
	/** Execute animation, called when choosing file from plugin package explorer */
	public static void pluginAnimFileChosen(String instancepath) {
		inst.animate(instancepath);
	}
	
	/**
	 * Begin animation
	 */
	private void animate(String instancepath) {
		//take the first non-empty line of the instance file as the model path
		String modelpath = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(instancepath));
			String line = "";
			while((line=bf.readLine())!=null && modelpath.equals("")) {
				modelpath = line.trim();
			}
			if (modelpath.equals("")) {
				throw new FileNotFoundException("First line of test file must specify a model file to test.");
			}
			
			URL instanceURL = new URL("file:" + instancepath);
			URL modelURL = null;
			//first assume absolute path - if not found then try relative
			try {
				modelURL = new URL("file:" + modelpath);
				modelURL.getContent();
			} catch (IOException e) {
				try {
					modelURL = new URL(instanceURL, modelpath);
					modelURL.getContent();
				} catch (FileNotFoundException e2) {
					throw new FileNotFoundException("First line of test file must specify a " +
							"model file to test. Could not locate file: " + modelpath);
				} catch (IOException e3) {
					throw new IOException("Error reading file: " + modelpath);
				}
			}
			
			model.reset();
			view.reset();

			//Build the graph, using the collected model/instance information
			model.buildGraph(modelURL, instanceURL);
			
			//then tell view to draw the OD that PluginModel will have created.
			view.update();
			
		} catch(Exception e) {
			showError(e);
		}
	}
	
	private void showError(Exception e) {
		if (e!=null && e.getMessage()!=null){
			view.showError(e.getMessage());
		} else {
			view.showError("Internal Error Occurred");
		}
		e.printStackTrace();
	}
	
	/**
	 * Run the Animator as a stand-alone application
	 */
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setSize(650, 420);
		shell.setText("Animator");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
				
		//run it
		new View(false).createPartControl(shell);
		
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();
	}
}
