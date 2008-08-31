package nz.ac.massey.cs.barrio.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.Activator;
import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.classifier.KnownClassifier;
import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.gui.OutputGenerator;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.preferences.PreferenceConstants;
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
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.utils.UserData;

public class GraphClusteringJob extends Job {

	private Graph finalGraph;
	private List<String> filters;
	private List<Edge> removedEdges;
	private boolean canceled;
	private int numClusters;
	private PrintStream out;
	private long start;
	
	public GraphClusteringJob(Graph finalGraph, List<String> filters) 
	{
		super("Processing graph");
		start = System.currentTimeMillis();
		this.finalGraph = (Graph) finalGraph.copy();
		this.filters = filters;		
		removedEdges = new ArrayList<Edge>();
		canceled = false;
		numClusters = 0;
		out = null;
		String filename = getFilename(finalGraph.getUserDatum("file").toString());
//		System.out.println("[ClusterJob] file = "+filename);
		try {
			String folder = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.FOLDER_NAME);
			out = new PrintStream(folder+filename+".xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private String getFilename(String string) {
		return string.substring(string.lastIndexOf('\\'), string.lastIndexOf('.'));
		
	}


	protected void canceling() {
		canceled = true;
	}
	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int SCALE = filters.size()+finalGraph.numEdges();
		monitor.beginTask("Processing Graph", SCALE);
		
		filterGraph(monitor);
		clusterGraph(monitor);
//		classifyGraph(monitor);
//		buildVisual(monitor);
		
		monitor.done();
		if(out!=null) out.close();
		if(canceled) return Status.CANCEL_STATUS;
		return Status.OK_STATUS;
		
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
		
		Set<Edge> edges = finalGraph.getEdges();
		for(Edge edge:edges) 
			edge.setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		removedEdges.clear();
		String message = "Removing Separation Level: ";		
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		
		clusterer.nameClusters(finalGraph);
		printHead(out);
		printMiddle(out,0);
		int sep = 1;
		while(finalGraph.numEdges()>0)
		{
			monitor.subTask(message+sep);
			clusterer.cluster(finalGraph);
			clusterer.nameClusters(finalGraph);
			removedEdges.addAll(clusterer.getEdgesRemoved());
			monitor.worked(clusterer.getEdgesRemoved().size());
			printMiddle(out, sep);
			sep++;
		}
		
		clusterer.nameClusters(finalGraph);
		printEnd(out);
	}
	
	private void printHead(PrintStream out)
	{
		if(out==null) return;
		List<String> containers = new ArrayList<String>();
		List<String> namespaces = new ArrayList<String>();
		
		Set<Vertex> verts = finalGraph.getVertices();
		for(Vertex v:verts)
		{
			if(!containers.contains(v.getUserDatum("class.jar").toString()))
				containers.add(v.getUserDatum("class.jar").toString());
			if(!namespaces.contains(v.getUserDatum("class.packageName").toString()))
				namespaces.add(v.getUserDatum("class.packageName").toString());
		}
		
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.print("<graph file=\"");
		out.print(finalGraph.getUserDatum("file"));
		out.println("\">");
		out.print("<containers count =\"");
		out.print(containers.size());
		out.println("\">");
		for(String c:containers)
		{
			out.print("<container name=\"");
			out.print(c);
			out.println("\" />");
		}
		out.println("</containers>");
		out.print("<namespaces count =\"");
		out.print(namespaces.size());
		out.println("\" />");
		out.print("<classes count =\"");
		out.print(finalGraph.numVertices());
		out.println("\" />");
		
	}
	
	private void printEnd(PrintStream out)
	{
		if(out==null) return;
		out.print("<analysis time-seconds=\"");
		out.print((System.currentTimeMillis()-start)/1000);
		out.println("\" />");
		out.println("</graph>");
		out.close();
	}
	
	
	private void printMiddle(PrintStream out, int sep)
	{
		if(out==null) return;
		List<String> clusters = new ArrayList<String>();
		
		Set<Vertex> verts = finalGraph.getVertices();
		for(Vertex v:verts)
		{
			if(!clusters.contains(v.getUserDatum("class.cluster").toString()))
				clusters.add(v.getUserDatum("class.cluster").toString());
		}
		if(numClusters!=clusters.size())
		{
			out.print("<separation value =\"");
			out.print(sep);
			out.println("\">");
			out.print("<clusters count=\"");
			out.print(clusters.size());
			out.println("\" />");
			out.print("<relationships count=\"");
			out.print(finalGraph.numEdges());
			out.println("\" />");
			
			PackageAnalyser pa = new PackageAnalyser(finalGraph);
			out.print("<containers-with-clusters count=\"");
			out.print(pa.getContainerWMC().size());
			out.println("\">");
			for(String s:pa.getContainerWMC())
			{
				out.print("<container name=\"");
				out.print(s);
				out.println("\" />");
			}
			out.println("</containers-with-clusters>");
			
			out.print("<namesapses-with-clusters count=\"");
			out.print(pa.getPackageWMC().size());
			out.println("\">");
			for(String s:pa.getPackageWMC())
			{
				out.print("<namespace name=\"");
				out.print(s);
				out.println("\" />");
			}
			out.println("</namesapses-with-clusters>");
			
			out.println("</separation>");
			numClusters = clusters.size();
		}
	}
	 

	public Graph getFinalGraph() {
		return finalGraph;
	}

	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}

	public List<String> getFilters() {
		return filters;
	}
}
