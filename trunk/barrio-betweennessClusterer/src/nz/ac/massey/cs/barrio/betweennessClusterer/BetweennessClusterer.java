package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;

import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public class BetweennessClusterer implements Clusterer{

	private List<Edge> edgesRemoved = null;
	private BetweennessClusteringJob job;
	
	public void cluster(Graph graph, int separation) {

		job = new BetweennessClusteringJob(graph, separation);
		job.schedule();
	}
	
	public Job getJob()
	{
		return job;
	}

	public List<Edge> getEdgesRemoved() {
		// TODO Auto-generated method stub
		return edgesRemoved;
	}

}
