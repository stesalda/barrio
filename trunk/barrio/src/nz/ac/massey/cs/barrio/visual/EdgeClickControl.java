package nz.ac.massey.cs.barrio.visual;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import nz.ac.massey.cs.barrio.gui.OutputUI;

import prefuse.Visualization;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;
import prefuse.data.Edge;
import prefuse.data.Node;

public class EdgeClickControl extends ControlAdapter implements Control {

	@Override
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item.getGroup().equals("graph.edges"))
		{
			VisualHighlightingManager mgr = new VisualHighlightingManager();
			mgr.setSelectItem(item, String.valueOf(!item.get("edge.isSelected").equals("true")));
		}
	}
}
