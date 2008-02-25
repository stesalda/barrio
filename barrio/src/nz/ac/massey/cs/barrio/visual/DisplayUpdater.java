package nz.ac.massey.cs.barrio.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.gui.OutputUI;
import prefuse.Display;
import prefuse.data.Node;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class DisplayUpdater {
	
	@SuppressWarnings("unchecked")
	public void update(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{
			Display display = (Display) OutputUI.panelGraph.getComponent(0);
			Iterator<VisualItem> nodeIterator = display.getVisualization().getVisualGroup("graph.nodes").tuples();
			Iterator<VisualItem> edgeIterator = display.getVisualization().getVisualGroup("graph.edges").tuples();
			
			List<String> clusterNames = new ArrayList<String>();
			
			while(nodeIterator.hasNext())
			{
				VisualItem node = nodeIterator.next();
				String id = node.getString("class.id");
				if(existsVertex(id, jungGraph)) 
					node.setVisible(true); 
				else node.setVisible(false);
				
				Iterator<edu.uci.ics.jung.graph.Vertex> vertexIterator = jungGraph.getVertices().iterator();
				while(vertexIterator.hasNext())
				{
					String clusterName = node.getString("class.cluster");
					edu.uci.ics.jung.graph.Vertex v = vertexIterator.next();
					
					if(id.equals(v.getUserDatum("class.id")))
					{
						node.set("class.cluster", clusterName);
						if(!clusterNames.contains(clusterName)) clusterNames.add(clusterName);
					}
				}
			}
			
			while(edgeIterator.hasNext())
			{
				VisualItem edge = edgeIterator.next();
				String id = edge.getString("id");
				if(existsEdge(id, jungGraph))
					edge.setVisible(true);
				else edge.setVisible(false);
			}
			
			
			removeOldAggregates(display);
			setNewAggregates(clusterNames, display);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void removeOldAggregates(Display display)
	{		
		AggregateTable at = (AggregateTable) display.getVisualization().getVisualGroup("aggregates");
		Iterator aitemIterator = at.tuples();
		while(aitemIterator.hasNext())
		{
			AggregateItem aitem = (AggregateItem) aitemIterator.next();
			if(aitem.getString("type").equals("cluster")) at.removeTuple(aitem);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void setNewAggregates(List<String> clusterNames, Display display)
	{
		AggregateTable at = (AggregateTable) display.getVisualization().getVisualGroup("aggregates");
		VisualGraph vg = (VisualGraph) display.getVisualization().getVisualGroup("graph");
		
		Iterator<String> clusterIter = clusterNames.iterator();
		while(clusterIter.hasNext())
		{
			AggregateItem aitem = (AggregateItem)at.addItem();
			String aCluster =  clusterIter.next();
			aitem.setString("type", "cluster");
			Iterator<Node> nodes = vg.nodes();
			while(nodes.hasNext())
			{
				prefuse.data.Node node = nodes.next();
				String cluster = node.getString("class.cluster");
				if(cluster.equals(aCluster)) aitem.addItem((VisualItem)node);
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
