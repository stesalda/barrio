package nz.ac.massey.cs.barrio.actions;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SrcCodeReadAction {
	
	protected IJavaProject project = null;
	private IWorkbenchWindow window;
	

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		
        ProjectSelectDialog psd = new ProjectSelectDialog(window.getShell());
        List<IJavaProject> selectedProjects = psd.open();
        project = selectedProjects.get(0);
        
        if (project==null) {
			MessageDialog.openError(
					window.getShell(),
					"DependencyGraphExtractor Plug-in",
					"No Java project selected");
			return;
		}
	}


	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
	}
		
	public void dispose() {
	
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
