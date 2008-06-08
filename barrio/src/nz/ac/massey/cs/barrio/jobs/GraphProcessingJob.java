package nz.ac.massey.cs.barrio.jobs;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.classifier.KnownClassifier;
import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.graphconverter.JungPrefuseBridge;
import nz.ac.massey.cs.barrio.gui.OutputGenerator;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.preferences.RuleStorage;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import prefuse.Display;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

public class GraphProcessingJob extends Job {

	private Object input;
	private Graph initGraph;
	private Graph finalGraph;

	private List<String> filters;
	private int separation;
	private List<Edge> removedEdges;
	private boolean canceled;
	
	private Display display;
	private OutputUI output;
	
	
	public void setOutput(OutputUI output) {
		this.output = output;
	}




	public GraphProcessingJob(Object input, Graph initGraph, List<String> filters, int separation) 
	{
		super("Processing graph");
		this.input = input;
		
		this.initGraph = initGraph;
		this.finalGraph = null;
		
		this.filters = filters;
		this.separation = separation;		
		removedEdges = new ArrayList<Edge>();
		canceled = false;
		
	}
	
	
	
	
	protected void canceling() {
		canceled = true;
	}
	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int SCALE;
		if(input!=null) SCALE = 4+filters.size()+separation;
		else SCALE = 3+filters.size()+separation;
	
			monitor.beginTask("Processing Graph", SCALE);
			
			if(initGraph==null) readInput(monitor);
			if(initGraph==null) return Status.CANCEL_STATUS;
			finalGraph = (Graph) initGraph.copy();
			
			filterGraph(monitor);
			clusterGraph(monitor);
			classifyGraph(monitor);
			buildVisual(monitor);
			
			monitor.done();
			
			if(canceled) return Status.CANCEL_STATUS;
			return Status.OK_STATUS;
		
	}




	private void readInput(IProgressMonitor monitor) {
		if(canceled) return;
		
		monitor.subTask("Reading Input File");
		List<InputReader> readers = KnownInputReader.all();
		initGraph = new DirectedSparseGraph();
		boolean done = false;
		InputReader reader = readers.get(0);
		//for(InputReader reader:readers)
		{
			try {
				initGraph = reader.read(input);
				System.out.println("[Job]: graph = "+initGraph.getVertices().size()+" nodes, "+initGraph.getEdges().size()+" edges");
				done = true;
				//break;
			} catch (UnknownInputException e) {
				//e.printStackTrace();
			} 
			catch (IOException e) {
				//e.printStackTrace();
				if (e instanceof java.net.ConnectException)
				{
					showConnectionErrorMessage();
					return;
				}
			}
		}
		if(!done) showUnknownInputMessage();
		monitor.worked(1);
	}
	
	private void showUnknownInputMessage()
	{
		output.getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				Shell s = new Shell();
				MessageBox mb = new MessageBox(s, SWT.ICON_ERROR);
				mb.setText("Barrio Error: Unknown Input");
				mb.setMessage("Cannot build graph from this input type!!!\n Refer to user manual.");
				int rc = mb.open();
				if(rc==SWT.OK) s.dispose();
			}
		});
	}
	
	private void showConnectionErrorMessage()
	{
		output.getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				Shell s = new Shell();
				MessageBox mb = new MessageBox(s, SWT.ICON_ERROR);
				mb.setText("Barrio Network Connection Error");
				mb.setMessage("Could not connect to DTD file!!!\n Check your network settings.");
				int rc = mb.open();
				if(rc==SWT.OK) s.dispose();
			}
		});
	}


	
	
	
	private void filterGraph(IProgressMonitor monitor) {
		
		List<Filter> knownFilters = new ArrayList<Filter>();
		knownFilters.addAll(KnownNodeFilters.all());
		knownFilters.addAll(KnownEdgeFilters.all());
		monitor.subTask("Filtering Graph");
		for(Filter filter:knownFilters)
		{
			Filter f = filter;
			if(filters.contains(filter.getName()))
			{
				finalGraph = f.filter(finalGraph).assemble();
			}
			monitor.worked(1);
			if(canceled) return;
		}
	}


	
	
	@SuppressWarnings("unchecked")
	private void clusterGraph(IProgressMonitor monitor) {

		String subtask = "Removing Separation Level ";
		
		Set<Edge> edges = finalGraph.getEdges();
		for(Edge edge:edges) 
			edge.setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		removedEdges.clear();
		
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		for(int i=0; i<separation; i++)
		{
			monitor.subTask(subtask+(i+1));
			clusterer.cluster(finalGraph);
			removedEdges.addAll(clusterer.getEdgesRemoved());
						
			monitor.worked(1);
			if(canceled) return;
		}	
		clusterer.nameClusters(finalGraph);
		
		for(Edge edge:removedEdges)
		{
			edge.setUserDatum("relationship.state", "removed", UserData.SHARED);
			finalGraph.addEdge(edge);
		}
	}
	
	
	
	
	private void classifyGraph(IProgressMonitor monitor)
	{
		if(canceled) return;
		Classifier classifier = KnownClassifier.all().get(0);
		List<ReferenceRule> rules = getRules();
		
		int SCALE = finalGraph.getVertices().size();
		monitor.beginTask("", SCALE);
		Iterator<Object> iter = finalGraph.getVertices().iterator();
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


	 


	private void buildVisual(IProgressMonitor monitor) {

		if(canceled) return;
		monitor.subTask("Producing Visualisation");
		
		JungPrefuseBridge bridge = new JungPrefuseBridge();
		DisplayBuilder disBuilder = new DisplayBuilder();
		disBuilder.setOutput(output);
		display = disBuilder.getDisplay(bridge.convert(finalGraph));
		display.setLayout(new BorderLayout());
		monitor.worked(1);
		
		
		output.getDisplay().asyncExec(new Runnable()
		{
			public void run() 
			{
				OutputGenerator og = new OutputGenerator(initGraph, finalGraph);
				output.updateOutputs(og, removedEdges);	
				output.paintGraph(display);
			}
			
		});
		monitor.worked(1);
		
	}


	public Graph getInitGraph() {
		return initGraph;
	}

	public Graph getFinalGraph() {
		return finalGraph;
	}
	
	public Display getDispaly()
	{
		return display;
	}

	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}

	public List<String> getFilters() {
		return filters;
	}

	public int getSeparation() {
		return separation;
	}
}
