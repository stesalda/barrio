package nz.ac.massey.cs.barrio.motifFinder;

import edu.uci.ics.jung.graph.Vertex;

/**
 * This class represents a single instance of a motif. By motif we mean an element of a pattern.
 *
 */
public interface MotifInstance {

	/**
	 * Retrieves jung.graph.Vertex type that acts the specified rolename
	 * @param roleName the rolename
	 * @return the jung.graph.Vertex
	 */
	public Vertex getInstance(String roleName);
}