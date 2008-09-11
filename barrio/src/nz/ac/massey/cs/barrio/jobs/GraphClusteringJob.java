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

	private Graph finalGraph;
	private List<Edge> removedEdges;
	private boolean canceled;
	private int numClusters;
	private int separationValue;
	
	public GraphClusteringJob(Graph initGraph) 
	{
		super("Processing graph");
		this.finalGraph = (Graph) initGraph.copy();	
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
		int SCALE = finalGraph.numEdges()/3;
		monitor.beginTask("Processing Graph", SCALE);
		clusterGraph(monitor);		
		monitor.done();
		
		if(canceled) return Status.CANCEL_STATUS;
		return Status.OK_STATUS;
		
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
		HashMap<String,Integer> clusts = clusterer.nameClusters(finalGraph);
		numClusters = clusts.size();
		
		int sep = 1;
		while(finalGraph.numEdges()>0)
		{
			if(canceled) return;
			monitor.subTask(message+sep);
			clusterer.cluster(finalGraph);
			List<Edge> newRemovedEdges = clusterer.getEdgesRemoved();
			for(Edge e:newRemovedEdges)
			{
				e.setUserDatum("relationship.separation", sep, UserData.SHARED);
			}
			removedEdges.addAll(newRemovedEdges);
			monitor.worked(clusterer.getEdgesRemoved().size());
			if(numClusters!=clusterer.nameClusters(finalGraph).size())
			{
				separationValue = sep;
				break;
			}			
			sep++;
		}
		
		clusterer.nameClusters(finalGraph);
		
//		for(Edge e: removedEdges)
//		{
//			finalGraph.addEdge(e);
//		}
	}


	public int getSeparationValue() {
		return separationValue;
	}


	public Graph getFinalGraph() {
		return finalGraph;
	}


	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}
}
