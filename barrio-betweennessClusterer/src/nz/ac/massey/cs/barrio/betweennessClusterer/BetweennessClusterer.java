package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public class BetweennessClusterer implements Clusterer{

	private List<Edge> edgesRemoved = null;
	
	public void cluster(Graph graph) {
		EdgeBetweennessClusterer ebc = new EdgeBetweennessClusterer();
		ebc.extract(graph);
		edgesRemoved = ebc.getEdgesRemoved();
	}

	public List<Edge> getEdgesRemoved() {
		return edgesRemoved;
	}
}
