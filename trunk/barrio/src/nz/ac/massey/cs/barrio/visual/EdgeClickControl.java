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

	private OutputUI output;
	
	public EdgeClickControl(OutputUI output) 
	{
		this.output = output;
	}

	@Override
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item.getGroup().equals("graph.edges"))
		{
			VisualHighlightingManager mgr = new VisualHighlightingManager();
			mgr.setSelectItem(item, String.valueOf(!item.get("edge.isSelected").equals("true")));
			
			int rows = output.getTable().getModel().getRowCount();
			for(int i=0; i<rows; i++)
			{
				if(item.get("id").equals(output.getTable().getModel().getValueAt(i,0)))
				{
					String value = output.getTable().getModel().getValueAt(i, 6).toString();
					if(value==null) value="false";
					boolean newValue = !value.equals("true");
					output.getTable().getModel().setValueAt(newValue, i, 6);
				}
			}
			
		}
	}
}
