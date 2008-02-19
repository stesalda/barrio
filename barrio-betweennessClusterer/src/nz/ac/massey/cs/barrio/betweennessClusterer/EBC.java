package nz.ac.massey.cs.barrio.betweennessClusterer;

import java.util.ArrayList;
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

public class EBC implements GraphClusterer {

	private int separation;
	private List<Edge> edgesRemoved;
	
	public EBC(int separationLevel) {
		separation = separationLevel;
		edgesRemoved = new ArrayList<Edge>();
	}

	@SuppressWarnings("unchecked")
	public ClusterSet extract(ArchetypeGraph g) {
		if (!(g instanceof Graph))
		{
			throw new IllegalArgumentException("Argument must be of type Graph.");
		}
		
		Graph graph = (Graph)g;
		
		Iterator<Edge> edgeIter = graph.getEdges().iterator();
		while(edgeIter.hasNext())
		{
			edgeIter.next().setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		}
		
       
		for (int i=0; i<separation; i++) 
        {
            BetweennessCentrality bc = new BetweennessCentrality(graph,false);
            bc.setRemoveRankScoresOnFinalize(true);
            bc.evaluate();
            
            double lastRank = -1.0;
            List<?> rankings = bc.getRankings();
//	            System.out.println("[EBC]: rankings = "+rankings);
            Iterator<?> iter = rankings.iterator();
            while(iter.hasNext())
            {
            	Object edgeRanking = iter.next();
            	double rankValue = Double.parseDouble(edgeRanking.toString());
            	
            	if(lastRank<0 || rankValue==lastRank)
            	{
            		EdgeRanking highestBetweenness = (EdgeRanking) edgeRanking;
            		Edge e = (Edge)highestBetweenness.edge.getEqualEdge(graph);
            		e.setUserDatum("relationship.betweenness", rankValue, UserData.SHARED);
		            edgesRemoved.add(e);
		            graph.removeEdge(e);//highestBetweenness.edge);

	            	lastRank = rankValue;
            	}
            }
            System.out.println("[EBC]: Betweenness level done = "+(i+1));
        }

	    WeakComponentClusterer wcSearch = new WeakComponentClusterer();
	    ClusterSet clusterSet = wcSearch.extract(graph);
	    for (Iterator<Edge> iter = edgesRemoved.iterator(); iter.hasNext(); )
        graph.addEdge((Edge)iter.next());
	    
	    nameClusters(clusterSet);
	    return clusterSet;
	}
	
	public List<Edge> getEdgesRemoved()
	{
		return edgesRemoved;
	}
	
	@SuppressWarnings("unchecked")
	private void nameClusters(ClusterSet cs)
	{
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
		}
	}

}
