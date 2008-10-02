package nz.ac.massey.cs.barrio.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.jobs.GraphBuildingJob;
import nz.ac.massey.cs.barrio.jobs.GraphFilteringJob;
import nz.ac.massey.cs.barrio.srcReader.KnownSourceReader;
import nz.ac.massey.cs.barrio.srcReader.SourceReader;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class SrcCodeReadAction implements IWorkbenchWindowActionDelegate {

	private InputUI input;
	private OutputUI output;
	private byte[] projectOdem;
	
	
	public void run(IAction action) 
	{		
		init(null);
        ProjectSelectDialog psd = new ProjectSelectDialog(new Shell());
        List<IJavaProject> projects = psd.open();
        
        List<SourceReader> readers = KnownSourceReader.all();
        final SourceReader reader = readers.get(0);
        final CustomJob srcReadingJob = (CustomJob) reader.getProjectReadingJob(projects);
        srcReadingJob.addJobChangeListener(new IJobChangeListener(){
        	public void aboutToRun(IJobChangeEvent event) {}
			public void awake(IJobChangeEvent event) {}
			public void running(IJobChangeEvent event) {}
			public void scheduled(IJobChangeEvent event) {}
			public void sleeping(IJobChangeEvent event) {}			
			public void done(IJobChangeEvent event) 
			{
				projectOdem = srcReadingJob.getBuffer();
				setNextJob();
			}
        	
        });
        srcReadingJob.schedule();
    }
	
	
	protected void setNextJob() {
		final GraphBuildingJob buildingJob = new GraphBuildingJob(projectOdem);
		buildingJob.setUser(true);
		buildingJob.addJobChangeListener(new IJobChangeListener(){
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
		                        	input.checkDisplayGraphClick();
		                        	input.setClustered(false);
		                        	input.setDoneFilters();
		                        	input.setInteractiveElements();
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
	


	public void selectionChanged(IAction action, ISelection selection) {
		
	}
		
	public void dispose() {
	
	}


	
	public void init(IWorkbenchWindow window) {
		GuiGetter gg = new GuiGetter();
		input = gg.getInputUI();
	    output = gg.getOutputUI();
	}

}
