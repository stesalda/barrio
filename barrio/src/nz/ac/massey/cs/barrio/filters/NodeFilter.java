package nz.ac.massey.cs.barrio.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;

public class NodeFilter implements Filter{

	/**
	 * Applies filter to the graph by removing vertices that contain filter property 
	 * @param g - the graph
	 * @return UnasembeledGraph, can be assembled by calling assemble() method. 
	 */
	public UnassembledGraph filter(Graph g) 
	{
		Set<Vertex> vertices = chooseGoodVertices(g.getVertices());
		return new UnassembledGraph(this, vertices, g.getEdges(), g);
	}
	
	/**
	 * Iterates over a set of vertices and checks which ones will remain in new graph
	 * @param verts - the set of vertices
	 * @return set of vertices that will remain in the new filtered graph
	 */
	public Set<Vertex> chooseGoodVertices(Set<Vertex> verts)
	{
		Set<Vertex> myVerts = new HashSet<Vertex>();
		Iterator<?> iter = verts.iterator();
		while(iter.hasNext()) 
		{
			Vertex v = (Vertex) iter.next();
//			System.out.println("[nodeFilter]: test node="+v.getUserDatum("class.name")
//					+" isAbstract="+v.getUserDatum("class.isAbstract"));
			if (acceptVertex(v)) myVerts.add(v);
//			System.out.println("[nodeFilter]: accept node "+v.getUserDatum("class.name")
//					+" = "+acceptVertex(v));
		}
		return myVerts;
	}
	
	/**
	 * Needs to be overridden in the subclass. Decides whether a vertex is to be kept in the graph or to be removed
	 * @param v - the vertex
	 * @return true if vertex remains or false if vertex is removed
	 */	
	public boolean acceptVertex(Vertex v)
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
