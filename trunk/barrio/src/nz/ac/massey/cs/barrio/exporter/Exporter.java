package nz.ac.massey.cs.barrio.exporter;

import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public interface Exporter {

	public void export(Graph graph, int separation, List<Edge> removedEdges, List<String> filters, String folderName);
}
