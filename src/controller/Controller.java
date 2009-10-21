package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import model.PluginModel;
import model.objectDiagram.ODAction;
import model.objectDiagram.ODEvent;
import model.objectDiagram.ODObject;

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
import view.UMLClassFigure;
import view.View;

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
	/** Path to the normalized metamodel xmi file */ 	//TODO - delete this later
	private final String metamodelpath = "UMLMetamodel/08-05-12.xmi";
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
		
		URL metamodelURL = null;
		URL gtsRulesURL = null;
		if (isPlugin) {
			metamodelURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(metamodelpath), null);
			gtsRulesURL = FileLocator.find(Activator.getDefault().getBundle(), new Path(gtsRulesPath), null);
		} else {
			try {
				metamodelURL = new URL("file:" + metamodelpath);
				gtsRulesURL = new URL("file:" + gtsRulesPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		try {
			model = new PluginModel(metamodelURL, gtsRulesURL, debugmode, graphOutputsPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.setModel(model);
		
		//Animation button action - TODO - delete this later
		view.addAnimateListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				animate("microwaveTest1.umltest");
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
		
		//Clicking on an event/action action
		view.setTransitionListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				try {
					ClickableLabel sourcelbl = (ClickableLabel)me.getSource();
					UMLClassFigure figure = (UMLClassFigure)sourcelbl.getParent().getParent();
					ODObject obj = figure.getObj();
					if (obj.getEventMode()) {
						ODEvent event = (ODEvent)sourcelbl.getData();
						model.transition(obj, "AcceptEventAction", event.getOccurence());
					} else {
						ODAction action = (ODAction)sourcelbl.getData();
						model.transition(obj, action.getType(), sourcelbl.getText());
					}
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
					UMLClassFigure figure = (UMLClassFigure)sourceitem.getData();
					ODObject obj = figure.getObj();
					model.transition(obj,"ActorSendSignal", sourceitem.getText());
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
				} catch (IOException e2) {
					throw new FileNotFoundException("Could not locate model file: " + modelpath);
				}
			}
			
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
