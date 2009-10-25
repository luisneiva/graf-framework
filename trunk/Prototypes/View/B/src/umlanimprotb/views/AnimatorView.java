package umlanimprotb.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import SWTDiagRendering.UMLClassFigureTest;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class AnimatorView extends ViewPart {
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	public AnimatorView() {
	}

	public void createPartControl(Composite parent) {
    	//parent.setLayout(new RowLayout(SWT.VERTICAL)); //for some reason this line kills the canvas...
    	
    	/*
    	new Label (parent, SWT.HORIZONTAL).setText("My test view (composite)");
    	new Label (parent.getShell(), SWT.HORIZONTAL).setText("My test view (shell) (doesn't work)");
    	*/
    	
    	FigureCanvas canvas = new FigureCanvas(parent);
    	canvas.setLayout(new RowLayout(SWT.VERTICAL));
    	
    	/*
    	new Label (canvas, SWT.HORIZONTAL).setText("My test view (canvas)");
    	*/
    	
    	LightweightSystem lws = new LightweightSystem(canvas);
    	Figure contents = UMLClassFigureTest.GiveMeTheFigure();
    	contents.add(new org.eclipse.draw2d.Label("My test view (canvas contents)"));
    	lws.setContents(contents);
    	
    	/*
    	new Label (parent, SWT.HORIZONTAL).setText("End of test view (composite)");
    	*/
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
}