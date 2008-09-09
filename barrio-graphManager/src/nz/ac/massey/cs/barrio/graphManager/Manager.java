package nz.ac.massey.cs.barrio.graphManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class Manager implements GraphManager {

	private Project project = null;
	
	
	@Override
	public void setGraph(Graph graph) 
	{
		project = new Project();
		project.setName("name"); 	//need to set proper name
		project.setContainers(new ArrayList<Container>());
		project.setClusters(new ArrayList<Cluster>());
		
		Set<Vertex> verts = graph.getVertices();
		for(Vertex v:verts)
		{
			Container container = null;
			Namespace namespace = null;

			String containerName = v.getUserDatum("class.jar").toString();
			String namespaceName = v.getUserDatum("class.packageName").toString();
			String clusterName = v.getUserDatum("class.cluster").toString();
			String className = v.getUserDatum("class.name").toString();
			
			if(project.getContainer(containerName)!=null) 
				container = project.getContainer(containerName);
			else
			{
				System.out.println("[Manager]: container not found: "+containerName);				
				container = new Container();
				container.setName(containerName);
				container.setNamespaces(new ArrayList<Namespace>());
				container.setClusters(new ArrayList<Cluster>());
				project.getContainers().add(container);
			}
			
			if(container.getNamespace(namespaceName)!=null)
				namespace = container.getNamespace(namespaceName);
			else
			{
				namespace = new Namespace();
				namespace.setName(namespaceName);
				namespace.setClasses(new ArrayList<Clazz>());
				namespace.setClusters(new ArrayList<Cluster>());
				container.getNamespaces().add(namespace);
			}
			
			Clazz clazz = new Clazz();
			clazz.setName(className);
			namespace.getClasses().add(clazz);

			Cluster cluster = null;
			if(project.getCluster(clusterName)!=null) 
				cluster = project.getCluster(clusterName);
			else
			{
				cluster = new Cluster();
				cluster.setName(clusterName);
				cluster.setClasses(new ArrayList<Clazz>());
				project.getClusters().add(cluster);
			}
			cluster.getClasses().add(clazz);
			
			if(container.getCluster(clusterName)!=null) 
				cluster = container.getCluster(clusterName);
			else
			{
				cluster = new Cluster();
				cluster.setName(clusterName);
				cluster.setClasses(new ArrayList<Clazz>());
				container.getClusters().add(cluster);
			}
			cluster.getClasses().add(clazz);
			
			if(namespace.getCluster(clusterName)!=null) 
				cluster = namespace.getCluster(clusterName);
			else
			{
				cluster = new Cluster();
				cluster.setName(clusterName);
				cluster.setClasses(new ArrayList<Clazz>());
				namespace.getClusters().add(cluster);
			}
			cluster.getClasses().add(clazz);
		}
//		printProject();
	}

	private void printProject() 
	{
		System.out.println(project.getName()+"; clusters = "+project.getClusters().size());
		for(Cluster c1: project.getClusters())
		{
			System.out.println("cluster = "+c1.getName()+" ("+c1.getClasses().size()+")");
		}
		
		for(Container c : project.getContainers())
		{
			for(Cluster c1: c.getClusters())
			{
				System.out.println("   cluster = "+c1.getName()+" ("+c1.getClasses().size()+")");
			}			
			System.out.println("   "+c.getName());
			for(Namespace n : c.getNamespaces())
			{
				for(Cluster c2: n.getClusters())
				{
					System.out.println("      cluster = "+c2.getName()+" ("+c2.getClasses().size()+")");
				}
				System.out.println("      "+n.getName());
				for(Clazz cz : n.getClasses())
				{
					System.out.println("         "+cz.getName());
				}
			}
		}
	}

	@Override
	public List<String> getProjectClusters(boolean showSingletons) 
	{
		List<String> result = new ArrayList<String>();
		for(Cluster c:project.getClusters())
		{
			if(!showSingletons && c.getClasses().size()<2) continue;
			result.add(c.getName());
		}
		return result;
	}

	@Override
	public List<String> getContainerClusters(String containerName, boolean showSingletons) 
	{
		List<String> result = new ArrayList<String>();
		Container container = project.getContainer(containerName);
		if(container==null) return result;
		for(Cluster c:container.getClusters())
		{
			if(!showSingletons && c.getClasses().size()<2) continue;
			result.add(c.getName());
		}
		return result;
	}

	@Override
	public List<String> getNamespaceClusters(String containerName, String namespaceName, boolean showSingletons) 
	{
		List<String> result = new ArrayList<String>();
		Container container = project.getContainer(containerName);
		if(container==null) return result;
		Namespace namespace = container.getNamespace(namespaceName);
		if(namespace==null) return result;
		for(Cluster c:namespace.getClusters())
		{
			if(!showSingletons && c.getClasses().size()<2) continue;
			result.add(c.getName());
		}
		return result;
	}

	@Override
	public List<String> getContainers() {
		List<String> result = new ArrayList<String>();
		for(Container c:project.getContainers())
		{
			result.add(c.getName());
		}
		return result;
	}

	@Override
	public List<String> getNamespaces(String containerName) {
		List<String> result = new ArrayList<String>();
		Container container = project.getContainer(containerName);
		if(container==null) return result;
		for(Namespace n:container.getNamespaces())
		{
			result.add(n.getName());
		}
		return result;
	}

	@Override
	public List<String> getClasses(String containerName, String namespaceName) {
		List<String> result = new ArrayList<String>();
		Container container = project.getContainer(containerName);
		if(container==null) return result;
		Namespace namespace = container.getNamespace(namespaceName);
		if(namespace==null) return result;
		for(Clazz c:namespace.getClasses())
		{
			result.add(c.getName());
		}
		return result;
	}
}
