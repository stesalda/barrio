package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public interface GraphManager {
	
	/**
	 * Required method to run in order to use any of the other methods of GraphManager.
	 * Sets 'tree' structure of the project.
	 * @param graph instance of edu.uci.ics.jung.graph.Graph
	 */
	public void setGraph(Graph graph);
	
	/**  
	 * setGraph(Graph graph) must be called before using following method.
	 * @param showSingletons true if you want to have singleton clusters, false otherwise
	 * @return List of cluster names within the project
	 */
	public List<String> getProjectClusters(boolean showSingletons);
	
	/** 
	 * setGraph(Graph graph) must be called before using following method.
	 * returns number of classes in the specified cluster
	 * @param clusterName name of the cluster
	 * @return number of classes within the cluster
	 */
	public int getProjectClusterSize(String clusterName);
	
	/** 
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param showSingletons true if you with to see singleton clusters, false otherwise
	 * @return List of cluster names within the container
	 */
	public List<String> getContainerClusters(String containerName, boolean showSingletons);
	
	/** 
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @param showSingletons true if you with to see singleton clusters, false otherwise
	 * @return List of cluster names within the namespace
	 */
	public List<String> getNamespaceClusters(String containerName, String namespaceName, boolean showSingletons);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @return list of container names within the project
	 */
	public List<String> getContainers();
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of container
	 * @return list of namespace names within the container
	 */
	public List<String> getNamespaces(String containerName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @return List of class names within namespace
	 */
	public List<String> getClasses(String containerName, String namespaceName);

	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @param className name of the class
	 * @return cluster name the class belongs to
	 */
	public String getClassCluster(String containerName, String namespaceName, String className);

	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @param className name of the class
	 * @return class parameters string
	 */
	public String getClassAnnotation(String containerName, String namespaceName, String className);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @return List of the names of rule defined clusters within the project
	 */
	public List<String> getProjectRuleDefinedClusters();
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @return List of the names of the rule defined clusters within the container
	 */
	public List<String> getContainerRuleDefinedClusters(String containerName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @return List of names of the rule defined clusters within the namespace
	 */
	public List<String> getNamespaceRuleDefinedClusters(String containerName, String namespaceName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @param className name of the class
	 * @return List of the names of the rule defined clusters the class belongs to
	 */
	public List<String> getClassRuleDefinedClusters(String containerName, String namespaceName, String className);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @return number of classes within the container
	 */
	public int getContainerSize(String containerName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @return number of classes within the namespace
	 */
	public int getNamespaceSize(String containerName, String namespaceName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param clusterName name of the cluster
	 * @return number of classes within the container that belong to the cluster
	 */
	public int getIntersectionSize(String containerName, String clusterName);
	
	/**
	 * setGraph(Graph graph) must be called before using following method.
	 * @param containerName name of the container
	 * @param namespaceName name of the namespace
	 * @param clusterName clusterName name of the cluster
	 * @return number of classes within the namespace that belong to the cluster
	 */
	public int getIntersectionSize(String containerName, String namespaceName, String clusterName);
}
