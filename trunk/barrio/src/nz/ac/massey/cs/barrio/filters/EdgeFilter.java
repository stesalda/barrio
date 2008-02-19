package nz.ac.massey.cs.barrio.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;

public class EdgeFilter implements Filter{

	public UnassembledGraph filter(Graph g) 
	{
		Set<Edge> edges = chooseGoodEdges(g.getEdges());
		
		return new UnassembledGraph(this, g.getVertices(), edges, g);
	}
	
	public Set<Edge> chooseGoodEdges(Set<?> edges)
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
	
	public boolean acceptEdge(Edge e)
	{
		return true;
	}

	public String getName() 
	{
		return null;
	}
}
