package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public interface GraphManager {
	
	public void setGraph(Graph graph);
	
	/** 
	 * setGraph must be called before using following method.
	 * Returns list of cluster names within project, 
	 * showSingletons = true if you with to see singleton clusters, false otherwise
	 */
	public List<String> getProjectClusters(boolean showSingletons);
	
	/** 
	 * setGraph must be called before using following method.
	 * Returns list of cluster names within container, 
	 * showSingletons = true if you with to see singleton clusters, false otherwise
	 */
	public List<String> getContainerClusters(String containerName, boolean showSingletons);
	
	/** 
	 * setGraph must be called before using following method.
	 * Returns list of cluster names within namespace, 
	 * showSingletons = true if you with to see singleton clusters, false otherwise
	 */
	public List<String> getNamespaceClusters(String containerName, String namespaceName, boolean showSingletons);
	
	public List<String> getContainers();
	public List<String> getNamespaces(String containerName);
	public List<String> getClasses(String containerName, String namespaceName);


}
