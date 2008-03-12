package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public class BetweennessClusteringJob extends Job implements Clusterer{

	private Graph graph;
	private int separation;
	private List<Edge> removedEdges;
	
	public BetweennessClusteringJob(Graph graph, int separation) {
		super("Applying separation ... ");
		this.graph = graph;
		this.separation = separation;
		removedEdges = new ArrayList<Edge>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("separation level done", separation);
		EBC ebc = new EBC(1);
		for(int i=0; i<separation; i++)
		{
			monitor.subTask(String.valueOf(i+1));
			ebc.extract(graph);
			removedEdges.addAll(ebc.getEdgesRemoved());
			monitor.worked(1);
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	public void cluster(Graph graph, int separation) {
		// TODO Auto-generated method stub
		
	}

	public List<Edge> getEdgesRemoved() {
		// TODO Auto-generated method stub
		return null;
	}

	public Job getJob() {
		// TODO Auto-generated method stub
		return null;
	}

}
