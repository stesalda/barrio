package nz.ac.massey.cs.barrio.motifFinder;

import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public interface Motif {

	/**
	 * Finds all motif instances that occur in the dependency graph
	 * @param graph the graph
	 * @return iterator of the intances of the motif
	 */
	public Iterator<MotifInstance> findAll(Graph graph);
	
	/**
	 * Gets the list of all possible rolenames in current motif
	 * @return list of rolenames
	 */
	public List<String> getRoleNames();
}
