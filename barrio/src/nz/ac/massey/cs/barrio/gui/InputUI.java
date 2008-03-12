package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.filters.NodeFilter;
import nz.ac.massey.cs.barrio.graphconverter.JungPrefuseBridge;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.outputs.OutputGenrator;
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.util.display.DisplayLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

public class InputUI extends Composite{
	
	private Graph initGraph = null;
	private Graph finalGraph = null;
	private List<Edge> removedEdges = null;
	
	private Button btnBrowse;
	private Button btnRefresh;
	private Button btnExport;

	private List<String> activeFilters = new ArrayList<String>();
	private List<String> previousFilters = new ArrayList<String>();
	private List<Filter> knownFilters = new ArrayList<Filter>();
	private int separationLevel = 0;
	private int lastSeparationLevel = 0;
	protected static Composite comp;
	private Button checkContainers;
	private Button checkPackages;
	private Button checkDependencyCluster;
	private final Button checkRemovedEdges;
	
	public InputUI(Composite parent, int style) {
		   super(parent, SWT.NONE);
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
		   comp.setBounds(15, 500, 150, 90);
		   comp.setVisible(false);
		   
		   int y = 100;
		   
		   final Button btnUp = new Button(comp, SWT.ARROW|SWT.UP);
		   btnUp.setToolTipText("Pan up");
		   btnUp.setBounds(50,y,20,20);
		   
		   Button btnLeft = new Button(comp, SWT.ARROW|SWT.LEFT);
		   btnLeft.setToolTipText("Pan left");
		   btnLeft.setBounds(0,y+25,20,20);
		   
		   Button btnZoomOut = new Button(comp, SWT.PUSH);
		   btnZoomOut.setToolTipText("Zoom out");
		   btnZoomOut.setBounds(25,y+25,20,20);
		   btnZoomOut.setText("-");
		   
		   Button btnZoomToFit = new Button(comp, SWT.PUSH);
		   btnZoomToFit.setToolTipText("Zoom to fit screen");
		   btnZoomToFit.setBounds(50,y+25,20,20);
		   btnZoomToFit.setText("=");
		   
		   Button btnZoomIn = new Button(comp, SWT.PUSH);
		   btnZoomIn.setToolTipText("Zoom in");
		   btnZoomIn.setBounds(75,y+25,20,20);
		   btnZoomIn.setText("+");
		   
		   Button btnRight = new Button(comp, SWT.ARROW|SWT.RIGHT);
		   btnRight.setToolTipText("Pan right");
		   btnRight.setBounds(100,y+25,20,20);
		   
		   Button btnDown = new Button(comp, SWT.ARROW|SWT.DOWN);
		   btnDown.setToolTipText("Pan down");
		   btnDown.setBounds(50,y+50,20,20);
		   
		   checkContainers = new Button(comp, SWT.CHECK);
		   checkContainers.setText("View Containers");
		   checkContainers.setBounds(0, y+80, 100, 20);
		   
		   checkPackages = new Button(comp, SWT.CHECK);
		   checkPackages.setText("View Packages");
		   checkPackages.setBounds(0, y+100, 100, 20);
		   
		   checkDependencyCluster = new Button(comp, SWT.CHECK);
		   checkDependencyCluster.setText("View Dependency Clusters");
		   checkDependencyCluster.setBounds(0, y+120, 150, 20);
		   
		   checkRemovedEdges = new Button(comp, SWT.CHECK);
		   checkRemovedEdges.setText("View Removed Edges");
		   checkRemovedEdges.setBounds(0, y+140, 150, 20);
		   
		   //----------------------------------------------------------------
		   
		   
		   
		   //Events
		   btnBrowse.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  btnBrowseClick();
			    	  //btnRefreshClick(nodeFilters, edgeFilters);
			      }  
		   });
		   
		   slider.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  sliderMove(lblSeparation, slider.getSelection());
			      }  
		   });
		   
		   btnRefresh.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  btnRefreshClick(nodeFilters, edgeFilters);
			      }  
		   });
		   
		   btnExport.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  btnExportClick();
			      }  
		   });

		   final double panValue = 100;
		   final long duration = 1000;
		   btnUp.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}
	
				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0, 0-panValue, duration);
				}
		   });
		   
		   btnDown.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0, panValue, duration);
				}
			});
		   
		   btnLeft.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(0-panValue, 0, duration);
				}
			});
		   
		   btnRight.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					((Display)OutputUI.panelGraph.getComponent(0)).animatePan(panValue, 0, duration);
				}
			});
		   
		   btnZoomIn.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 1.2, duration);
				}
			});
		   
		   btnZoomOut.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
					display.animateZoom(new Point(display.getWidth()/2, display.getHeight()/2), 0.8, duration);
				}
			});
		   
		   btnZoomToFit.addSelectionListener(new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					zoomToFitDisplay(duration); 
				}
			});
		   
		   checkDependencyCluster.addSelectionListener(new SelectionListener(){
			   public void widgetDefaultSelected(SelectionEvent e) {}

			   public void widgetSelected(SelectionEvent e) {
				   updateDependencyClusterAggregates();
			   }
		   });
		   
		   checkPackages.addSelectionListener(new SelectionListener(){
			   public void widgetDefaultSelected(SelectionEvent e) {}

			   public void widgetSelected(SelectionEvent e) {
				   updatePackageAggregates();
			   }
		   });
		   
		   checkContainers.addSelectionListener(new SelectionListener(){
			   public void widgetDefaultSelected(SelectionEvent e) {}

			   public void widgetSelected(SelectionEvent e) {
				   updateConatainerAggregates();
			   }
		   });
		   
		   checkRemovedEdges.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {}
		
					public void widgetSelected(SelectionEvent e) {
						updateVisualRemovedEdges();
					}
		   });
		   
		   ubdateBtnRefreshEnabled();
	}
	
		
	public void dispose() {
		super.dispose();
	}
	
	
	
	
	
	
	//User Interface events==============================================================
	private void checkboxClick(Button check)
	{
		if(check.getSelection()) activeFilters.add(check.getText());
		else activeFilters.remove(check.getText());
		
		ubdateBtnRefreshEnabled();
	}
	
	private void ubdateBtnRefreshEnabled()
	{
		if(initGraph!=null)
		{
			if(previousFilters.equals(activeFilters) && lastSeparationLevel==separationLevel) 
				btnRefresh.setEnabled(false);
			else btnRefresh.setEnabled(true);
		}else btnRefresh.setEnabled(false);
	}
	
		
	private void btnBrowseClick()
	{
		new File("barrioPlugin/jGraph.xml").delete();
		new File("barrioPlugin/pGraph.xml").delete();
		
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "ODEM Files","XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.odem", "*.xml", "*.*" });
	    String filename = dlg.open();
	    shell.close();
	    
	    List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		reader.read(filename);
  	  	
  	  	initGraph = new GraphMLFile().load("barrioPlugin/jGraph.xml");
  	  			
		processGraph();
	}
	
	
	private void btnRefreshClick(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters) 
	{
		processGraph();
		ubdateBtnRefreshEnabled();
	}
	
	
	private void sliderMove(Label label, int value)
	{
		label.setText("Separation level = "+ value);
		separationLevel = value;
		ubdateBtnRefreshEnabled();
	}
	
	private void btnExportClick()
	{
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		e.export(initGraph, finalGraph, separationLevel, activeFilters, removedEdges, OutputUI.treePwMC, OutputUI.treeCwMP);
	}
	//User Interface events end==============================================================
	
	
	
	
	
	
	
	//graph processing methods=================================================
	private void processGraph()
	{
		finalGraph = (Graph) initGraph.copy();
		filterGraph();
		clusterGraph();
		
	}
	
	private void filterGraph()
	{
		for(Filter filter:knownFilters)
		{
			Filter f = filter;
			if(activeFilters.contains(f.getName()))
			{
				finalGraph = f.filter(finalGraph).assemble();
			}
		}
	}
	
	
	private void clusterGraph()
	{
		final Clusterer c = KnownClusterer.all().get(0);
		c.cluster(finalGraph, separationLevel);
		
		Job job = c.getJob();
		job.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {}

			public void awake(IJobChangeEvent event) {}

			public void done(IJobChangeEvent event) {
				if(removedEdges!=null) for(Edge e:removedEdges)
				{
					e.setUserDatum("relationship.state", "removed", UserData.SHARED);
					removedEdges = c.getEdgesRemoved();
				}
				updateOutputs(false);
			}

			public void running(IJobChangeEvent event) {}

			public void scheduled(IJobChangeEvent event) {}

			public void sleeping(IJobChangeEvent event) {}
			
		});
		
	}
	//graph processing methods end=================================================
	
	
	
	
	
	
	//Update outputs methods ============================================
	private void updateOutputs(boolean isInit)
	{
		if(isInit)
		{
			OutputGenrator.initGraph = initGraph;
			OutputGenrator.generateProjectDescription(OutputUI.treeProject);
			OutputUI.treeProject.update();
		}
		
		OutputGenrator.clusteredGraph = finalGraph;
		OutputGenrator.generatePackagesWithMultipleClusters(OutputUI.treePwMC);
		OutputUI.treePwMC.update();
		OutputGenrator.generateClustersWithMuiltiplePackages(OutputUI.treeCwMP);
		OutputUI.treeCwMP.update();
		
		List<String[]> list = new ArrayList<String[]>();
		OutputGenrator.generateListRemovedEdges(list, removedEdges);
		OutputUI.updateTable(list);
		
		previousFilters = new ArrayList<String>(activeFilters);
		lastSeparationLevel = separationLevel;
		
		updateDisplay();
	}
	
	
	
	private void updateDisplay()
	{
		System.out.println("[InputUI]: update display called");
		JungPrefuseBridge bridge = new JungPrefuseBridge();
		DisplayBuilder disBuilder = new DisplayBuilder();
		Display dis = disBuilder.getDisplay(bridge.convert(finalGraph));
		dis.setLayout(new BorderLayout());
		
		OutputUI.panelGraph.removeAll();
		OutputUI.panelGraph.add(dis, 0);
		OutputUI.panelGraph.doLayout();
		OutputUI.panelGraph.repaint();
		
		updateVisualElements();
//		zoomToFitDisplay(10000);
	}
	
	private void updateVisualElements()
	{
		updateConatainerAggregates();
		updatePackageAggregates();
		updateDependencyClusterAggregates();
		updateVisualRemovedEdges();
	}
	
	@SuppressWarnings("unchecked")
	private void updateConatainerAggregates()
	{
		boolean viewContainers = checkContainers.getSelection();
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{
			Display dis = (Display) OutputUI.panelGraph.getComponent(0);
			Iterator<VisualItem> i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("jar") && viewContainers) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("jar") && !viewContainers) ai.setVisible(false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updatePackageAggregates()
	{
		boolean viewPackages = checkPackages.getSelection();
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{
			Display dis = (Display) OutputUI.panelGraph.getComponent(0);
			Iterator<VisualItem> i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("package") && viewPackages) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("package") && !viewPackages) ai.setVisible(false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateDependencyClusterAggregates()
	{
		boolean viewClusters = checkDependencyCluster.getSelection();
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{
			Display dis = (Display) OutputUI.panelGraph.getComponent(0);
			Iterator<VisualItem> i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("cluster") && viewClusters) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("cluster") && !viewClusters) ai.setVisible(false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateVisualRemovedEdges()
	{
		boolean viewEdges = checkRemovedEdges.getSelection();
		if(OutputUI.panelGraph.getComponent(0)!=null && OutputUI.panelGraph.getComponent(0) instanceof Display)
		{			
			Display display = (Display) OutputUI.panelGraph.getComponent(0);
			Iterator<VisualItem> edgeIterator = display.getVisualization().getVisualGroup("graph.edges").tuples();
			while(edgeIterator.hasNext())
			{
				VisualItem edge = edgeIterator.next();
				if(!viewEdges && edge.getString("relationship.state").equals("removed")) edge.setVisible(false);
				else edge.setVisible(true);
			}
			
		}
	}
	
	private void zoomToFitDisplay(long duration)
	{
		Display display = (Display)OutputUI.panelGraph.getComponent(0);
		DisplayLib.fitViewToBounds(display,display.getVisualization().getBounds(Visualization.ALL_ITEMS), duration);
	}
	//Update outputs methods end ============================================
}
