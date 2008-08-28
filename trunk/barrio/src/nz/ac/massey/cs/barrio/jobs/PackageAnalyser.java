package nz.ac.massey.cs.barrio.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class PackageAnalyser {
	
	private Graph graph;
	
	public PackageAnalyser(Graph graph)
	{
		this.graph = graph;
	}
	
	public List<String> getPwMC()
	{
		List<String> result = new ArrayList<String>();
		
		List<List<String>> tempContainer = new ArrayList<List<String>>();
		
		
		Set<Vertex> verts = graph.getVertices();
		for(Vertex v:verts)
		{
			StringBuffer namespase = new StringBuffer();
			namespase.append(v.getUserDatum("class.jar").toString());
			namespase.append('/');
			namespase.append(v.getUserDatum("class.packageName").toString());
			
		}
		return result;
	}

}
