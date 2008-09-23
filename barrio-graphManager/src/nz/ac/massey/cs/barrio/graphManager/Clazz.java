package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Clazz {
	
	private String name;
	private String clusterName;
	private List<String> ruleDefinedClusters;
	private String annotation;

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

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getAnnotation() {
		return annotation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append("-(");
		builder.append(annotation);
		builder.append(')');
		return builder.toString();
	}
	
	

	
}
