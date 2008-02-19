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
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
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

public class PrefuseGraphBuilder {
	
	private prefuse.data.Graph prefuseGraph;  
	private edu.uci.ics.jung.graph.Graph jungGraph;
	private List removedEdges;
	private List<String> packages;
	private List<String> jars;
	private List<String> clusters;
	
	public PrefuseGraphBuilder(edu.uci.ics.jung.graph.Graph jGraph, List list) 
	{
		packages = new ArrayList<String>();
		jars = new ArrayList<String>();
		clusters = new ArrayList<String>();
		
		prefuseGraph = new prefuse.data.Graph(true);
		prefuseGraph.addColumn("class.id", String.class);
		prefuseGraph.addColumn("class.name", String.class);
		prefuseGraph.addColumn("class.jar", String.class);
		prefuseGraph.addColumn("class.packageName", String.class);
		prefuseGraph.addColumn("class.isInterface", String.class);
		prefuseGraph.addColumn("class.isAbstract", String.class);
		prefuseGraph.addColumn("class.access", String.class);
		prefuseGraph.addColumn("class.isException", String.class);
		prefuseGraph.addColumn("class.expression", new LabelExpression("class.jar","class.packageName","class.name"));
		prefuseGraph.addColumn("class.icon", new ImageExpression("class.isInterface", "class.isException", "class.isAbstract", "class.access"));
		prefuseGraph.addColumn("node.isSelected", boolean.class);
		prefuseGraph.addColumn("class.cluster", String.class);

		prefuseGraph.addColumn("relationship.type", String.class);
		prefuseGraph.addColumn("relationship.state", String.class);
		prefuseGraph.addColumn("edge.isSelected", String.class);
		
		jungGraph = jGraph;
		removedEdges = list;
	}
	
	public void setJungGraph(edu.uci.ics.jung.graph.Graph jungGraph) {
		this.jungGraph = jungGraph;
	}

	public void buildPrefuseGraph()
	{
		addNodes();
		addEdges();
	}
		
	private void addNodes() 
	{
		//Create prefuse node for every jung graph vertex
		Iterator iter = jungGraph.getVertices().iterator();
		while(iter.hasNext())
		{
			edu.uci.ics.jung.graph.Vertex v = (edu.uci.ics.jung.graph.Vertex) iter.next();
			String classID = v.getUserDatum("class.id").toString();
			String className = v.getUserDatum("class.name").toString();
			String jar = v.getUserDatum("class.jar").toString();
			String classPackageName = v.getUserDatum("class.packageName").toString();
			String isInterface = v.getUserDatum("class.isInterface").toString();
			String isAbstract = v.getUserDatum("class.isAbstract").toString();
			String access = v.getUserDatum("class.access").toString();
			String isException = v.getUserDatum("class.isException").toString();
			String clusterName = v.getUserDatum("class.cluster").toString();
			
					
			prefuse.data.Node node = prefuseGraph.addNode();
			node.set("class.id", classID);
			node.set("class.name", className);
			node.set("class.jar", jar);
			node.set("class.packageName", classPackageName);
			node.set("class.isInterface", isInterface);
			node.set("class.isAbstract", isAbstract);
			node.set("class.access", access);
			node.set("class.isException", isException);
			node.set("node.isSelected", "false");
			node.set("class.cluster", clusterName);
			
			addPackageJarCluster(classPackageName, jar, clusterName);
		}
	}
	
	private void addPackageJarCluster(String packageName, String jarName, String clusterName)
	{
		if(!packages.contains(packageName)) packages.add(packageName);
		if(!jars.contains(jarName)) jars.add(jarName);
		if(!clusters.contains(clusterName)) clusters.add(clusterName);
	}
	
