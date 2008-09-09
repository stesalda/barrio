package nz.ac.massey.cs.barrio.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.jobs.GraphBuildingJob;
import nz.ac.massey.cs.barrio.srcReader.KnownSourceReader;
import nz.ac.massey.cs.barrio.srcReader.SourceReader;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportAction implements IWorkbenchWindowActionDelegate {

	private InputUI input;
	private OutputUI output;
	private byte[] projectOdem;
	
	
	public void run(IAction action) 
	{		
		init(null);
        ProjectSelectDialog psd = new ProjectSelectDialog(new Shell());
        List<IJavaProject> projects = psd.open();
        
        FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
        
        List<SourceReader> readers = KnownSourceReader.all();
        final SourceReader reader = readers.get(0);
        final CustomJob srcReadingJob = (CustomJob) reader.getProjectReadingJob(projects, dialog.open());
        srcReadingJob.schedule();
    }


	
	public void init(IWorkbenchWindow window) {
		GuiGetter gg = new GuiGetter();
		input = gg.getInputUI();
	    output = gg.getOutputUI();
	}



	public void dispose() {
		// TODO Auto-generated method stub
		
	}



	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}


}
