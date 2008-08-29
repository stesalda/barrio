package nz.ac.massey.cs.barrio.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
		
		List<PackageCluster> tempContainer = new ArrayList<PackageCluster>();
		
		
		Set<Vertex> verts = graph.getVertices();
		for(Vertex v:verts)
		{
			StringBuffer namespace = new StringBuffer();
			namespace.append(v.getUserDatum("class.jar").toString());
			namespace.append('/');
			namespace.append(v.getUserDatum("class.packageName").toString());
			
			PackageCluster pc = new PackageCluster();
			pc.setNamespace(namespace.toString());
			pc.setCluster(v.getUserDatum("class.packageName").toString());
			
			if(!tempContainer.contains(pc)) tempContainer.add(pc);
		}
		Collections.sort(tempContainer);
		
		PackageCluster temp = null;
		for(PackageCluster pc:tempContainer)
		{
		}
		return result;
	}

}
