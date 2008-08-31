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
	
	public List<String> getPackageWMC()
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
			
			PackageCluster pc = null;
			for(PackageCluster clust:tempContainer)
			{
				if(clust.getNamespace().equals(namespace.toString()))
				{
					pc = clust;
					break;
				}
			}

			String cluster = v.getUserDatum("class.cluster").toString();
			if(pc==null)
			{
				pc = new PackageCluster();
				pc.setNamespace(namespace.toString());
				pc.addCluster(cluster);
				tempContainer.add(pc);
			}
			else pc.addCluster(cluster);
			
		}
		
		for(PackageCluster pc:tempContainer)
		{
			if(pc.getCluster().size()>1) result.add(pc.getNamespace());
		}
		return result;
	}
	
	public List<String> getContainerWMC()
	{
		List<String> result = new ArrayList<String>();
		
		List<PackageCluster> tempContainer = new ArrayList<PackageCluster>();
		
		
		Set<Vertex> verts = graph.getVertices();
		for(Vertex v:verts)
		{
			String container = v.getUserDatum("class.jar").toString();
			
			PackageCluster pc = null;
			for(PackageCluster clust:tempContainer)
			{
				if(clust.getNamespace().equals(container.toString()))
				{
					pc = clust;
					break;
				}
			}

			String cluster = v.getUserDatum("class.cluster").toString();
			if(pc==null)
			{
				pc = new PackageCluster();
				pc.setNamespace(container.toString());
				pc.addCluster(cluster);
				tempContainer.add(pc);
			}
			else pc.addCluster(cluster);
			
		}
		
		for(PackageCluster pc:tempContainer)
		{
			if(pc.getCluster().size()>1) result.add(pc.getNamespace());
		}
		return result;
	}

}
