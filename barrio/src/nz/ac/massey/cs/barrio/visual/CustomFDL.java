package nz.ac.massey.cs.barrio.visual;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class CustomFDL extends ForceDirectedLayout{

	public CustomFDL(String arg0, boolean arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public CustomFDL(String arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public CustomFDL(String arg0, ForceSimulator arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public CustomFDL(String arg0, ForceSimulator arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public CustomFDL(String arg0) {
		super(arg0);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected float getMassValue(VisualItem arg0) {
		// TODO Auto-generated method stub
		return super.getMassValue(arg0)*3;
	}

	@Override
	protected float getSpringCoefficient(EdgeItem arg0) {
		// TODO Auto-generated method stub
		return super.getSpringCoefficient(arg0)/10;
	}	
	

}
