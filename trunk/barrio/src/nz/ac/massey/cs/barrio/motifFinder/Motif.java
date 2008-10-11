package nz.ac.massey.cs.barrio.motifFinder;

import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public interface Motif {

	public Iterator<MotifInstance> findAll(Graph graph);
	
	public List<String> getRoleNames();
}
