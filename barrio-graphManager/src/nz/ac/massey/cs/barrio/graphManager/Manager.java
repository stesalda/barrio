package nz.ac.massey.cs.barrio.graphManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class Manager implements GraphManager {

	private Project project = null;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void setGraph(Graph graph) 
	{
		project = new Project();
		project.setName(graph.getUserDatum("file").toString());
		project.setContainers(new ArrayList<Container>());
		project.setClusters(new ArrayList<Cluster>());
		project.setRuleDefinedClusters(new ArrayList<String>());
		
		Set<Vertex> verts = graph.getVertices();
		for(Vertex v:verts)
		{
			Container container = null;
			Namespace namespace = null;

			String containerName = v.getUserDatum("class.jar").toString();
			String namespaceName = v.getUserDatum("class.packageName").toString();
			String clusterName = v.getUserDatum("class.cluster").toString();
			String className = v.getUserDatum("class.name").toString();
			List<String> classRuleDefinedClusters = (List<String>) v.getUserDatum("classification");
			
			if(project.getContainer(containerName)!=null) 
				container = project.getContainer(containerName);
			else
			{			
				container = new Container();
				container.setName(containerName);
				container.setNamespaces(new ArrayList<Namespace>());
				container.setClusters(new ArrayList<Cluster>());
				container.setRuleDefinedClusters(new ArrayList<String>());
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
				namespace.setRuleDefinedClusters(new ArrayList<String>());
				container.getNamespaces().add(namespace);
			}
			
			Clazz clazz = new Clazz();
			clazz.setName(className);
			clazz.setClusterName(clusterName);
			clazz.setRuleDefinedClusters(classRuleDefinedClusters);
			namespace.getClasses().add(clazz);
			
			for(String classRuleDefinedCluser: classRuleDefinedClusters)
			{
				if(!project.getRuleDefinedClusters().contains(classRuleDefinedCluser))
					project.getRuleDefinedClusters().add(classRuleDefinedCluser);
				if(!container.getRuleDefinedClusters().contains(classRuleDefinedCluser))
					container.getRuleDefinedClusters().add(classRuleDefinedCluser);
				if(!namespace.getRuleDefinedClusters().contains(classRuleDefinedCluser))
					namespace.getRuleDefinedClusters().add(classRuleDefinedCluser);
			}

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
	
	@Override
	public String getClassCluster(String containerName, String namespaceName, String className) {
		Container container = project.getContainer(containerName);
		Namespace namespace = container.getNamespace(namespaceName);
		Clazz clazz = namespace.getClazz(className);
		return clazz.getClusterName();
	}

	@Override
	public int getProjectClusterSize(String clusterName) {
		Cluster cluster = project.getCluster(clusterName);
		if(cluster==null) return 0;
		return cluster.getClasses().size();
	}

	@Override
	public List<String> getProjectRuleDefinedClusters() {
		return project.getRuleDefinedClusters();
	}

	@Override
	public List<String> getContainerRuleDefinedClusters(String containerName) {
		Container container = project.getContainer(containerName);
		return container.getRuleDefinedClusters();
	}

	@Override
	public List<String> getNamespaceRuleDefinedClusters(String containerName, String namespaceName) {
		Container container = project.getContainer(containerName);
		Namespace namespace = container.getNamespace(namespaceName);
		return namespace.getRuleDefinedClusters();
	}

	@Override
	public List<String> getClassRuleDefinedClusters(String containerName, String namespaceName, String className) {
		Container container = project.getContainer(containerName);
		Namespace namespace = container.getNamespace(namespaceName);
		Clazz clazz = namespace.getClazz(className);
		return clazz.getRuleDefinedClusters();
	}
	
	
}
