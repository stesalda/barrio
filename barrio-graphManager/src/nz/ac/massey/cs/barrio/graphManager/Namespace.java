package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Namespace {
	
	private String name;
	private List<Clazz> classes;
	private List<Cluster> clusters;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Clazz> getClasses() {
		return classes;
	}
	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	
	public Clazz getClazz(String name)
	{
		for(Clazz c: classes)
		{
			String clazzName = c.getName();
			if(clazzName.equals(name)) return c;
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

	@Override
	public String toString() {
		return name;
	}
}
