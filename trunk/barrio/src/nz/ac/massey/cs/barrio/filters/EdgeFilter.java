package nz.ac.massey.cs.barrio.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;

public class EdgeFilter implements Filter{

	/**
	 * Applies filter to the graph by removing edges that contain filter property 
	 * @param g - the graph
	 * @return UnasembeledGraph, can be assembled by calling assemble() method. 
	 */
	public UnassembledGraph filter(Graph g) 
	{
		Set<Edge> edges = chooseGoodEdges(g.getEdges());
		
		return new UnassembledGraph(this, g.getVertices(), edges, g);
	}
	
	/**
	 * Iterates over a set of edges and checks which ones will remain in new graph
	 * @param edges - the set of edges
	 * @return set of edges that will remain in the new filtered graph
	 */
	public Set<Edge> chooseGoodEdges(Set<Edge> edges)
	{
		Set<Edge> myEdges = new HashSet<Edge>();		
		Iterator<?> iter = edges.iterator();
		while(iter.hasNext())
		{
			Edge e = (Edge) iter.next();
			if(acceptEdge(e)) myEdges.add(e);
		}		
		return myEdges;
	}
	
	/**
	 * Needs to be overridden in the subclass. Decides whether an edge is to be kept in the graph or to be removed
	 * @param e - the edge
	 * @return true if edge remains or false if edge is removed
	 */
	public boolean acceptEdge(Edge e)
	{
		return true;
	}

	/**
	 *  Needs to be overridden in the subclass.
	 *  @return Name of the filter
	 */
	public String getName() 
	{
		return null;
	}
}
