package nz.ac.massey.cs.barrio.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.jobs.MainJob;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import edu.uci.ics.jung.graph.Graph;

public class MainAction implements IWorkbenchWindowActionDelegate{
	
	@Override
	public void run(IAction action) {
		
		Shell shell = new Shell();
		DirectoryDialog dlg = new DirectoryDialog(shell, SWT.OPEN);
	    String foldername = dlg.open();
	    shell.close();		
		MainJob job = new MainJob(new File(foldername));
		job.setUser(true);
		job.schedule();
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}
