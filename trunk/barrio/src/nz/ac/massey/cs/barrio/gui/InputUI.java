package nz.ac.massey.cs.barrio.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.filters.EdgeFilter;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.filters.NodeFilter;
import nz.ac.massey.cs.barrio.jobs.GraphClusteringJob;
import nz.ac.massey.cs.barrio.jobs.GraphFilteringJob;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;

public class InputUI extends Composite{
	
	private Button btnAnalyse;
	private Button btnApplyFilters;

	private List<String> doneFilters = new ArrayList<String>();
	private List<String> todoFilters = new ArrayList<String>();
	private List<Filter> knownFilters = new ArrayList<Filter>();
	private int separationLevel = 0;
	private Composite graphControlsComposite;
	private final Scale slider;
	private final Label lblSeparation;
	private Graph initGraph = null;
	private Graph filteredGraph = null;
	private Graph clusteredGraph = null;
	private List<Edge> removedEdges = null;


	protected static org.eclipse.swt.widgets.Display display;
	
	public InputUI(Composite parent, int style) {
		   super(parent, SWT.NONE);
		   this.setLayout(new FillLayout());
		   
		   ScrolledComposite sc = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		   sc.setLayout(new GridLayout());
		   
		   
		   Composite mainComposite = new Composite(sc, SWT.NONE);
		   mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		   mainComposite.setLayout(new GridLayout());
		   

		   GridData horizontalFillData = new GridData(GridData.FILL_HORIZONTAL);
		   
		   sc.setContent(mainComposite);
		   sc.setExpandHorizontal(true);
		   sc.setExpandVertical(true);
		   
		   
		   Composite topComposite = new Composite(mainComposite, SWT.NONE);
		   topComposite.setLayout(new GridLayout());
		   
		   Label lblFilters = new Label(topComposite, SWT.NONE);
		   lblFilters.setText("Filter out:");
		   
		   Label lblNodes = new Label(topComposite, SWT.NONE);
		   lblNodes.setText("Nodes (classes)");
		   
		   final List<NodeFilter> nodeFilters = KnownNodeFilters.all();
		   Iterator<NodeFilter> iter = nodeFilters.iterator();
		   while (iter.hasNext())
		   {
			   NodeFilter nf = iter.next();
			   knownFilters.add(nf);
			   String label = nf.getName();
			   
			   final Button checkboxNode = new Button(topComposite, SWT.CHECK);
			   checkboxNode.setText(label);
			   checkboxNode.addSelectionListener(new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {}

					public void widgetSelected(SelectionEvent e) {
						filterCheckboxClick(checkboxNode);
					}
			   });
		   }
		   
		   Label lblEdges = new Label(topComposite, SWT.NONE);
		   lblEdges.setText("Edges (relationships)");
		   
