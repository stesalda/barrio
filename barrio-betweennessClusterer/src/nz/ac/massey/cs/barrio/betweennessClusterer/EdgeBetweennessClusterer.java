package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.GraphClusterer;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.EdgeRanking;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

public class EdgeBetweennessClusterer implements GraphClusterer {

	private List<Edge> edgesRemoved;
	
	public EdgeBetweennessClusterer() {
		edgesRemoved = new ArrayList<Edge>();
	}

	@SuppressWarnings("unchecked")
	public ClusterSet extract(ArchetypeGraph g) {
		if (!(g instanceof Graph))
		{
			throw new IllegalArgumentException("Argument must be of type Graph.");
		}
		
		Graph graph = (Graph)g;
		
		
        BetweennessCentrality bc = new BetweennessCentrality(graph,false);
        bc.setRemoveRankScoresOnFinalize(true);
        bc.evaluate();
        
        double lastRank = -1.0;
        
        for(Object rank:bc.getRankings())
        {
        	double rankValue = Double.parseDouble(rank.toString());
        	
        	if(lastRank<0 || rankValue==lastRank)
        	{
        		EdgeRanking highestBetweenness = (EdgeRanking) rank;
        		Edge e = (Edge)highestBetweenness.edge.getEqualEdge(graph);
        		e.setUserDatum("relationship.betweenness", rankValue, UserData.SHARED);
	            edgesRemoved.add(e);
	            graph.removeEdge(e);//highestBetweenness.edge);

            	lastRank = rankValue;
        	}
        }

	    WeakComponentClusterer wcSearch = new WeakComponentClusterer();
	    ClusterSet clusterSet = wcSearch.extract(graph);

	    
	    //nameClusters(clusterSet);
	    return clusterSet;
	}
	
	public List<Edge> getEdgesRemoved()
	{
		return edgesRemoved;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> nameClusters(ClusterSet cs)
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(int i=0; i<cs.size(); i++)
		{
			Set<Vertex> verts = cs.getCluster(i);
			String clusterName = "cluster-"+i;
			
			Iterator<Vertex> vertItearIterator = verts.iterator();
			while(vertItearIterator.hasNext())
			{
				Vertex v = vertItearIterator.next();
				v.setUserDatum("class.cluster", clusterName, UserData.SHARED);
			}
			map.put(clusterName, cs.getCluster(i).size());
		}
//		System.out.println("[EdgeBetwClust]: map = "+map);
		return map;
	}
	
	
	public HashMap<String, Integer> nameClusters(Graph graph)
	{
		WeakComponentClusterer wcSearch = new WeakComponentClusterer();
	    ClusterSet clusterSet = wcSearch.extract(graph);
	    return nameClusters(clusterSet);
	}

}
