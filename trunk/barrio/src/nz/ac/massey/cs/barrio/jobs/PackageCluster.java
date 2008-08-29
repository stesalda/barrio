package nz.ac.massey.cs.barrio.jobs;

public class PackageCluster implements Comparable{
	
	private String namespace;
	private String cluster;
	
	public PackageCluster() {
		super();
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getCluster() {
		return cluster;
	}
	
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	@Override
	public boolean equals(Object arg0) {
		PackageCluster pc = (PackageCluster) arg0;
		
		if(pc.getNamespace().equals(this.namespace) && pc.getCluster().equals(this.cluster)) 
			return true;
		else return false;
	}

	@Override
	public int compareTo(Object arg0) {
		PackageCluster pc = (PackageCluster) arg0;
		
		return this.namespace.compareTo(pc.namespace);
	}
	
	

}
