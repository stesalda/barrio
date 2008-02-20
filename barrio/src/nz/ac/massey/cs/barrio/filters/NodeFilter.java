package nz.ac.massey.cs.barrio.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.UnassembledGraph;

public class NodeFilter implements Filter{

	public UnassembledGraph filter(Graph g) 
	{
		Set<Vertex> vertices = chooseGoodVertices(g.getVertices());
		return new UnassembledGraph(this, vertices, g.getEdges(), g);
	}
	
	public Set<Vertex> chooseGoodVertices(Set<?> verts)
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
	
		
	public boolean acceptVertex(Vertex v)
	{
		return true;
	}

	public String getName() 
	{
		return null;
	}
}
