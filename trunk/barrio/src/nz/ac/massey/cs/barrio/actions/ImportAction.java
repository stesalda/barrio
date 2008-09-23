package nz.ac.massey.cs.barrio.actions;

import java.io.File;
import java.util.ArrayList;

import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.jobs.GraphBuildingJob;
import nz.ac.massey.cs.barrio.jobs.GraphFilteringJob;

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

	public void init(IWorkbenchWindow window) {
		GuiGetter gg = new GuiGetter();
	    input = gg.getInputUI();
	    output = gg.getOutputUI();
	}

	public void selectionChanged(IAction action, ISelection selection) {}

	public void run(IAction action) {
		init(null);
				
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "ODEM Files","XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.odem", "*.xml", "*.*" });
	    String filename = dlg.open();
	    shell.close();
	    
	    final GraphBuildingJob buildingJob = new GraphBuildingJob(new File(filename));
	    buildingJob.setUser(true);
	    buildingJob.addJobChangeListener(new IJobChangeListener()
	    {
			public void aboutToRun(IJobChangeEvent event) {}
			public void awake(IJobChangeEvent event) {}
			public void running(IJobChangeEvent event) {}
			public void scheduled(IJobChangeEvent event) {}
			public void sleeping(IJobChangeEvent event) {}

			public synchronized void done(IJobChangeEvent event) 
			{
				input.setInitGraph(buildingJob.getInitGraph());
				final GraphFilteringJob filteringJob = new GraphFilteringJob(buildingJob.getInitGraph(), input.getActiveFilters());
				filteringJob.setUser(true);
				filteringJob.addJobChangeListener(new IJobChangeListener()
				{
					public void aboutToRun(IJobChangeEvent event) {}
					public void awake(IJobChangeEvent event) {}
					public void running(IJobChangeEvent event) {}
					public void scheduled(IJobChangeEvent event) {}
					public void sleeping(IJobChangeEvent event) {}

					@SuppressWarnings("unchecked")
					public synchronized void done(IJobChangeEvent event) 
					{
						input.setFilteredGraph(filteringJob.getFilteredGraph());
						input.setRemovedEdges(new ArrayList());
						output.getDisplay().asyncExec(new Runnable()
		                {
		                        public void run() 
		                        {
		                        	output.updateOutputs(filteringJob.getFilteredGraph());
		                        }
		                });
					}
				});
				filteringJob.schedule();
			}
	    });
	    buildingJob.schedule();
	}
}
