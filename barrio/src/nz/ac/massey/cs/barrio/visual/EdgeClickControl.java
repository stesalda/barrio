package nz.ac.massey.cs.barrio.visual;

import java.awt.event.MouseEvent;

import prefuse.Visualization;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;
import prefuse.data.Edge;
import prefuse.data.Node;

public class EdgeClickControl extends ControlAdapter implements Control {

	private VisualItem lastUsedVisualItem = null;
	private Visualization vis = null;
	@Override
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item.equals(lastUsedVisualItem) && e.getButton()==MouseEvent.BUTTON1)
		{
			setSelectItem(lastUsedVisualItem, "false");
			lastUsedVisualItem = null;
			vis = item.getVisualization();
			vis.run("color");
			vis.run("layout2");
		}else
		if(item.getGroup().equals("graph.edges") && e.getButton()==MouseEvent.BUTTON1)
		{
			vis = item.getVisualization();
			if(lastUsedVisualItem!=null) setSelectItem(lastUsedVisualItem, "false");
			setSelectItem(item, "true");
			lastUsedVisualItem = item;
			vis.run("color");
			vis.run("layout2");
		}
	}
	
	
	private void setSelectItem(VisualItem item, String select)
	{
		item.setHighlighted(true);
		
		Edge edge = (Edge)item.getSourceTuple();
		edge.set("edge.isSelected", select);
		
		Node srcNode = edge.getSourceNode();
		srcNode.set("node.isSelected", select);
		
		Node destNode = edge.getTargetNode();
		destNode.set("node.isSelected", select);
		
		boolean b = select.equals("true");
		VisualItem vi1 = vis.getVisualItem("graph.nodes", srcNode);
		vi1.setHighlighted(b);
		VisualItem vi2 = vis.getVisualItem("graph.nodes", destNode);
		vi2.setHighlighted(b);
	}

}
