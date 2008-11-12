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
	private int stop;
	private List<Integer> keyList;
	private List<Graph> graphList;
	private List<List<Edge>> edgeList;
	
	public GraphClusteringJob(Graph filteredGraph, int stop) 
	{
		super("Clustering graph");
		this.clusteredGraph = (Graph) filteredGraph.copy();	
		//System.out.println("[ClusteringJob]: file = "+clusteredGraph.getUserDatum("file").toString());
		removedEdges = new ArrayList<Edge>();
		canceled = false;
		numClusters = 0;
		separationValue = 0;
		this.stop = stop;
		
		keyList = new ArrayList<Integer>();
		graphList = new ArrayList<Graph>();
		edgeList = new ArrayList<List<Edge>>();
		keyList.add(0, 0);
		graphList.add((Graph) clusteredGraph.copy());
		edgeList.add(copyList(removedEdges));
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
		int separationsDone = 0;
		while(clusteredGraph.numEdges()>0 && separationsDone<stop)
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
			int newNumClusters = clusterer.nameClusters(clusteredGraph).size();
			if(numClusters!= newNumClusters)
			{
				separationValue = sep;
				keyList.add(sep);
				
				for(Edge e:removedEdges)
				{
					e.setUserDatum("relationship.state", "removed", UserData.SHARED);
					clusteredGraph.addEdge(e);
				}
				
				graphList.add((Graph) clusteredGraph.copy());
				
				for(Edge e:removedEdges)
				{
					clusteredGraph.removeEdge(e);
				}
				
				edgeList.add(copyList(removedEdges));
				numClusters = newNumClusters;
				separationsDone++;
			}			
			sep++;
		}
		
		clusterer.nameClusters(clusteredGraph);
	}
	
	private List<Edge> copyList(List<Edge> list)
	{
		List<Edge> result = new ArrayList<Edge>();
		for(Edge e:list)
		{
			result.add(e);
		}
		return result;
	}


	public List<Integer> getKeyList() {
		return keyList;
	}


	public List<Graph> getGraphList() {
		return graphList;
	}


	public List<List<Edge>> getEdgeList() {
		return edgeList;
	}
	
	
}
