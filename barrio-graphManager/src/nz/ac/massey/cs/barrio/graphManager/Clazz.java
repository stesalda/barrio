package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Clazz {
	
	private String name;
	private String clusterName;
	private List<String> ruleDefinedClusters;

	public List<String> getRuleDefinedClusters() {
		return ruleDefinedClusters;
	}

	public void setRuleDefinedClusters(List<String> ruleDefinedClusters) {
		this.ruleDefinedClusters = ruleDefinedClusters;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	

	
}
