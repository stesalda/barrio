package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.constants.BarrioConstants;
import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.filters.NodeFilter;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.outputs.OutputGenrator;
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;
import nz.ac.massey.cs.barrio.visual.DisplayUpdater;
import nz.ac.massey.cs.barrio.visual.JungPrefuseBridge;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.io.GraphMLFile;

public class InputUI extends Composite{
	
	private Graph graph = null;
	private Graph filteredGraph = null;
	private int separationLevel = 0;
	private List<Edge> removedEdges = null;
	
	private Button btnBrowse;
	private Button btnRefresh;
	private Button btnExport;
	
	private List<String> activeFilters = new ArrayList<String>();
	private List<Filter> knownFilters = new ArrayList<Filter>();
	private DisplayUpdater updater = new DisplayUpdater();
	private boolean pan = false;
	protected static Composite comp;
	
	public InputUI(Composite parent, int style) {
		   super(parent, style);
		   this.setLayout(new GridLayout());
		   GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		   separatorData.widthHint = 200;
		   
		   Label lblXML = new Label(this, SWT.NONE);
		   lblXML.setText("Open Annotated XML file");

		   btnBrowse = new Button(this, SWT.PUSH | SWT.CENTER);
		   btnBrowse.setText("Browse"); 
		   
		   Label separator1 = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator1.setLayoutData(separatorData);
		   //-----------------------------------------------------------
		   
		   Label lblFilters = new Label(this, SWT.NONE);
		   lblFilters.setText("Filter out:");
		   
		   Label lblNodes = new Label(this, SWT.NONE);
		   lblNodes.setText("Nodes (classes)");
		   
		   final List<NodeFilter> nodeFilters = KnownNodeFilters.all();
		   System.out.println("[inputUI]: nodeFilters = " + nodeFilters.size());
		   Iterator<NodeFilter> iter = nodeFilters.iterator();
		   while (iter.hasNext())
		   {
			   NodeFilter nf = iter.next();
			   knownFilters.add(nf);
			   String label = nf.getName();
			   
			   final Button checkboxNode = new Button(this, SWT.CHECK);
			   checkboxNode.setText(label);
			   checkboxNode.addSelectionListener(new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						checkboxClick(checkboxNode);
					}
			   });
		   }
		   
		   Label lblEdges = new Label(this, SWT.NONE);
		   lblEdges.setText("Edges (relationships)");
		   
		   final List<EdgeFilter> edgeFilters = KnownEdgeFilters.all();
		   System.out.println("[inputUI]: edgeFilters = " + edgeFilters.size());
		   Iterator<EdgeFilter> edgeIter = edgeFilters.iterator();
		   while (edgeIter.hasNext())
		   {
			   EdgeFilter ef = edgeIter.next();;
			   knownFilters.add(ef);
			   String edgeFilterLabel = ef.getName();
			   
			   final Button checkboxEdge = new Button(this, SWT.CHECK);
			   checkboxEdge.setText(edgeFilterLabel); 
			   checkboxEdge.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					checkboxClick(checkboxEdge);
				}
				   
			   });
		   }
		   
		   Label separator2 = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator2.setLayoutData(separatorData);
		   //-------------------------------------------------------------
		   
		   final Label lblSeparation = new Label(this, SWT.NONE);
		   lblSeparation.setText("Separation level = 0");
		   GridData lblSeparationtData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   lblSeparationtData.widthHint = 120;
		   lblSeparation.setLayoutData(lblSeparationtData);
		   
		   final Scale slider = new Scale(this, SWT.HORIZONTAL);
		   slider.setMinimum(0);
		   slider.setMaximum(500);
		   slider.setIncrement(1);
		   slider.setPageIncrement(1);
		   slider.setSelection(0);
		   GridData sliderData = new GridData(GridData.FILL_HORIZONTAL);
		   slider.setLayoutData(sliderData);
		   
		   Label separator3 = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator3.setLayoutData(separatorData);
		   //--------------------------------------------------------------
		   
		   btnRefresh = new Button(this, SWT.PUSH | SWT.CENTER);
		   btnRefresh.setText("Refresh");
		   GridData refreshData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   refreshData.heightHint = 40;
		   refreshData.widthHint = 90;
		   btnRefresh.setLayoutData(refreshData);
		   
		   btnExport = new Button(this, SWT.PUSH | SWT.CENTER);
		   btnExport.setText("Export Results");
		   GridData exportData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   exportData.widthHint = 90;
		   btnExport.setLayoutData(exportData);
		   //----------------------------------------------------------------
		   
		   
		   
		   //visual display controls ----------------------------------------
		   comp = new Composite(this, SWT.NONE);
		   comp.setVisible(false);
		   final Button btnUp = new Button(comp, SWT.ARROW|SWT.UP);
		   btnUp.setBounds(50,0,20,20);
		   Button btnLeft = new Button(comp, SWT.ARROW|SWT.LEFT);
		   btnLeft.setBounds(0,25,20,20);
		   Button btnZoomOut = new Button(comp, SWT.PUSH);
		   btnZoomOut.setBounds(25,25,20,20);
		   btnZoomOut.setText("-");
		   Button btnZoomToFit = new Button(comp, SWT.PUSH);
		   btnZoomToFit.setBounds(50,25,20,20);
		   btnZoomToFit.setText("=");
		   Button btnZoomIn = new Button(comp, SWT.PUSH);
		   btnZoomIn.setBounds(75,25,20,20);
		   btnZoomIn.setText("+");
		   Button btnRight = new Button(comp, SWT.ARROW|SWT.RIGHT);
		   btnRight.setBounds(100,25,20,20);
		   Button btnDown = new Button(comp, SWT.ARROW|SWT.DOWN);
		   btnDown.setBounds(50,50,20,20);
		   //----------------------------------------------------------------
		   
		   
		   //Events
		   btnBrowse.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }

			      public void widgetSelected(SelectionEvent e) {
			    	  btnBrowseClick();
			    	  btnRefreshClick(nodeFilters, edgeFilters);
			      }  
		   });
		   
		   slider.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }

			      public void widgetSelected(SelectionEvent e) {
			    	  sliderMove(lblSeparation, slider.getSelection());
			      }  
		   });
		   
		   btnRefresh.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }

			      public void widgetSelected(SelectionEvent e) {
			    	  btnRefreshClick(nodeFilters, edgeFilters);
			      }  
		   });
		   
		   btnExport.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }

			      public void widgetSelected(SelectionEvent e) {
			    	  btnExportClick();
			      }  
		   });

		   final double panValue = 100;
		   final long duration = 1000;
		   btnUp.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
	
				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0, 0-panValue, duration);
				}
				   
			   });
		   
		   btnDown.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0, panValue, duration);
				}
				   
			   });
		   
		   btnLeft.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0-panValue, 0, duration);
				}
				   
			   });
		   
		   btnRight.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(panValue, 0, duration);
				}
				   
			   });
		   
		   btnZoomIn.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 1.2, duration);
				}
				   
			   });
		   
		   btnZoomOut.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 0.8, duration);
				}
				   
			   });
		   
		   btnZoomToFit.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
					DisplayLib.fitViewToBounds(display,display.getVisualization().getBounds(Visualization.ALL_ITEMS), duration); 
				}
				   
			   });
		   
	}
	
		
	public void dispose() {
		super.dispose();
	}
	
	
	
	private void checkboxClick(Button check)
	{
		if(check.getSelection())
		{
			if(!activeFilters.contains(check.getText()))
			{
				activeFilters.add(check.getText());
				
				Iterator<Filter> filterIterator = knownFilters.iterator();
				while (filterIterator.hasNext())
				{
					Filter knownFilter = filterIterator.next();
					if(knownFilter.getName().equals(check.getText()))
					{
						filteredGraph = knownFilter.filter(filteredGraph).assemble();
					}
				}
				
			}
		}
		else
		{
			if(activeFilters.contains(check.getText()))
			{
				activeFilters.remove(check.getText());
				filteredGraph = (Graph) graph.copy();
				Iterator<Filter> filterIterator = knownFilters.iterator();
				while (filterIterator.hasNext())
				{
					Filter knownFilter = filterIterator.next();
					if(activeFilters.contains(knownFilter.getName()))
					{
						filteredGraph = knownFilter.filter(filteredGraph).assemble();
					}
				}
			}
		}
	}
	
	
		
	private void btnBrowseClick()
	{
		new File(BarrioConstants.JUNG_GRAPH_FILE).delete();
		new File(BarrioConstants.PREFUSE_GRAPH_FILE).delete();
		
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "ODEM Files","XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.odem", "*.xml", "*.*" });
		dlg.setFilterPath(" ");
	    String filename = dlg.open();
	    shell.close();
	    
	    List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		reader.read(filename);
  	  	
  	  	graph = new GraphMLFile().load(BarrioConstants.JUNG_GRAPH_FILE);
  	  	
  	  	OutputGenrator.initGraph = graph;
		OutputGenrator.generateProjectDescription(OutputUI.treeProject);
		OutputUI.treeProject.update();
		
		filteredGraph = (Graph) graph.copy();
		
		updateDisplay(filteredGraph);
    }
	
	
	
	private void sliderMove(Label label, int value)
	{
		label.setText("Separation level = "+ value);
		separationLevel = value;
	}
	
	
	
	private void btnRefreshClick(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters) 
	{
		if(filteredGraph!=null)
		{
			Graph clusteredGraph = (Graph) filteredGraph.copy();
			cluster(clusteredGraph);
			System.out.println("[InputUI]: clustered edges = "+clusteredGraph.getEdges().size());
			OutputGenrator.clusteredGraph = clusteredGraph;
			OutputGenrator.generatePackagesWithMultipleClusters(OutputUI.treePwMC);
			OutputUI.treePwMC.update();
			OutputGenrator.generateClustersWithMuiltiplePackages(OutputUI.treeCwMP);
			OutputUI.treeCwMP.update();
			
			List<String[]> list = new ArrayList<String[]>();
			OutputGenrator.generateListRemovedEdges(list, removedEdges);
			OutputUI.updateTable(list);
			
			updateDisplay(clusteredGraph);
		}
	}
	
	
	private void btnExportClick()
	{
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		e.export(graph, filteredGraph, separationLevel, activeFilters, removedEdges, OutputUI.treePwMC, OutputUI.treeCwMP);
	}
	
	
	
	@SuppressWarnings("deprecation")
	private void cluster(Graph g)
	{
		Clusterer c = KnownClusterer.all().get(0);
		c.cluster(g, separationLevel);
		removedEdges = c.getEdgesRemoved();
		removeEdges(g);
	}


	private void removeEdges(Graph g) 
	{
		Iterator<Edge> iter = removedEdges.iterator();
		while(iter.hasNext())
		{
			Edge e = iter.next();
			g.removeEdge(e);
		}
	}
	
	
	private void updateDisplay(Graph jungGraph)
	{
		JungPrefuseBridge bridge = new JungPrefuseBridge();
		DisplayBuilder disBuilder = new DisplayBuilder();
		Display dis = disBuilder.getDisplay(bridge.convert(jungGraph));
		dis.setLayout(new BorderLayout());
		
		OutputUI.panelGraph.removeAll();
		OutputUI.checkboxInit();
		OutputUI.panelGraph.add(dis, 0);
		OutputUI.panelGraph.doLayout();
		OutputUI.panelGraph.repaint();
	}
}