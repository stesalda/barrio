package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.OverlayLayout;

import nz.ac.massey.cs.barrio.graphManager.GraphManager;
import nz.ac.massey.cs.barrio.graphManager.KnownGraphManagers;
import nz.ac.massey.cs.barrio.graphconverter.JungPrefuseBridge;
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.data.tuple.TupleSet;
import prefuse.util.display.DisplayLib;
import prefuse.visual.VisualItem;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

//@SuppressWarnings("serial")
public class VisualGraphFrame extends JFrame {
	
	private Graph graph = null;
	private JPanel graphPanel;
	private List<String> visualSettings = new ArrayList<String>();

	public VisualGraphFrame()
	{
        JPanel mainPanel = new JPanel(){
        	public boolean isOptimizedDrawingEnabled() {
                return false;
              }
        };
        LayoutManager overlay = new OverlayLayout(mainPanel);
        mainPanel.setLayout(overlay);
        
		JPanel controlsPanel = new JPanel();
		//controlsPanel.setBorder(BorderFactory.createEtchedBorder());
		controlsPanel.setAlignmentX(0.0f);
		controlsPanel.setAlignmentY(0.0f);			
		
		JPanel topPanel = new JPanel();
//		topPanel.setBackground(Color.red);
		JPanel bottomPanel = new JPanel();
//		bottomPanel.setBackground(Color.green);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3,5,5,5));
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		ImageIcon upIcon = new ImageIcon(this.getClass().getResource("icons/arrowUp.png"));
		JButton btnUp = new JButton();
		btnUp.setIcon(upIcon);
		btnUp.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnUp);
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		ImageIcon leftIcon = new ImageIcon(this.getClass().getResource("icons/arrowLeft.png"));
		JButton btnLeft = new JButton();
		btnLeft.setIcon(leftIcon);
		btnLeft.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnLeft);
		ImageIcon ziIcon = new ImageIcon(this.getClass().getResource("icons/zIn.png"));
		JButton btnZI = new JButton();
		btnZI.setIcon(ziIcon);
		btnZI.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnZI);
		ImageIcon ztfIcon = new ImageIcon(this.getClass().getResource("icons/zFit.png"));
		JButton btnZTF = new JButton();
		btnZTF.setIcon(ztfIcon);
		btnZTF.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnZTF);
		ImageIcon zoIcon = new ImageIcon(this.getClass().getResource("icons/zOut.png"));
		JButton btnZO = new JButton();
		btnZO.setIcon(zoIcon);
		btnZO.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnZO);
		ImageIcon rightIcon = new ImageIcon(this.getClass().getResource("icons/arrowRight.png"));
		JButton btnRight = new JButton();
		btnRight.setIcon(rightIcon);
		btnRight.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnRight);
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		ImageIcon downIcon = new ImageIcon(this.getClass().getResource("icons/arrowDown.png"));
		JButton btnDown = new JButton();
		btnDown.setIcon(downIcon);
		btnDown.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(btnDown);
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		topPanel.add(buttonsPanel);
		
		JPanel checksPanel = new JPanel(new GridLayout(7,0));
		final JCheckBox checkContainers = new JCheckBox();
		checkContainers.setText("View Containers");
		checksPanel.add(checkContainers);
		final JCheckBox checkNamespaces = new JCheckBox("View Namespaces");
		checksPanel.add(checkNamespaces);
		final JCheckBox checkDependencyClusters = new JCheckBox("View Dependency Clusters");
		checksPanel.add(checkDependencyClusters);
		JCheckBox checkRemovedEdges = new JCheckBox("View Removed Edges");
		checksPanel.add(checkRemovedEdges);
		
		JRadioButton classRadioButton = new JRadioButton("Class level");
		classRadioButton.setSelected(true);
		JRadioButton namespaceRadioButton = new JRadioButton("Namespace level");
		JRadioButton containerRadioButton = new JRadioButton("Container level");
		ButtonGroup levels = new ButtonGroup();
		levels.add(classRadioButton);
		levels.add(namespaceRadioButton);
		levels.add(containerRadioButton);
		checksPanel.add(classRadioButton);
		checksPanel.add(namespaceRadioButton);
		checksPanel.add(containerRadioButton);
		bottomPanel.add(checksPanel);

		controlsPanel.add(topPanel);
		controlsPanel.add(bottomPanel);
			
		mainPanel.add(controlsPanel);
        mainPanel.setOpaque(false);
		graphPanel = new JPanel(new BorderLayout());
		graphPanel.setAlignmentX(0.0f);
		graphPanel.setAlignmentY(0.0f);
		mainPanel.add(graphPanel);
		

		
		Toolkit toolkit = getToolkit();
		Dimension size = toolkit.getScreenSize();
		this.setTitle("Visual Graph");
		this.setSize(size.width-150, size.height-100);
        this.setLocation(size.width/2 - getWidth()/2, size.height/2 - getHeight()/2);
		this.add(mainPanel, BorderLayout.CENTER);
		this.setVisible(true);
		
		int width = 0;
		if(topPanel.getSize().width > width) width = topPanel.getWidth();
		if(bottomPanel.getWidth() > width) width = bottomPanel.getWidth();
		int height = topPanel.getHeight()+bottomPanel.getY()*2+bottomPanel.getHeight();
		controlsPanel.setMaximumSize(new Dimension(width,height));
		controlsPanel.updateUI();
		
		final double panValue = 100;
        final long duration = 1000;
        btnUp.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
					((Display)graphPanel.getComponent(0)).animatePan(0, 0+panValue, duration);				
			}        	
        });
        
        btnDown.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
					((Display)graphPanel.getComponent(0)).animatePan(0, 0-panValue, duration);				
			}        	
        });
        
        btnLeft.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
					((Display)graphPanel.getComponent(0)).animatePan(0+panValue, 0, duration);				
			}        	
        });
        
        btnRight.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
					((Display)graphPanel.getComponent(0)).animatePan(0-panValue, 0, duration);				
			}        	
        });
        
        btnZI.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
				{
					Display display  = (Display)graphPanel.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 1.2, duration);
				}
            }
        });
        
        btnZO.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
				{
					Display display  = (Display)graphPanel.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 0.8, duration);
				}
            }
        });
        
        btnZTF.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(graphPanel.getComponentCount()>0)
				{
					Display display  = (Display)graphPanel.getComponent(0);
					DisplayLib.fitViewToBounds(display,display.getVisualization().getBounds(Visualization.ALL_ITEMS), duration);
				}
            }
        });
        
        checkDependencyClusters.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(checkDependencyClusters.isSelected()) visualSettings.add(checkDependencyClusters.getText());
				else visualSettings.remove(checkDependencyClusters.getText());
				updateVisualElements();
			}        	
        });
        
        checkNamespaces.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(checkNamespaces.isSelected()) visualSettings.add(checkNamespaces.getText());
				else visualSettings.remove(checkNamespaces.getText());
				updateVisualElements();
			}        	
        });
        
        checkContainers.addActionListener(new ActionListener(){
			//@Override
			public void actionPerformed(ActionEvent e) {
				if(checkContainers.isSelected()) visualSettings.add(checkContainers.getText());
				else visualSettings.remove(checkContainers.getText());
				updateVisualElements();
			}        	
        });
