package nz.ac.massey.cs.barrio.clusterer;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Edge;

public interface Clusterer extends IAdaptable{

	public void cluster(Graph graph, int separation);
	
	public List<Edge> getEdgesRemoved();
}
