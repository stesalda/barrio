package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public class BetweennessClusterer{

	private List<Edge> edgesRemoved = null;
	private BetweennessClusteringJob job;
	
	public void cluster(Graph graph, int separation) {

		job = new BetweennessClusteringJob(graph, separation);
		job.schedule();
		job.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {}

			public void awake(IJobChangeEvent event) {}

			public void done(IJobChangeEvent event) {
				
				
			}

			public void running(IJobChangeEvent event) {}

			public void scheduled(IJobChangeEvent event) {}

			public void sleeping(IJobChangeEvent event) {}
			
		});
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
