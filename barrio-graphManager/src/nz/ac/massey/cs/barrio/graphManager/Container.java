package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Container {
	
	private String name;
	private List<Namespace> namespaces;
	private List<Cluster> clusters;
	private List<String> ruleDefinedClusters;
	
	public List<String> getRuleDefinedClusters() {
		return ruleDefinedClusters;
	}

	public void setRuleDefinedClusters(List<String> ruleDefinedClusters) {
		this.ruleDefinedClusters = ruleDefinedClusters;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public List<Namespace> getNamespaces() {
		return namespaces;
	}
	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public Namespace getNamespace(String name)
	{
		for(Namespace n: namespaces)
		{
			String namespaceName = n.getName();
			if(namespaceName.equals(name)) return n;
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
