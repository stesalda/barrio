package nz.ac.massey.cs.barrio.clusterer;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Edge;

public interface Clusterer {

	public void cluster(Graph graph, int separation);
	
	public List<Edge> getEdgesRemoved();
}