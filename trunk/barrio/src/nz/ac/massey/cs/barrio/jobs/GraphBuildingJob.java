package nz.ac.massey.cs.barrio.jobs;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.classifier.KnownClassifier;
import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.gui.OutputGenerator;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.preferences.RuleStorage;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import prefuse.Display;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public class GraphBuildingJob extends Job {

	private Object input;
	private Graph initGraph;
	private boolean canceled;
	
	
	public GraphBuildingJob(Object input) 
	{
		super("Building graph");
		this.input = input;		
		this.initGraph = null;
		canceled = false;	
	}
	
	protected void canceling() {
		canceled = true;
	}	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int SCALE;
		if(input!=null) SCALE = 3;
		else SCALE = 2;
	
			readInput(monitor);
			if(initGraph==null) return Status.CANCEL_STATUS;
			monitor.beginTask("Building Graph", SCALE+initGraph.numEdges());
			clusterGraph(monitor);
			classifyGraph(monitor);
			monitor.done();
			
			if(canceled) return Status.CANCEL_STATUS;
			return Status.OK_STATUS;
		
	}
	
	private void readInput(IProgressMonitor monitor) {
		if(canceled) return;
		
		monitor.subTask("Reading Input File");
		List<InputReader> readers = KnownInputReader.all();
		initGraph = new DirectedSparseGraph();
		InputReader reader = readers.get(0);
		try 
		{
			initGraph = reader.read(input);
		} 
		catch (UnknownInputException e) {} 
		catch (IOException e) {}
		monitor.worked(1);
	}

	
	private void clusterGraph(IProgressMonitor monitor) 
	{
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		clusterer.nameClusters(initGraph);
		monitor.worked(1);
	}
		
	@SuppressWarnings("unchecked")
	private void classifyGraph(IProgressMonitor monitor)
	{
		if(canceled) return;
		Classifier classifier = KnownClassifier.all().get(0);
		List<ReferenceRule> rules = getRules();
		
		int SCALE = initGraph.getVertices().size();
		monitor.beginTask("", SCALE);
		Iterator<Object> iter = initGraph.getVertices().iterator();
		while(iter.hasNext())
		{
			Vertex v = (Vertex) iter.next();
			classifier.classify(v, rules);
			monitor.worked(1);
			if(canceled) return;
		}
	}
	
	private List<ReferenceRule> getRules() 
	{
		RuleStorage storage = new RuleStorage(null);
		return storage.load();
	}

	public Graph getInitGraph() {
		return initGraph;
	}
}