		   final List<EdgeFilter> edgeFilters = KnownEdgeFilters.all();
		   Iterator<EdgeFilter> edgeIter = edgeFilters.iterator();
		   while (edgeIter.hasNext())
		   {
			   EdgeFilter ef = edgeIter.next();;
			   knownFilters.add(ef);
			   String edgeFilterLabel = ef.getName();
			   
			   final Button checkboxEdge = new Button(topComposite, SWT.CHECK);
			   checkboxEdge.setText(edgeFilterLabel); 
			   checkboxEdge.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					filterCheckboxClick(checkboxEdge);
				}
				   
			   });
		   }
		   
		   btnApplyFilters = new Button(topComposite, SWT.PUSH);
		   btnApplyFilters.setText("Apply Filters");
		   btnApplyFilters.setEnabled(false);
		   Label separator2 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator2.setLayoutData(horizontalFillData);
		   //-------------------------------------------------------------
		   
		   lblSeparation = new Label(topComposite, SWT.NONE);
		   lblSeparation.setText("Separation level = 0          ");
		   
		   slider = new Scale(topComposite, SWT.HORIZONTAL);
		   slider.setMinimum(0);
		   slider.setMaximum(1);
		   slider.setIncrement(1);
		   slider.setPageIncrement(1);
		   slider.setSelection(0);
		   
		   Label separator3 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator3.setLayoutData(horizontalFillData);
		   //--------------------------------------------------------------
		   
		   btnAnalyse = new Button(topComposite, SWT.PUSH | SWT.CENTER);
		   btnAnalyse.setText("Analyse");
		   GridData refreshData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   btnAnalyse.setLayoutData(refreshData);
		   
		   
		   //----------------------------------------------------------------
		   
		   Composite space = new Composite(mainComposite, SWT.NONE);
		   space.setLayout(new GridLayout());
		   new Label(space, SWT.NULL);
		   
		   
		   //visual display controls ----------------------------------------
		   GridLayout graphControlsLayout = new GridLayout();
		   
		   graphControlsComposite = new Composite(mainComposite, SWT.NONE);
		   graphControlsComposite.setLayout(graphControlsLayout);
		   graphControlsComposite.setVisible(false);
		   
		   Composite navigationComposite = new Composite(graphControlsComposite, SWT.NONE);
		   navigationComposite.setLayout(new GridLayout(5,true));
		   
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
		   
		   final Button checkContainers = new Button(graphControlsComposite, SWT.CHECK|SWT.NONE);
		   checkContainers.setText("View Containers");
		   
		   final Button checkPackages = new Button(graphControlsComposite, SWT.CHECK|SWT.NONE);
		   checkPackages.setText("View Packages");
		   
		   final Button checkDependencyCluster = new Button(graphControlsComposite, SWT.CHECK|SWT.NONE);
		   checkDependencyCluster.setText("View Dependency Clusters");
		   
		   final Button checkRemovedEdges = new Button(graphControlsComposite, SWT.CHECK|SWT.NONE);
		   checkRemovedEdges.setText("View Removed Edges");
		   
		   //----------------------------------------------------------------
		   
		   
		   //Events
		   slider.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  sliderMove();
			      }  
		   });
		   
		   btnApplyFilters.addSelectionListener(new SelectionListener() {
			   public void widgetDefaultSelected(SelectionEvent e) {}
			   
			   public void widgetSelected(SelectionEvent e) {
				   btnApplyFiltersClick();
			   }
			   
		   });
		   
		   btnAnalyse.addSelectionListener(new SelectionListener() {
			      public void widgetDefaultSelected(SelectionEvent e) {}

			      public void widgetSelected(SelectionEvent e) {
			    	  btnAnalyseClick(nodeFilters, edgeFilters);
			      }  
		   });
		   sc.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
		
	public void dispose() {
		super.dispose();
	}	
	
	//User Interface events==============================================================
	private void filterCheckboxClick(Button check)
	{
		if(check.getSelection()) todoFilters.add(check.getText());
		else todoFilters.remove(check.getText());
		
		if(initGraph==null)
		{
			btnApplyFilters.setEnabled(false);
			return;
		}
		
		if(isFiltered()) btnApplyFilters.setEnabled(false);
		else btnApplyFilters.setEnabled(true);
	}
	
	private boolean isFiltered()
	{
		if(todoFilters.size()!=doneFilters.size()) return false;
		for(String filter:doneFilters)
		{
			if(!todoFilters.contains(filter)) return false;
		}
		return true;
	}
	
	private void btnApplyFiltersClick()
	{
		final OutputUI output = new GuiGetter().getOutputUI();
		final GraphFilteringJob filteringJob = new GraphFilteringJob(initGraph, todoFilters);
		filteringJob.setUser(true);
		filteringJob.addJobChangeListener(new IJobChangeListener(){

			public void aboutToRun(IJobChangeEvent event) {}
			public void awake(IJobChangeEvent event) {}
			public void running(IJobChangeEvent event) {}
			public void scheduled(IJobChangeEvent event) {}
			public void sleeping(IJobChangeEvent event) {}
			
			public void done(IJobChangeEvent event) 
			{
				filteredGraph = filteringJob.getFilteredGraph();
				output.getDisplay().syncExec(new Runnable(){

					@Override
					public void run() {
						output.updateOutputs(filteredGraph);
						doneFilters = new ArrayList<String>();
						for(String filter:todoFilters) doneFilters.add(filter);
						btnApplyFilters.setEnabled(false);
					}									
				});
			}
				
		});
		filteringJob.schedule();
				
		
	}
	
	
	private void btnAnalyseClick(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters) 
	{
		if(initGraph!=null)
		{
			System.out.println("[InputUI]: initGraph file = " +initGraph.getUserDatum("file"));
			final OutputUI output = new GuiGetter().getOutputUI();
			final GraphClusteringJob clusteringJob = new GraphClusteringJob(filteredGraph);
			clusteringJob.setUser(true);
			clusteringJob.addJobChangeListener(new IJobChangeListener(){
				
				public void aboutToRun(IJobChangeEvent event) {}
				public void awake(IJobChangeEvent event) {}
				public void running(IJobChangeEvent event) {}
				public void scheduled(IJobChangeEvent event) {}
				public void sleeping(IJobChangeEvent event) {}
				
				public void done(IJobChangeEvent event) 
				{
					clusteredGraph = clusteringJob.getClusteredGraph();
					if(clusteredGraph==null) return;
					removedEdges = clusteringJob.getRemovedEdges();
					separationLevel = clusteringJob.getSeparationValue();
					
					output.getDisplay().syncExec(new Runnable(){

						@Override
						public void run() {
							sliderMove();
						}									
					});
					
				}
			});
			clusteringJob.schedule();
		}
	}


	private void sliderMove()
	{
		
		final OutputUI output = new GuiGetter().getOutputUI();
		if(slider.getSelection()==0){
			lblSeparation.setText("Separation level = 0");
			output.updateOutputs(filteredGraph);
			output.updateTableRemovedEdges(null);
		}
		if(slider.getSelection()==1){
			lblSeparation.setText("Separation level = "+ separationLevel);
			output.updateOutputs(clusteredGraph);
			output.updateTableRemovedEdges(removedEdges);
		}
	}
	//User Interface events end==============================================================
	

	
	public void setInitGraph(Graph initGraph) {
		this.initGraph = initGraph;
	}

	public int getSeparationLevel() {
		return separationLevel;
	}


	public Graph getInitGraph() {
		return initGraph;
	}


	public Graph getFilteredGraph() {
		return filteredGraph;
	}


	public Graph getClusteredGraph() {
		return clusteredGraph;
	}
	
	public void setRemovedEdges(List<Edge> removedEdges) {
		this.removedEdges = removedEdges;
	}

	public void setFilteredGraph(Graph filteredGraph) {
		this.filteredGraph = filteredGraph;
	}

	public void setClusteredGraph(Graph clusteredGraph) {
		this.clusteredGraph = clusteredGraph;
	}
	
	public List<String> getActiveFilters() {
		return todoFilters;
	}	
}