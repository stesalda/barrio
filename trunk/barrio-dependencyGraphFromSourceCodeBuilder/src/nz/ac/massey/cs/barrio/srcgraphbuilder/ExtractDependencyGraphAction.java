/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.barrio.srcgraphbuilder;

import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.dialogs.ProjectSelectDialog;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Action to extract the dependency graph from a Java project.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */
public class ExtractDependencyGraphAction implements IWorkbenchWindowActionDelegate {
	protected IJavaProject project = null;
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public ExtractDependencyGraphAction() {
		
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {		
//		FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
//        fd.setText("Save");
//        String[] filterExt = { "*.odem", "*.xml"};
//        fd.setFilterExtensions(filterExt);
//        fd.setFileName(project.getElementName()+".odem");
//        String fileName = fd.open();
//        if (fileName==null) {
//        	System.out.println("cancelled");
//        	return;
//        }
        
		String fileName = "tempOdem.odem";
        
        
        //Introducing project select dialog ...Slava
        ProjectSelectDialog psd = new ProjectSelectDialog(window.getShell());
        List<IJavaProject> selectedProjects = psd.open();
        project = selectedProjects.get(0);
        for(IJavaProject p:selectedProjects)
        {
        	System.out.println(p.getElementName());
        }
        //project select dialog ends
        
        if (project==null) {
			MessageDialog.openError(
					window.getShell(),
					"DependencyGraphExtractor Plug-in",
					"No Java project selected");
			return;
		}
        
        

        ExtractDependencyGraph2OdemFileJob job = new ExtractDependencyGraph2OdemFileJob(project);
		job.setFileName(fileName);
		job.schedule();
		/*
		MessageDialog.openInformation(
			window.getShell(),
			"DependencyGraphExtractor Plug-in",
			"Project analysed");
			*/
	}


	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
		
//		if (selection.isEmpty()){
//			// we keep the selected project
//			action.setEnabled(false);
//		}			
//		else if (selection instanceof IStructuredSelection) {
//			project = null;
//			// a selection containing elements
//			Iterator it = ((IStructuredSelection) selection).iterator();
//			while (project==null && it.hasNext()) {
//				// we should have an IJavaProject etc here
//				Object obj = it.next();
//				if (obj instanceof IJavaProject) {
//					project = (IJavaProject) obj;
//				}
//				else if (obj instanceof IJavaElement) {
//					project = ((IJavaElement)obj).getJavaProject();
//				}
//				else if (obj instanceof IResource) {
//					// try to find associated java project
//					try {
//						IResource res = (IResource)obj;	
//						res = res.getProject();
//						IWorkspace workspace = res.getWorkspace();
//						IJavaModel jmodel = JavaCore.create(workspace.getRoot());
//						IJavaProject[] jprojects = jmodel.getJavaProjects();
//						for (int i=0;i<jprojects.length;i++) {
//							if (jprojects[i].getResource()==res)
//								project=jprojects[i];
//						}
//					}
//					catch (Exception x) {}
//				}
//			}
//		}
//		action.setEnabled(project!=null);
//		// System.out.println("Selected project is : " + project);
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
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