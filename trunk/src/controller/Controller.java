package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFileChooser;

import model.PluginModel;
import model.TransitionAction;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import view.ClickableLabel;
import view.View;

/**
 * Coordinates the view and model major components.
 *  
 * @author Kevin O'Shea
 * @author Oscar Wood
 * @author Frank Su
 */
public class Controller {

	/** Model of the system */
	private PluginModel model;
	/** View for the system */
	private static View view;

	/** Is the system running as a plugin or stand-alone. True if plugin, False if standalone */
	private Boolean isPlugin;

	/** Reference to controller instance, needed when running as plugin */
	private static Controller inst;

	/** Path to directory for storing graph states */
	private final String graphOutputsPath = "GraphOutputs/";
	private String gtsRulesPath = Properties.getProperty("gtsRulesPath");  //"GTSRules.ggx";
	
	private String gtsRulesSeqPath = Properties.getProperty("gtsRulesSeqPath");  //"GTSRulesSeq.xml";

	/**
	 * Set up model and view. Create and register listeners in the view.
	 */
	public Controller(final View theview, boolean plugin) {
		inst = this;
		view = theview;
		isPlugin = plugin;
		try {
			String gtsRulesFilePath = "";
			String gtsRulesSeqFilePath = "";
			if (isPlugin) {
				Bundle bundle = Activator.getDefault().getBundle();
				URL gtsRulesRelURL = FileLocator.find(bundle, new Path(gtsRulesPath), null);
				URL gtsRulesSeqRelURL = FileLocator.find(bundle, new Path(gtsRulesSeqPath), null);
				if (gtsRulesRelURL==null)
					throw new IOException("Cannot locate file: " + gtsRulesPath);
				if (gtsRulesSeqRelURL==null)
					throw new IOException("Cannot locate file: " + gtsRulesSeqPath);
				URL gtsRulesURL = FileLocator.toFileURL(gtsRulesRelURL);
				URL gtsRulesSeqURL = FileLocator.toFileURL(gtsRulesSeqRelURL);
				gtsRulesFilePath = gtsRulesURL.getPath();
				gtsRulesSeqFilePath = gtsRulesSeqURL.getPath();
			} else {
				if (new File(gtsRulesPath).exists()==false)
				{
					JFileChooser fc2 = new JFileChooser();
					fc2.setDialogTitle("Choose a Rule:");
					fc2.showOpenDialog(null);
					gtsRulesPath = fc2.getSelectedFile().getAbsolutePath();
					
					Properties.setProperty("gtsRulesPath", gtsRulesPath);
					Properties.rewritePropertiesFile();
//					throw new IOException("Cannot locate file: " + gtsRulesPath);
				}
				if (new File(gtsRulesSeqPath).exists()==false)
				{
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Choose a Rule Seq:");
					fc.showOpenDialog(null);
					gtsRulesSeqPath = fc.getSelectedFile().getAbsolutePath();
					
					Properties.setProperty("gtsRulesSeqPath", gtsRulesSeqPath);
					Properties.rewritePropertiesFile();
//					throw new IOException("Cannot locate file: " + gtsRulesSeqPath);
				}
				gtsRulesFilePath = gtsRulesPath;
				gtsRulesSeqFilePath = gtsRulesSeqPath;
			}
			model = new PluginModel(gtsRulesFilePath, gtsRulesSeqFilePath, graphOutputsPath);
			view.setModel(model);
		} catch (Exception e) {
			showError(e);
		}

		//Undo (<) button
		view.addUndoListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				try {
					model.undoAction();
					view.update();
				} catch (Exception e) {
					showError(e);
				}
			}
			public void mouseReleased(MouseEvent me) {}
		});

		//Redo (>) button
		view.addRedoListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				try {
					model.redoAction();
					view.update();
				} catch (Exception e) {
					showError(e);
				}
			}
			public void mouseReleased(MouseEvent me) {}
		});

		//Reset (<<) button
		view.addResetListener(new MouseListener() {
			public void mouseDoubleClicked(MouseEvent me) {}
			public void mousePressed(MouseEvent me) {
				try {
					view.reset();
					model.reset();
					view.update();
				} catch (Exception e) {
					showError(e);
				}
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
	public void animate(String instancepath) {
		//take the first non-empty line of the instance file as the model path
		String modelpath = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(instancepath));
			String line = "";
			while((line=bf.readLine())!=null && modelpath.equals("")) {
				modelpath = line.trim();
			}
			if (modelpath.equals("")) {
				showError(new FileNotFoundException(
				"First line of test file must specify a model file to test."));
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
				} catch (FileNotFoundException ex) {
					showError(new FileNotFoundException("First line of test file must specify a " +
							"model file to test. Could not locate file: " + modelpath));
				} catch (IOException ex) {
					showError(new IOException("Error reading file: " + modelpath));
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
	 * Add a menu bar to the top.
	 * Hot keys are ctrl+n for new, ctrl+z for undo, ctrl+y for redo, ctrl+r for reset.
	 * @param shell  this shell window
	 * @param d  this display
	 */
	public void addMenuBar(final Shell shell, final Display d)
	{
		Menu menuBar, fileMenu, editMenu, helpMenu, properitesMenu;
		MenuItem fileMenuHeader, editMenuHeader, helpMenuHeader, properitesMenuHeader;
		MenuItem fileExitItem, fileNewItem, helpGetHelpItem, undoItem, redoItem, resetItem;
		final MenuItem properitesPrintDebugItem, properitesPrintRulesItem, properitesPrintGraphsItem;

		menuBar = new Menu(shell, SWT.BAR);
		fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");

		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		fileNewItem = new MenuItem(fileMenu, SWT.PUSH);
		fileNewItem.setText("&New\tCtrl+N");
		fileNewItem.setAccelerator(SWT.CTRL + 'N');

		fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("&Exit");

		editMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		editMenuHeader.setText("&Edit");

		editMenu = new Menu(shell, SWT.DROP_DOWN);
		editMenuHeader.setMenu(editMenu);

		undoItem = new MenuItem(editMenu, SWT.PUSH);
		undoItem.setText("&Undo\tCtrl+Z");
		undoItem.setAccelerator(SWT.CTRL + 'Z');

		redoItem = new MenuItem(editMenu, SWT.PUSH);
		redoItem.setText("&Redo\tCtrl+Y");
		redoItem.setAccelerator(SWT.CTRL + 'Y');

		resetItem = new MenuItem(editMenu, SWT.PUSH);
		resetItem.setText("&Reset\tCtrl+R");
		resetItem.setAccelerator(SWT.CTRL + 'R');


		properitesMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		properitesMenuHeader.setText("&Properites");
		
		helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("&Help");

		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader.setMenu(helpMenu);

		helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
		helpGetHelpItem.setText("&Get Help");

		properitesMenu = new Menu(shell, SWT.DROP_DOWN);
		properitesMenuHeader.setMenu(properitesMenu);

		properitesPrintRulesItem = new MenuItem(properitesMenu, SWT.PUSH);
		Boolean printRules = Boolean.parseBoolean(Properties.getProperty("PrintRules"));
		if(printRules) {
			properitesPrintRulesItem.setText("Turn Off Print Rules");
		} else {
			properitesPrintRulesItem.setText("Turn On Print Rules");
		}

		properitesPrintGraphsItem = new MenuItem(properitesMenu, SWT.PUSH);
		Boolean printGraphs = Boolean.parseBoolean(Properties.getProperty("PrintGraphs"));
		if(printGraphs) {
			properitesPrintGraphsItem.setText("Turn Off Print Graphs");
		} else {
			properitesPrintGraphsItem.setText("Turn On Print Graphs");
		}

		properitesPrintDebugItem = new MenuItem(properitesMenu, SWT.PUSH);
		Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
		if(printDebug) {
			properitesPrintDebugItem.setText("Turn Off Print Debug");
		} else {
			properitesPrintDebugItem.setText("Turn On Print Debug");
		}

		shell.setMenuBar(menuBar);

		// file->exit
		fileExitItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				shell.close();
				d.dispose();
			}

			public void widgetSelected(SelectionEvent e) {
				shell.close();
				d.dispose();
			}
		});

		// file->new
		fileNewItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				String instancepath = view.openFileChooser();

				// save file path in string to Properties java class.
				Properties.filePathStr = instancepath;

				if (instancepath == null) return;
				animate(instancepath);
			}			
		});

		helpGetHelpItem.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent e) {
			}			
		});

		// undo
		undoItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {

				try {
					model.undoAction();
					view.update();
				} catch (Exception exception) {
					showError(exception);
				}

			}			
		});

		// redo
		redoItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {

				try {
					model.redoAction();
					view.update();
				} catch (Exception exception) {
					showError(exception);
				}
			}			
		});

		// reset
		resetItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				try {
					view.reset();
					model.reset();
					view.update();
				} catch (Exception exception) {
					showError(exception);
				}

			}			
		});


		//Properties->PrintRules
		properitesPrintRulesItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				Boolean printRules = Boolean.parseBoolean(Properties.getProperty("PrintRules"));
				if(printRules){
					Properties.setProperty("PrintRules", "false");
					properitesPrintRulesItem.setText("Turn On Print Rules");	
				} else {
					Properties.setProperty("PrintRules", "true");
					properitesPrintRulesItem.setText("Turn Off Print Rules");
				}

				try {
					Properties.rewritePropertiesFile();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}			
		});


		//Properties->PrintGraphs
		properitesPrintGraphsItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				Boolean printGraphs = Boolean.parseBoolean(Properties.getProperty("PrintGraphs"));
				if(printGraphs){
					Properties.setProperty("PrintGraphs", "false");
					properitesPrintGraphsItem.setText("Turn On Print Graphs");	
				} else {
					Properties.setProperty("PrintGraphs", "true");
					properitesPrintGraphsItem.setText("Turn Off Print Graphs");
				}

				try {
					Properties.rewritePropertiesFile();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}			
		});


		//Properties->PrintDebug
		properitesPrintDebugItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				Boolean printDebug = Boolean.parseBoolean(Properties.getProperty("PrintDebug"));
				if(printDebug){
					Properties.setProperty("PrintDebug", "false");
					properitesPrintDebugItem.setText("Turn On Print Debug");	
				} else {
					Properties.setProperty("PrintDebug", "true");
					properitesPrintDebugItem.setText("Turn Off Print Debug");
				}

				try {
					Properties.rewritePropertiesFile();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}			
		});
	}


	/**
	 * Run the Animator as a stand-alone application
	 */
	public static void main(String[] args) {

		long time = System.currentTimeMillis();

		final Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setSize(650, 420);
		shell.setText("Animator");
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		//run it
		View view = new View(false, shell);
		view.createPartControl(shell);
		view.openFirst();

		long time1 = System.currentTimeMillis();
		shell.open();
		long time2 = System.currentTimeMillis();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();

		//Time how long the application runs for
		long time3 = System.currentTimeMillis();
		long setupTime = time2 - time1;
		long totalTime = time3 - time;
		System.out.println("Total setup time: " + setupTime + " milliseconds");
		System.out.println("Total execution time: " + totalTime + " milliseconds\n");
	}
}
