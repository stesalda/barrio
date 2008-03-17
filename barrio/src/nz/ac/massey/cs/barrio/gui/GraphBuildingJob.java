package nz.ac.massey.cs.barrio.gui;

import java.util.List;

import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLFile;

public class GraphBuildingJob extends Job {

	private String filename;
	private Graph initGraph;
	private boolean isCanceled;



	public GraphBuildingJob(String filename, Graph initGraph) {
		super("Building Graph");
		
		this.filename = filename;
		this.initGraph = initGraph;
		this.isCanceled = false;
	}
	
	
	
	@Override
	protected void canceling() {
		isCanceled = true;
	}





	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int SCALE = 2;
		monitor.beginTask("Building Graph", SCALE);
		readInput(monitor);
		buildGraph(monitor);
		monitor.done();
		
		if(isCanceled) return Status.CANCEL_STATUS;
		return Status.OK_STATUS;
	}
	
	
	private void readInput(IProgressMonitor monitor) {
		if(isCanceled) return;
		monitor.subTask("Reading Input File");
		List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		reader.read(filename);
		monitor.worked(1);
	}
	
	
	
	
	private void buildGraph(IProgressMonitor monitor) {
		if(isCanceled) return;
		monitor.subTask("Building Graph");
		GraphMLFile graphML = new GraphMLFile();
		initGraph = graphML.load("barrioPlugin/jGraph.xml");
		monitor.worked(1);		
	}

}
