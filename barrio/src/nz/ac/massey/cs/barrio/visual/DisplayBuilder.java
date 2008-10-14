package nz.ac.massey.cs.barrio.visual;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class DisplayBuilder {
	
	private List<String> packages;
	private List<String> jars;
	private List<String> clusters;
	
	public DisplayBuilder() 
	{
		packages = new ArrayList<String>();
		jars = new ArrayList<String>();
		clusters = new ArrayList<String>();
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Display getDisplay(Graph prefuseGraph)
	{
		setPackagesJars(prefuseGraph);
		
		final Visualization vis = new Visualization();
		AggregateTable at = vis.addAggregates("aggregates");
        at.addColumn(VisualItem.POLYGON, float[].class);
        at.addColumn("type", String.class);
		at.addColumn("aggregate.name", String.class);
        

		
		//vis.addGraph("graph", prefuseGraph);
		VisualGraph vg = vis.addGraph("graph", prefuseGraph); 
		System.out.println("[DispalayBuilder]: prefuse graph = "+ vg.getNodeCount()+" "+vg.getEdgeCount());
		
		Iterator<String> jarIter = jars.iterator();
        while(jarIter.hasNext())
        {
        	AggregateItem aitem = (AggregateItem)at.addItem();
        	aitem.set("aggregate.name", "View Containers");
        	String aJar =  jarIter.next();
        	aitem.setString("type", "jar");
        	Iterator<Node> nodes = vg.nodes();
        	while(nodes.hasNext())
        	{
        		prefuse.data.Node node = nodes.next();
        		String jar = "";
        		if(node.canGetString("class.jar")) jar = node.getString("class.jar");
        		if(jar.equals(aJar)) aitem.addItem((VisualItem)node);
        	}
        	aitem.setVisible(false);
        }
        
        Iterator<String> clusterIter = clusters.iterator();
        while(clusterIter.hasNext())
        {
        	AggregateItem aitem = (AggregateItem)at.addItem();
        	aitem.set("aggregate.name", "View Dependency Clusters");
        	String aCluster =  clusterIter.next();
        	aitem.setString("type", "cluster");
        	Iterator<Node> nodes = vg.nodes();
        	while(nodes.hasNext())
        	{
        		prefuse.data.Node node = nodes.next();
        		String cluster = "";
        		if(node.canGetString("class.cluster")) cluster = node.getString("class.cluster");
        		if(cluster.equals(aCluster)) aitem.addItem((VisualItem)node);
        	}
        	aitem.setVisible(false);
        }
		
        Iterator<String> packs = packages.iterator();
        while(packs.hasNext())
        {
        	AggregateItem aitem = (AggregateItem)at.addItem();
        	aitem.set("aggregate.name", "View Namespaces");
        	String aPack =  packs.next();
        	aitem.setString("type", "package");
        	Iterator<Node> nodes = vg.nodes();
        	while(nodes.hasNext())
        	{
        		prefuse.data.Node node = nodes.next();
        		String pack = "";
        		if(node.canGetString("class.packageName")) pack = node.getString("class.packageName");
        		if(pack.equals(aPack)) aitem.addItem((VisualItem)node);
        	}
        	aitem.setVisible(false);
        }
		
		AbstractPredicate nodeSelectPredicate = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String isSelected = "";
				if(t.canGetString("node.isSelected")) isSelected = t.get("node.isSelected").toString();
				return isSelected.equals("true");
			}
		};
		
		AbstractPredicate abstractPredicate = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String state = "";
				if(t.canGetString("relationship.state")) state = t.get("relationship.state").toString();
		    	if(state.equals("removed"))return true;
		        return false;
			}
		};
		
		AbstractPredicate abstractPredicate2 = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String isSelected = "";
				if(t.canGetString("edge.isSelected")) isSelected = t.get("edge.isSelected").toString();
		    	return isSelected.equals("true");
			}
		};
		
		ActionList color = new ActionList();
		int myRemovedColor = ColorLib.rgb(255,0,0);
		int mySelectColor = ColorLib.rgb(0,0,255);		
		int mySelectFillColor = ColorLib.rgb(0,110,255);
		
		ColorAction a1 = new ColorAction("graph.nodes", VisualItem.STROKECOLOR, Color.BLACK.hashCode());
		a1.add(nodeSelectPredicate, mySelectColor);
		
		Action a2 = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, Color.BLACK.hashCode());
		
		ColorAction a3 = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(200,255,255));
		a3.add(nodeSelectPredicate, mySelectFillColor);
		
		ColorAction a4 = new ColorAction("graph.edges", VisualItem.STROKECOLOR, Color.BLACK.hashCode());
		a4.add(abstractPredicate2, mySelectColor);
		a4.add(abstractPredicate, myRemovedColor);
		
		ColorAction a5 = new ColorAction("graph.edges", VisualItem.TEXTCOLOR, Color.BLACK.hashCode());
		a5.add(abstractPredicate2, mySelectColor);
		a5.add(abstractPredicate, myRemovedColor);
		
		ColorAction a6 = new ColorAction("graph.edges", VisualItem.FILLCOLOR, Color.BLACK.hashCode());
		a6.add(abstractPredicate2, mySelectColor);
		a6.add(abstractPredicate, myRemovedColor);
		
		ColorAction aStroke = new ColorAction("aggregates", VisualItem.STROKECOLOR);
		aStroke.setDefaultColor(ColorLib.rgb(0,0,0));
		
		int[] palette = new int[] {ColorLib.rgba(0,0,200,125),ColorLib.rgba(0,200,0,125),ColorLib.rgba(200,0,0,125)};
        ColorAction aFill = new DataColorAction("aggregates", "type", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
		
		color.add(a1);
		color.add(a2);
		color.add(a3);
		color.add(a4);
		color.add(a5);
		color.add(a6);
		color.add(aStroke);
		color.add(aFill);
		vis.putAction("color", color);
		
		
		
		ActionList animate = new ActionList(10000);
		//animate.add(new CustomFDL("graph"));
		int edgeLength = 200;
		if(prefuseGraph.getEdges().getTupleCount()>200) edgeLength = prefuseGraph.getEdges().getTupleCount();
		
		
        
		
		int nNodes = prefuseGraph.getNodeCount();
		if(nNodes > 1200)
		{
			RadialTreeLayout rtl = new RadialTreeLayout("graph",edgeLength);
			rtl.setLayoutAnchor(new Point(0,0));
			animate.add(rtl);
		}
		else
		{
			CustomFDL fdl =new CustomFDL("graph");
			fdl.setLayoutAnchor(new Point(300,300));
			animate.add(fdl);
		}
		
		animate.add(color);  
        animate.add(new AggregateLayout("aggregates"));
        animate.add(new RepaintAction());
        vis.putAction("layout", animate);
        vis.run("layout");
        
        ActionList refresh = new ActionList(Activity.INFINITY);
        refresh.add(color);
        refresh.add(new AggregateLayout("aggregates"));
        refresh.add(new RepaintAction());        
        vis.putAction("layout2", refresh);
        vis.run("layout2");
        
        

        LabelRenderer lr = new LabelRenderer("class.expression", "class.icon");
        lr.setImagePosition(Constants.LEFT);
        lr.setImageTextPadding(5);
        lr.setRoundedCorner(8,8);
        lr.setHorizontalPadding(10);
        lr.setVerticalPadding(5);
        lr.setManageBounds(true);
        
        EdgeRenderer er = new LabelEdgeRenderer();
        er.setArrowHeadSize(30,40);
        er.setDefaultLineWidth(1.5);
        er.setManageBounds(true);
        
        Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer)polyR).setCurveSlack(0.05f);
        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(lr);
        drf.setDefaultEdgeRenderer(er);
        drf.add("ingroup('aggregates')", polyR);
		vis.setRendererFactory(drf);
        
		Display dis = new Display(vis);
		dis.setHighQuality(true);
		dis.addControlListener(new PanControl());
		dis.addControlListener(new ZoomToFitControl());
		dis.addControlListener(new EdgeClickControl());
		dis.addControlListener(new ZoomControl());
		dis.addControlListener(new WheelZoomControl());
		dis.addControlListener(new AggregateDragControl());
		
		return dis;
	}



	@SuppressWarnings("unchecked")
	private void setPackagesJars(Graph prefuseGraph) 
	{
		Iterator<Node> nodeIter = prefuseGraph.nodes();
		while(nodeIter.hasNext())
		{
			Node node = nodeIter.next();
			String pack = "";
			String jar = "";
			String cluster = "";
			if(node.canGetString("class.packageName")) pack = node.getString("class.packageName");
			if(node.canGetString("class.jar")) jar = node.getString("class.jar");
			if(node.canGetString("class.cluster")) cluster = node.getString("class.cluster");
			if(!packages.contains(pack)) packages.add(pack);
			if(!jars.contains(jar)) jars.add(jar);
			if(!clusters.contains(cluster)) clusters.add(cluster);
		}
	}
}
