package nz.ac.massey.cs.barrio.exporter;

import java.util.List;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public interface Exporter {

	/**
	 * Writes graph clustering result information to XML file.
	 * @param graph the graph
	 * @param separation separation level (number of iterations)
	 * @param removedEdges list of the removed edges
	 * @param filters list of the applied filters
	 * @param folderName name of the folder of the XML file
	 */
	public void export(Graph graph, int separation, List<Edge> removedEdges, List<String> filters, String folderName);
}
