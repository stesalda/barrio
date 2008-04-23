package nz.ac.massey.cs.barrio.visual;

import java.util.Iterator;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.visual.VisualItem;

public class VisualHighlightingManager {
	
	public void setSelectItem(VisualItem item, String select)
	{
		boolean b = select.equals("true");
		
		item.setHighlighted(b);
		
		Edge edge = (Edge)item.getSourceTuple();		
		Node srcNode = edge.getSourceNode();
		Node destNode = edge.getTargetNode();
		srcNode.set("node.isSelected", select);		
		destNode.set("node.isSelected", select);
		edge.set("edge.isSelected", select);
		
		Visualization vis = item.getVisualization();
		VisualItem vi1 = vis.getVisualItem("graph.nodes", srcNode);
		vi1.setHighlighted(b);
		VisualItem vi2 = vis.getVisualItem("graph.nodes", destNode);
		vi2.setHighlighted(b);
		
		if(!b) 
		{
			if(hasHighlightedEdges(srcNode)) srcNode.set("node.isSelected", "true");
			if(hasHighlightedEdges(destNode)) destNode.set("node.isSelected", "true");
		}
	}
	
	private boolean hasHighlightedEdges(Node node)
	{
		Iterator iter = node.getGraph().edges();
		while(iter.hasNext())
		{
			Edge e = (Edge) iter.next();
			if(e.get("edge.isSelected").equals("true") && (e.getSourceNode()==node || e.getTargetNode()==node))
			{
				return true;
			}
		}
		
		return false;
	}

}
