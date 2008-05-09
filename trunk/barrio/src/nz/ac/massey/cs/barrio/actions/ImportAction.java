package nz.ac.massey.cs.barrio.actions;

import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputGenerator;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.jobs.GraphProcessingJob;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ImportAction implements IWorkbenchWindowActionDelegate{
	
	private InputUI input;
	private OutputUI output;

	public void dispose() {}

	public void init(IWorkbenchWindow window) {}

	public void selectionChanged(IAction action, ISelection selection) {}

	public void run(IAction action) {
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "ODEM Files","XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.odem", "*.xml", "*.*" });
	    String filename = dlg.open();
	    shell.close();
	    
	    GuiGetter gg = new GuiGetter();
	    input = gg.getInputUI();
	    output = gg.getOutputUI();
	    
	    final GraphProcessingJob job = new GraphProcessingJob(filename, null, input.getActiveFilters(), input.getSeparationLevel());
	    job.setOutput(output);
	    job.setUser(true);
	    input.setJob(job);
	    final Long start = System.currentTimeMillis();
	    job.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {}

			public void awake(IJobChangeEvent event) {}

			public void running(IJobChangeEvent event) {}

			public void scheduled(IJobChangeEvent event) {}

			public void sleeping(IJobChangeEvent event) {}

			public synchronized void done(IJobChangeEvent event) {
				paintDisplay(job);
				output.updateVisualElements(input.getVisualSettings());
				Long stop = System.currentTimeMillis();
				System.out.println("[ImportAction]: Time taken = "+(stop-start)/1000+" seconds");
			}
	    	
	    });
	    job.schedule();
	}
	
	
	private void paintDisplay(final GraphProcessingJob job)
	{
		output.getDisplay().asyncExec(new Runnable()
		{
			public void run() 
			{
				output.paintGraph(job.getDispaly());	
			}			
		});
	}
	
	
	private void updateOutputs(final GraphProcessingJob job)
	{
		output.getDisplay().asyncExec(new Runnable()
		{
			public void run() 
			{
				OutputGenerator og = new OutputGenerator(job.getInitGraph(), job.getFinalGraph());
				output.updateOutputs(og, job.getRemovedEdges());	
			}
			
		});
		
	}
}
