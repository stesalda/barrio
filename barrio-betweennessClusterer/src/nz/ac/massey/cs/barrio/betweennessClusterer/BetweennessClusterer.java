package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.barrio.clusterer.Clusterer;

public class BetweennessClusterer implements Clusterer{

	private List<Edge> edgesRemoved = null;
	
	public void cluster(Graph graph, int separation) {
		// TODO Auto-generated method stub
		EBC ebc = new EBC(separation);
		ebc.extract(graph);
		edgesRemoved = ebc.getEdgesRemoved();
	}

	public List<Edge> getEdgesRemoved() {
		// TODO Auto-generated method stub
		return edgesRemoved;
	}

}