//        
//        checkRemovedEdges.addSelectionListener(new SelectionListener(){
//
//                     public void widgetDefaultSelected(SelectionEvent e) {}
//     
//                             public void widgetSelected(SelectionEvent e) {
//                                     visualControlCheck(checkRemovedEdges);
//                             }
//        });
        
        classRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(graph==null) return;				
				JungPrefuseBridge bridge = new JungPrefuseBridge();
				updateVis(bridge.convert(graph));
			}
        	
        });
        
        namespaceRadioButton.addActionListener(new ActionListener(){
        	
			public void actionPerformed(ActionEvent e) {
				if(graph==null) return;				
				JungPrefuseBridge bridge = new JungPrefuseBridge();
				updateVis(bridge.convert(buildNamespaceGraph(graph)));
			}        	
        });
        
        containerRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(graph==null) return;				
				JungPrefuseBridge bridge = new JungPrefuseBridge();
				updateVis(bridge.convert(buildContainerGraph(graph)));
			}
        	
        });

	}
	
	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		if(this.graph!=graph){
			this.graph = graph;
			JungPrefuseBridge bridge = new JungPrefuseBridge();
			updateVis(bridge.convert(graph));
			updateVisualElements();
		}		
	}
	
	private void updateVis(prefuse.data.Graph graph)
	{
		DisplayBuilder db = new DisplayBuilder();
		Display graphDisplay = db.getDisplay(graph);
		graphPanel.removeAll();
		graphPanel.add(graphDisplay);
		graphPanel.updateUI();
	}
	
	
	private void updateVisualElements() 
    {
        if(graphPanel.getComponentCount()>0)
        {
            Visualization vis = ((Display)graphPanel.getComponent(0)).getVisualization();
            TupleSet aggregates = vis.getGroup("aggregates");
            Iterator<VisualItem> iter = aggregates.tuples();
            while(iter.hasNext())
            {
                VisualItem item = iter.next();
                String name = item.get("aggregate.name").toString();
                if(visualSettings.contains(name)) item.setVisible(true);
                else item.setVisible(false);
            }
            
            TupleSet edges = vis.getVisualGroup("graph.edges");
            Iterator<VisualItem> edgeIter = edges.tuples();
            while(edgeIter.hasNext())
            {
                VisualItem edge = edgeIter.next();
                if(!edge.canGetString("relationship.state")) continue;
	            if(!visualSettings.contains("View Removed Edges") && edge.getString("relationship.state").equals("removed")) edge.setVisible(false);
	            else edge.setVisible(true);
            }
        }               
    }

	
	
	private Graph buildNamespaceGraph(Graph graph)
	{
		Graph namespaceGraph = new DirectedSparseGraph();
		GraphManager manager = KnownGraphManagers.all().get(0);
		manager.setGraph(graph);
		int vertexId = 0;
		for(String container:manager.getContainers())
		{
			for(String namespace: manager.getNamespaces(container))
			{
				Vertex v = new DirectedSparseVertex();
				v.addUserDatum("class.id", String.valueOf(vertexId), UserData.SHARED);
				v.addUserDatum("class.jar", container, UserData.SHARED);
				v.addUserDatum("class.packageName", namespace, UserData.SHARED);
				namespaceGraph.addVertex(v);
				vertexId++;
			}
		}
		
		List<TempEdge> tempEdges = new ArrayList<TempEdge>();
		Set<Edge> edges = graph.getEdges();
		for(Edge edge:edges)
		{
			String srcC = ((Vertex)edge.getEndpoints().getFirst()).getUserDatum("class.jar").toString();
			String srcN = ((Vertex)edge.getEndpoints().getFirst()).getUserDatum("class.packageName").toString();
			String destC = ((Vertex)edge.getEndpoints().getSecond()).getUserDatum("class.jar").toString();
			String destN = ((Vertex)edge.getEndpoints().getSecond()).getUserDatum("class.packageName").toString();
			if(srcC.equals(destC) && srcN.equals(destN)) continue;
			TempEdge te = new TempEdge();
			te.setSrcC(srcC);
			te.setSrcN(srcN);
			te.setDestC(destC);
			te.setDestN(destN);
			if(!tempEdges.contains(te)) tempEdges.add(te);
		}
		
		for(TempEdge te:tempEdges)
		{
			Vertex src = null;
			Vertex dest = null;
			Set<Vertex> verts = namespaceGraph.getVertices();
			for(Vertex v:verts)
			{
				if(te.getSrcC().equals(v.getUserDatum("class.jar")) && te.getSrcN().equals(v.getUserDatum("class.packageName")))
					src = v;				
				if(te.getDestC().equals(v.getUserDatum("class.jar")) && te.getDestN().equals(v.getUserDatum("class.packageName")))
					dest = v;
				
				if(src!=null && dest!=null) break;
			}
			if(src!=null && dest!=null) 
			{
				Edge e = new DirectedSparseEdge(src, dest);
				e.addUserDatum("sourceId", src.getUserDatum("class.id"), UserData.SHARED);
				e.addUserDatum("targetId", dest.getUserDatum("class.id"), UserData.SHARED);
				namespaceGraph.addEdge(e);
			}
		}
		System.out.println("[VGF]: namespaceGraph = "+namespaceGraph.numVertices()+" "+namespaceGraph.numEdges());
		return namespaceGraph;
	}
	
	
	private Graph buildContainerGraph(Graph graph)
	{
		Graph containerGraph = new DirectedSparseGraph();
		GraphManager manager = KnownGraphManagers.all().get(0);
		manager.setGraph(graph);
		int vertexId = 0;
		
		for(String container:manager.getContainers())
		{
			Vertex v = new DirectedSparseVertex();
			v.addUserDatum("class.id", String.valueOf(vertexId), UserData.SHARED);
			v.addUserDatum("class.jar", container, UserData.SHARED);
			containerGraph.addVertex(v);
			vertexId++;			
		}
		
		List<TempEdge> tempEdges = new ArrayList<TempEdge>();
		Set<Edge> edges = graph.getEdges();
		for(Edge edge:edges)
		{
			String srcC = ((Vertex)edge.getEndpoints().getFirst()).getUserDatum("class.jar").toString();
			String destC = ((Vertex)edge.getEndpoints().getSecond()).getUserDatum("class.jar").toString();
			if(srcC.equals(destC)) continue;
			TempEdge te = new TempEdge();
			te.setSrcC(srcC);
			te.setSrcN("null");
			te.setDestC(destC);
			te.setDestN("null");
			if(!tempEdges.contains(te)) tempEdges.add(te);
		}
		
		for(TempEdge te:tempEdges)
		{
			Vertex src = null;
			Vertex dest = null;
			Set<Vertex> verts = containerGraph.getVertices();
			for(Vertex v:verts)
			{
				if(te.getSrcC().equals(v.getUserDatum("class.jar"))) src = v;				
				if(te.getDestC().equals(v.getUserDatum("class.jar"))) dest = v;
				if(src!=null && dest!=null) break;
			}
			if(src!=null && dest!=null) 
			{
				Edge e = new DirectedSparseEdge(src, dest);
				e.addUserDatum("sourceId", src.getUserDatum("class.id"), UserData.SHARED);
				e.addUserDatum("targetId", dest.getUserDatum("class.id"), UserData.SHARED);
				containerGraph.addEdge(e);
			}
		}
		System.out.println("[VGF]: namespaceGraph = "+containerGraph.numVertices()+" "+containerGraph.numEdges());
		return containerGraph;

	}
	
	
	
	
	private class TempEdge
	{
		private String srcC = null;
		private String srcN = null;
		private String destC = null;
		private String destN = null;
		
		public String getSrcC() {
			return srcC;
		}

		public void setSrcC(String srcC) {
			this.srcC = srcC;
		}

		public String getSrcN() {
			return srcN;
		}

		public void setSrcN(String srcN) {
			this.srcN = srcN;
		}

		public String getDestC() {
			return destC;
		}

		public void setDestC(String destC) {
			this.destC = destC;
		}

		public String getDestN() {
			return destN;
		}

		public void setDestN(String destN) {
			this.destN = destN;
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TempEdge)) return false;
			TempEdge te = (TempEdge) obj;
			if(!te.getSrcC().equals(srcC)) return false;
			if(!te.getSrcN().equals(srcN)) return false;
			if(!te.getDestC().equals(destC)) return false;
			if(!te.getDestN().equals(destN)) return false;
			return true;
		}		
	}
}
