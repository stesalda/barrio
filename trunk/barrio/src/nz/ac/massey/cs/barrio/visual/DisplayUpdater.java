package nz.ac.massey.cs.barrio.visual;

import java.util.Iterator;

import nz.ac.massey.cs.barrio.gui.OutputUI;
import prefuse.Display;
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
