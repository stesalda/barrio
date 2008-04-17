package nz.ac.massey.cs.barrio.gui;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.filters.NodeFilter;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
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
	protected static Composite graphControlsComposite;
	private Button checkContainers;
	private Button checkPackages;
	private Button checkDependencyCluster;
	private final Button checkRemovedEdges;
	
	private GraphProcessingJob job;
	org.eclipse.swt.widgets.Display display;
	
	public InputUI(Composite parent, int style) {
		   super(parent, SWT.NONE);
		   this.setLayout(new FillLayout());
		   
		   ScrolledComposite sc = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		   sc.setLayout(new GridLayout());
		   
		   
		   Composite mainComposite = new Composite(sc, SWT.BORDER);
		   mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		   mainComposite.setLayout(new GridLayout());
		   

		   GridData horizontalFillData = new GridData(GridData.FILL_HORIZONTAL);
		   
		   sc.setContent(mainComposite);
		   sc.setExpandHorizontal(true);
		   sc.setExpandVertical(true);
		   
		   
		   Composite topComposite = new Composite(mainComposite, SWT.BORDER);
		   topComposite.setLayout(new GridLayout());
		   
		   Label lblXML = new Label(topComposite, SWT.NONE);
		   lblXML.setText("Browse for input file");
		   btnBrowse = new Button(topComposite, SWT.PUSH | SWT.CENTER);
		   btnBrowse.setText("Browse"); 
		   
		   Label separator1 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator1.setLayoutData(horizontalFillData);
		   //-----------------------------------------------------------
		   
		   Label lblFilters = new Label(topComposite, SWT.BORDER);
		   lblFilters.setText("Filter out:");
		   
		   Label lblNodes = new Label(topComposite, SWT.BORDER);
		   lblNodes.setText("Nodes (classes)");
		   
		   final List<NodeFilter> nodeFilters = KnownNodeFilters.all();
		   Iterator<NodeFilter> iter = nodeFilters.iterator();
		   while (iter.hasNext())
		   {
			   NodeFilter nf = iter.next();
			   knownFilters.add(nf);
			   String label = nf.getName();
			   
			   final Button checkboxNode = new Button(topComposite, SWT.CHECK|SWT.BORDER);
			   checkboxNode.setText(label);
			   checkboxNode.addSelectionListener(new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {}

					public void widgetSelected(SelectionEvent e) {
						checkboxClick(checkboxNode);
					}
			   });
		   }
		   
		   Label lblEdges = new Label(topComposite, SWT.BORDER);
		   lblEdges.setText("Edges (relationships)");
		   
		   final List<EdgeFilter> edgeFilters = KnownEdgeFilters.all();
		   Iterator<EdgeFilter> edgeIter = edgeFilters.iterator();
		   while (edgeIter.hasNext())
		   {
			   EdgeFilter ef = edgeIter.next();;
			   knownFilters.add(ef);
			   String edgeFilterLabel = ef.getName();
			   
			   final Button checkboxEdge = new Button(topComposite, SWT.CHECK|SWT.BORDER);
			   checkboxEdge.setText(edgeFilterLabel); 
			   checkboxEdge.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					checkboxClick(checkboxEdge);
				}
				   
			   });
		   }
		   
		   Label separator2 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator2.setLayoutData(horizontalFillData);
		   //-------------------------------------------------------------
		   
		   final Label lblSeparation = new Label(topComposite, SWT.BORDER);
		   lblSeparation.setText("Separation level = 0");
		   
		   final Scale slider = new Scale(topComposite, SWT.HORIZONTAL);
		   slider.setMinimum(0);
		   slider.setMaximum(500);
		   slider.setIncrement(1);
		   slider.setPageIncrement(1);
		   slider.setSelection(0);
		   slider.setLayoutData(horizontalFillData);
		   
		   Label separator3 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator3.setLayoutData(horizontalFillData);
		   //--------------------------------------------------------------
		   
		   btnRefresh = new Button(topComposite, SWT.PUSH | SWT.CENTER);
		   btnRefresh.setText("Refresh");
		   GridData refreshData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   
		   
		   
		   btnExport = new Button(topComposite, SWT.PUSH | SWT.CENTER);
		   btnExport.setText("Export Results");
		   GridData exportData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   
		   
		   btnRefresh.setLayoutData(refreshData);
		   btnExport.setLayoutData(exportData);
		   
		   
		   //----------------------------------------------------------------
		   
		   Composite space = new Composite(mainComposite, SWT.BORDER);
		   space.setLayout(new GridLayout());
		   new Label(space, SWT.NULL);
		   
		   
		   //visual display controls ----------------------------------------
		   GridLayout graphControlsLayout = new GridLayout();
		   
		   graphControlsComposite = new Composite(mainComposite, SWT.BORDER);
		   graphControlsComposite.setLayout(graphControlsLayout);
		   graphControlsComposite.setVisible(false);
		   
		   Composite navigationComposite = new Composite(graphControlsComposite, SWT.BORDER);
		   navigationComposite.setLayout(new GridLayout(5,true));
		   

		   
		   
		   int y = 100;

		   new Label(navigationComposite, SWT.NULL);
		   new Label(navigationComposite, SWT.NULL);
		   
		   final Button btnUp = new Button(navigationComposite, SWT.PUSH);
		   btnUp.setImage(new Image(display, this.getClass().getResourceAsStream("icons/arrowUp.png")));
		   btnUp.setToolTipText("Pan up");

		   new Label(navigationComposite, SWT.NULL);
		   new Label(navigationComposite, SWT.NULL);
		   
		   Button btnLeft = new Button(navigationComposite, SWT.PUSH);
		   btnLeft.setImage(new Image(display, this.getClass().getResourceAsStream("icons/arrowLeft.png")));
		   btnLeft.setToolTipText("Pan left");
		   
		   Button btnZoomOut = new Button(navigationComposite, SWT.PUSH);
		   btnZoomOut.setImage(new Image(display, this.getClass().getResourceAsStream("icons/zOut.png")));
		   btnZoomOut.setToolTipText("Zoom out");
		   
		   Button btnZoomToFit = new Button(navigationComposite, SWT.PUSH);
		   btnZoomToFit.setImage(new Image(display, this.getClass().getResourceAsStream("icons/zFit.png")));
		   btnZoomToFit.setToolTipText("Zoom to fit screen");
		   
		   Button btnZoomIn = new Button(navigationComposite, SWT.PUSH);
		   btnZoomIn.setImage(new Image(display, this.getClass().getResourceAsStream("icons/zIn.png")));
		   btnZoomIn.setToolTipText("Zoom in");
		   
		   Button btnRight = new Button(navigationComposite, SWT.PUSH);
		   btnRight.setImage(new Image(display, this.getClass().getResourceAsStream("icons/arrowRight.png")));
		   btnRight.setToolTipText("Pan right");		   

		   new Label(navigationComposite, SWT.NULL);
		   new Label(navigationComposite, SWT.NULL);
		   
		   Button btnDown = new Button(navigationComposite, SWT.PUSH);
		   btnDown.setImage(new Image(display, this.getClass().getResourceAsStream("icons/arrowDown.png")));
		   btnDown.setToolTipText("Pan down");		   

		   new Label(navigationComposite, SWT.NULL);
		   new Label(navigationComposite, SWT.NULL);
		   
		   checkContainers = new Button(graphControlsComposite, SWT.CHECK|SWT.BORDER);
		   checkContainers.setText("View Containers");
		   
		   checkPackages = new Button(graphControlsComposite, SWT.CHECK|SWT.BORDER);
		   checkPackages.setText("View Packages");
		   
		   checkDependencyCluster = new Button(graphControlsComposite, SWT.CHECK|SWT.BORDER);
		   checkDependencyCluster.setText("View Dependency Clusters");
		   
		   checkRemovedEdges = new Button(graphControlsComposite, SWT.CHECK|SWT.BORDER);
		   checkRemovedEdges.setText("View Removed Edges");
		   
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
					Display display = (Display)OutputUI.panelGraph.getComponent(0);
	                DisplayLib.fitViewToBounds(display,display.getVisualization().getBounds(Visualization.ALL_ITEMS), duration); 
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
		   
		   updateBtnRefreshEnabled();
		   display = super.getDisplay();
		   

		   //mainComposite.setSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		   //sc.setMinSize(mainComposite.computeSize(mainComposite.getBounds().width, mainComposite.getBounds().height));
		   sc.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
		
	public void dispose() {
		super.dispose();
	}
	
	private String getFilePath(String path)
	{
		return this.getClass().getResource(path).getFile().toString();
	}
	
	
	
	
	//User Interface events==============================================================
	private void checkboxClick(Button check)
	{
		if(check.getSelection()) activeFilters.add(check.getText());
		else activeFilters.remove(check.getText());
		updateBtnRefreshEnabled();
	}
	
	private void updateBtnRefreshEnabled()
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
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "ODEM Files","XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.odem", "*.xml", "*.*" });
	    String filename = dlg.open();
	    shell.close();
	    
	    job = new GraphProcessingJob(filename, initGraph, finalGraph);
	    job.setUser(true);
	    runJob(true);
	}
	
	
	private void btnRefreshClick(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters) 
	{
		job.setCanceled(false);
		runJob(false);
	}
	
	
	private void runJob(boolean isInit)
	{
		job.setInit(isInit);
		job.setFilters(activeFilters);
		job.setSeparation(separationLevel);
	    job.setViewContainers(checkContainers.getSelection()); 
	    job.setViewPackages(checkPackages.getSelection());
	    job.setViewClusters(checkDependencyCluster.getSelection()); 
	    job.setViewEdges(checkRemovedEdges.getSelection());
	    
  	  	job.schedule();
  	  	job.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void awake(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void done(IJobChangeEvent event) {
				display.asyncExec(new Runnable(){

					public void run() {
						updateElements();
						
					}
				});
				
			}

			public void running(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void scheduled(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void sleeping(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
  	  		
  	  	});
	}
	
	
	
	
	private void updateElements() {
		if(job.getResult().equals(Status.OK_STATUS))
		{
			initGraph = job.getInitGraph();
			finalGraph = job.getFinalGraph();
			removedEdges = job.getRemovedEdges();
			
			previousFilters.clear();
			previousFilters.addAll(activeFilters);
			lastSeparationLevel = separationLevel;

			updateBtnRefreshEnabled();
		}
	}


	private void sliderMove(Label label, int value)
	{
		label.setText("Separation level = "+ value);
		separationLevel = value;
		updateBtnRefreshEnabled();
	}
	
	private void btnExportClick()
	{
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		e.export(initGraph, finalGraph, separationLevel, activeFilters, removedEdges, OutputUI.treePwMC, OutputUI.treeCwMP);
	}
	//User Interface events end==============================================================
	
	
	
	
	
	
	
	//Update visualisation methods ============================================
	
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
        //System.out.println("[InputUI]: view containers = " + viewContainers);
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
        //System.out.println("[InputUI]: view packages = " + viewPackages);
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
        //System.out.println("[InputUI]: view clusters = " + viewClusters);
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
        //System.out.println("[InputUI]: view edges = " + viewEdges);
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

	
	//Update visualisation methods end ============================================
}
