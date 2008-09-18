package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Project {
	
	private String name;
	private List<Container> containers;
	private List<Cluster> clusters;
	private List<String> ruleDefinedClusters;
	
	public List<String> getRuleDefinedClusters() {
		return ruleDefinedClusters;
	}
	public void setRuleDefinedClusters(List<String> ruleDefinedClusters) {
		this.ruleDefinedClusters = ruleDefinedClusters;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Container> getContainers() {
		return containers;
	}
	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	
	public Container getContainer(String name)
	{
		for(Container c: containers)
		{
			String containerName = c.getName();
			if(containerName.equals(name)) return c;
		}
		return null;
	}
	
	public Cluster getCluster(String name)
	{
		for(Cluster c: clusters)
		{
			String clusterName = c.getName();
			if(clusterName.equals(name)) return c;
		}
		return null;
	}

}
