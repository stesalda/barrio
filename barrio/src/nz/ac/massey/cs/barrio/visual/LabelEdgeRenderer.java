package nz.ac.massey.cs.barrio.visual;
//@AUTHOR Graham Jensen

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.VisualItem;

public class LabelEdgeRenderer extends EdgeRenderer {


	private String m_name = "relationship.type";
	


	public LabelEdgeRenderer() {
		super(Constants.EDGE_TYPE_CURVE,Constants.EDGE_ARROW_FORWARD);
		
	}


	/**
	 * @see prefuse.render.AbstractShapeRenderer#getRenderType(prefuse.visual.VisualItem)
	 */
	public int getRenderType(VisualItem item) {
		return RENDER_TYPE_DRAW;
	}

	/**
	 * @see prefuse.render.Renderer#render(java.awt.Graphics2D, prefuse.visual.VisualItem)
	 */
	public void render(Graphics2D g, VisualItem item) {
		super.render(g, item);
		
		CubicCurve2D.Float cc =new CubicCurve2D.Float();
		m_cubic.subdivide(cc, new CubicCurve2D.Float());
		double x = cc.getX2();
		double y = cc.getY2();
		Object o = item.get(m_name);
		if(o != null)
		{
			String s = o.toString();
			Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
			
			g.setColor(ColorLib.getColor(item.getInt(VisualItem.TEXTCOLOR)));
			g.drawString(s, (int)( x- r.getWidth()/2 ), (int)( y + r.getHeight()/2));
		}
		
	}


    


	public void setBounds(VisualItem item) {
		if ( !m_manageBounds ) return;
		Shape shape = getShape(item);
		if ( shape == null ) {
			item.setBounds(item.getX(), item.getY(), 0, 0);
			return;
		}
		GraphicsLib.setBounds(item, shape, getStroke(item));

	}




	protected static void getAlignedPoint(Point2D p, Rectangle2D r, int xAlign, int yAlign) {
		double x = r.getX(), y = r.getY(), w = r.getWidth(), h = r.getHeight();
		if ( xAlign == Constants.CENTER ) {
			x = x+(w/2);
		} else if ( xAlign == Constants.RIGHT ) {
			x = x+w;
		}
		if ( yAlign == Constants.CENTER ) {
			y = y+(h/2);
		} else if ( yAlign == Constants.BOTTOM ) {
			y = y+h;
		}
		p.setLocation(x,y);
	}

	public String getM_name() {
		return m_name;
	}

	public void setM_name(String m_name) {
		this.m_name = m_name;
	}



} // end of class EdgeRenderer

