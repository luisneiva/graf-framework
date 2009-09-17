package view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import controller.Controller;

public class PackExpContrib extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
		Object firstElement = selection.getFirstElement();
		IFile file = (IFile)firstElement;
		//Show the Animator view
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage().showView("view.View");
		} catch (Exception e) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"Information", "Could not display Animator view");
		}
		//Run animation on chosen file
		Controller.pluginAnimFileChosen(file.getLocation().toOSString());
		return null;
	}
}
