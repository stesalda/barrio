package nz.ac.massey.cs.barrio.clusterer;

import java.util.HashMap;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Edge;

public interface Clusterer{
	
	public void cluster(Graph graph);
	
	public List<Edge> getEdgesRemoved();
	
	public HashMap<String, Integer> nameClusters(Graph graph);
}