	private void addEdges() 
	{
		Iterator<?> iter = jungGraph.getEdges().iterator();
		while(iter.hasNext())
		{
			edu.uci.ics.jung.graph.Edge jungEdge = (edu.uci.ics.jung.graph.Edge)iter.next();
			String state = "";
			if(removedEdges.contains(jungEdge)) state = "removed";
			edu.uci.ics.jung.graph.Vertex jungVertex1 = (edu.uci.ics.jung.graph.Vertex) jungEdge.getEndpoints().getFirst();
			edu.uci.ics.jung.graph.Vertex jungVertex2 = (edu.uci.ics.jung.graph.Vertex) jungEdge.getEndpoints().getSecond();
			String jungSrcId = jungVertex1.getUserDatum("class.id").toString();
			String jungDestId = jungVertex2.getUserDatum("class.id").toString();
			String relationshipType = jungEdge.getUserDatum("relationship.type").toString();

			prefuse.data.Node prefuseNodeSrc = null;
			prefuse.data.Node prefuseNodeDest = null;
			
			int n = prefuseGraph.getNodeCount();
			for(int j=0; j<n; j++)
			{
				prefuse.data.Node prefuseNode = prefuseGraph.getNode(j);
				
				if(jungSrcId.equals(prefuseNode.get("class.id"))) prefuseNodeSrc = prefuseNode;
				if(jungDestId.equals(prefuseNode.get("class.id"))) prefuseNodeDest = prefuseNode;
				if(prefuseNodeSrc!=null && prefuseNodeDest!=null)
				{
					prefuse.data.Edge edge = prefuseGraph.addEdge(prefuseNodeSrc, prefuseNodeDest);
					edge.set("relationship.type", relationshipType);
					edge.set("relationship.state", state);
					edge.set("edge.isSelected", "false");
					break;
				}
			}
		}
	}
	
	public Display getDisplay()
	{
		final Visualization vis = new Visualization();
		AggregateTable at = vis.addAggregates("aggregates");
        at.addColumn(VisualItem.POLYGON, float[].class);
        at.addColumn("type", String.class);
        

		
		//vis.addGraph("graph", prefuseGraph);
		VisualGraph vg = vis.addGraph("graph", prefuseGraph); 
		
		Iterator<String> jarIter = jars.iterator();
        while(jarIter.hasNext())
        {
        	AggregateItem aitem = (AggregateItem)at.addItem();
        	String aJar =  jarIter.next();
        	aitem.setString("type", "jar");
        	Iterator<Node> nodes = vg.nodes();
        	while(nodes.hasNext())
        	{
        		prefuse.data.Node node = nodes.next();
        		String jar = node.getString("class.jar");
        		if(jar.equals(aJar)) aitem.addItem((VisualItem)node);
        	}
        	aitem.setVisible(false);
        }
        
        Iterator<String> clusterIter = clusters.iterator();
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
		
        Iterator<String> packs = packages.iterator();
        while(packs.hasNext())
        {
        	AggregateItem aitem = (AggregateItem)at.addItem();
        	String aPack =  packs.next();
        	aitem.setString("type", "package");
        	Iterator<Node> nodes = vg.nodes();
        	while(nodes.hasNext())
        	{
        		prefuse.data.Node node = nodes.next();
        		String pack = node.getString("class.packageName");
        		if(pack.equals(aPack)) aitem.addItem((VisualItem)node);
        	}
        	aitem.setVisible(false);
        }
		
		AbstractPredicate nodeSelectPredicate = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String isSelected = t.get("node.isSelected").toString();
				return isSelected.equals("true");
			}
		};
		
		AbstractPredicate abstractPredicate = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String state = t.get("relationship.state").toString();
		    	if(state.equals("removed"))return true;
		        return false;
			}
		};
		
		AbstractPredicate abstractPredicate2 = new AbstractPredicate()
		{
			public boolean getBoolean(Tuple t) {
				String isSelected = t.get("edge.isSelected").toString();
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
        ColorAction aFill3 = new DataColorAction("aggregates", "type", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
		
		color.add(a1);
		color.add(a2);
		color.add(a3);
		color.add(a4);
		color.add(a5);
		color.add(a6);
		color.add(aStroke);
		//color.add(aFill);
		//color.add(aFill2);
		color.add(aFill3);
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
        er.setArrowHeadSize(15,20);
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
		dis.addControlListener(new AggregateDragControl());
		
		return dis;
	}
}
