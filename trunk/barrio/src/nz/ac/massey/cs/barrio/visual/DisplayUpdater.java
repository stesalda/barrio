package nz.ac.massey.cs.barrio.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.constants.BarrioConstants;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import prefuse.Display;
import prefuse.data.Graph;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class DisplayUpdater {
	
	@SuppressWarnings("unchecked")
	private void update2(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{			
			Display display = (Display) OutputUI.panelGraph.getComponent(0);
			List<String> clusterNames = new ArrayList<String>();
			
			updateVisibility(display, jungGraph);
			removeOldAggregates(display);
			updateClusterNames(clusterNames, display, jungGraph);
			
			System.out.println("[DisplayUpdater]: clusters = "+clusterNames);
			
			setNewAggregates(clusterNames, display);
		}
	}
	
	
	


	@SuppressWarnings("unchecked")
	private void updateVisibility(Display display, edu.uci.ics.jung.graph.Graph jungGraph)
	{
		Iterator<VisualItem> nodeIterator = display.getVisualization().getVisualGroup(BarrioConstants.VISUAL_NODES).tuples();
		Iterator<VisualItem> edgeIterator = display.getVisualization().getVisualGroup(BarrioConstants.VISUAL_EDGES).tuples();
					
		while(nodeIterator.hasNext())
		{
			VisualItem node = nodeIterator.next();
			String id = node.getString("class.id");
			if(existsVertex(id, jungGraph)) 
				node.setVisible(true); 
			else node.setVisible(false);
		}
		
		while(edgeIterator.hasNext())
		{
			VisualItem edge = edgeIterator.next();
			String id = edge.getString("id");
			if(existsEdge(id, jungGraph))
				edge.setVisible(true);
			else edge.setVisible(false);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void removeOldAggregates(Display display)
	{		
		List<AggregateItem> items = new ArrayList<AggregateItem>();
		
		AggregateTable at = (AggregateTable) display.getVisualization().getVisualGroup("aggregates");
		Iterator aitemIterator = at.tuples();
		while(aitemIterator.hasNext())
		{
			AggregateItem aitem = (AggregateItem) aitemIterator.next();
			if(aitem.getString("type").equals("cluster"))
			{
				
				items.add(aitem);
//				aitemIterator.remove();
				//System.out.println("[DisplayUpdater]: Aggregate removed = "+at.removeTuple(aitem));
			}
		}
		
		for(AggregateItem item:items) {
			at.removeTuple(item);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void updateClusterNames(List clusterNames, Display display, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		Iterator<edu.uci.ics.jung.graph.Vertex> vertexIterator = jungGraph.getVertices().iterator();
		
		while(vertexIterator.hasNext())
		{
			edu.uci.ics.jung.graph.Vertex v = vertexIterator.next();
			String vertexId = v.getUserDatum("class.id").toString();
			String vertexCluster = v.getUserDatum("class.cluster").toString();
			if(!clusterNames.contains(vertexCluster)) clusterNames.add(vertexCluster);
			
			Iterator<VisualItem> nodeIterator = display.getVisualization().getVisualGroup(BarrioConstants.VISUAL_NODES).tuples();
			while(nodeIterator.hasNext())
			{
				VisualItem node = nodeIterator.next();
				String nodeId = node.getString("class.id");
				if(vertexId.equals(nodeId))
				{
					node.set("class.cluster", vertexCluster);
					break;
				}
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void setNewAggregates(List<String> clusterNames, Display display)
	{
		AggregateTable at = (AggregateTable) display.getVisualization().getVisualGroup(BarrioConstants.VISUAL_AGGREGATES);
		VisualGraph vg = (VisualGraph) display.getVisualization().getVisualGroup(BarrioConstants.VISUAL_GRAPH);
		
		Iterator<String> clusterIter = clusterNames.iterator();
		while(clusterIter.hasNext())
		{
			String aCluster =  clusterIter.next();
			
			AggregateItem aitem = (AggregateItem)at.addItem();
			aitem.setString("type", "cluster");
			
			Iterator<VisualItem> nodes = vg.nodes();
			while(nodes.hasNext())
			{
				VisualItem node = nodes.next();
				String cluster = node.getString("class.cluster");
				if(cluster.equals(aCluster)) aitem.addItem(node);
			}
			aitem.setVisible(false);
		}
	}
	
		
	
	@SuppressWarnings("unchecked")
	private boolean existsVertex(String id, edu.uci.ics.jung.graph.Graph jungGraph)
	{
		Iterator<edu.uci.ics.jung.graph.Vertex> vertexIterator = jungGraph.getVertices().iterator();
		while(vertexIterator.hasNext())
		{
			edu.uci.ics.jung.graph.Vertex vertex = vertexIterator.next();
			if(id.equals(vertex.getUserDatum("class.id").toString())) return true;
		}
		return false;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private boolean existsEdge(String id, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		Iterator<edu.uci.ics.jung.graph.Edge> edgeIterator = jungGraph.getEdges().iterator();
		while(edgeIterator.hasNext())
		{
			edu.uci.ics.jung.graph.Edge edge = edgeIterator.next();
			if(id.equals(edge.getUserDatum("id").toString())) return true;
		}
		return false;
	}

}
