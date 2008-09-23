package nz.ac.massey.cs.barrio.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.utils.UserData;

public class GraphClusteringJob extends Job {

	private Graph clusteredGraph;
	private List<Edge> removedEdges;
	private boolean canceled;
	private int numClusters;
	private int separationValue;
	
	public GraphClusteringJob(Graph filteredGraph) 
	{
		super("Clustering graph");
		this.clusteredGraph = (Graph) filteredGraph.copy();	
		//System.out.println("[ClusteringJob]: file = "+clusteredGraph.getUserDatum("file").toString());
		removedEdges = new ArrayList<Edge>();
		canceled = false;
		numClusters = 0;
		separationValue = 0;
	}


	protected void canceling() {
		canceled = true;
	}
	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		int SCALE = clusteredGraph.numEdges()/3;
		monitor.beginTask("Clustering Graph", SCALE);
		clusterGraph(monitor);		
		monitor.done();
		
		if(canceled) return Status.CANCEL_STATUS;
		return Status.OK_STATUS;
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void clusterGraph(IProgressMonitor monitor) {
		
		Set<Edge> edges = clusteredGraph.getEdges();
		for(Edge edge:edges) 
			edge.setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		removedEdges.clear();
		String message = "Removing Separation Level: ";		
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		HashMap<String,Integer> clusts = clusterer.nameClusters(clusteredGraph);
		numClusters = clusts.size();
		
		int sep = 1;
		while(clusteredGraph.numEdges()>0)
		{
			if(canceled) return;
			monitor.subTask(message+sep);
			clusterer.cluster(clusteredGraph);
			List<Edge> newRemovedEdges = clusterer.getEdgesRemoved();
			for(Edge e:newRemovedEdges)
			{
				e.setUserDatum("relationship.separation", sep, UserData.SHARED);
			}
			removedEdges.addAll(newRemovedEdges);
			monitor.worked(clusterer.getEdgesRemoved().size());
			if(numClusters!=clusterer.nameClusters(clusteredGraph).size())
			{
				separationValue = sep;
				break;
			}			
			sep++;
		}
		
		clusterer.nameClusters(clusteredGraph);
	}


	public int getSeparationValue() {
		return separationValue;
	}


	public Graph getClusteredGraph() {
		return clusteredGraph;
	}


	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}
}
