package nz.ac.massey.cs.barrio.jobs;

import java.util.ArrayList;
import java.util.List;

public class PackageCluster{
	
	private String namespace;
	private List<String> clusters;
	
	public PackageCluster() {
		super();
		clusters = new ArrayList<String>();
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public List<String> getCluster() {
		return clusters;
	}
	
	public void setCluster(List<String> clusters) {
		this.clusters = clusters;
	}
	
	public void addCluster(String cluster)
	{
		if(!clusters.contains(cluster)) clusters.add(cluster);
	}
}
