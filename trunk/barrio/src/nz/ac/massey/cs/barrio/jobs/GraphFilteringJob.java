package nz.ac.massey.cs.barrio.jobs;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.utils.UserData;

public class GraphFilteringJob extends Job {

	private Graph filteredGraph;
	private List<String> filters;
	private boolean canceled;
	
	public GraphFilteringJob(Graph initGraph, List<String> filters) 
	{
		super("Filtering Graph");		
		this.filteredGraph = (Graph) initGraph.copy();			
		this.filters = filters;	
		canceled = false;
		
	}	
	
	protected void canceling() {
		canceled = true;
	}	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		if(filteredGraph == null) return Status.CANCEL_STATUS;
		if(filters==null) return Status.CANCEL_STATUS;
		if(filters.size()<1) return Status.OK_STATUS;
		
		int SCALE = filters.size();
		monitor.beginTask("Filtering Graph", SCALE);
		String filename = filteredGraph.getUserDatum("file").toString();
		filterGraph(monitor);
		filteredGraph.setUserDatum("file", filename, UserData.SHARED);
		clusterGraph(monitor);
		monitor.done();		
		if(canceled) return Status.CANCEL_STATUS;
		return Status.OK_STATUS;		
	}

	private void filterGraph(IProgressMonitor monitor) {
		
		List<Filter> knownFilters = new ArrayList<Filter>();
		knownFilters.addAll(KnownNodeFilters.all());
		knownFilters.addAll(KnownEdgeFilters.all());
		for(Filter filter:knownFilters)
		{
			Filter f = filter;
			if(filters.contains(filter.getName()))
			{
				StringBuilder builder = new StringBuilder();
				builder.append("Filtering ");
				builder.append(f.getName());
				monitor.subTask(builder.toString());
				filteredGraph = f.filter(filteredGraph).assemble();
			}
			monitor.worked(1);
			if(canceled) return;
		}
	}
	
	private void clusterGraph(IProgressMonitor monitor) 
	{
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		clusterer.nameClusters(filteredGraph);
		monitor.worked(1);
	}

	public Graph getFilteredGraph() {
		return filteredGraph;
	}
}
