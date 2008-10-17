package nz.ac.massey.cs.barrio.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;

public class InputUI extends Composite{	

	private List<String> doneFilters = new ArrayList<String>();
	private List<String> todoFilters = new ArrayList<String>();
	private List<Filter> knownFilters = new ArrayList<Filter>();
	private int separationLevel = 0;
	private Graph initGraph = null;
	private Graph filteredGraph = null;
	private Graph clusteredGraph = null;
	private List<Edge> removedEdges = null;
	private VisualGraphFrame vgf = null;
	private boolean isClustered = false;
	
	private Button btnAnalyse;
	private Button btnApplyFilters;
	private final Scale slider;
	private final Label lblSeparation;
	private final Button checkDisplayGraph;

	
	
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
		   slider.setEnabled(false);
		   
		   Label separator3 = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		   separator3.setLayoutData(horizontalFillData);
		   //--------------------------------------------------------------
		   Combo combo = new Combo(topComposite,SWT.DROP_DOWN);
		   for (int i = 1; i < 11; i++)combo.add(String.valueOf(i));
		   combo.add("-All-");
		   combo.select(0);
		   combo.setEnabled(false);
		   
		   btnAnalyse = new Button(topComposite, SWT.PUSH | SWT.CENTER);
		   btnAnalyse.setText("Analyse");
		   GridData refreshData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		   btnAnalyse.setLayoutData(refreshData);
		   btnAnalyse.setEnabled(false);
		   
		    //----------------------------------------------------------------
		   
		   Composite graphDisplay = new Composite(mainComposite, SWT.NONE);
		   graphDisplay.setLayout(new GridLayout());
		   checkDisplayGraph = new Button(graphDisplay, SWT.CHECK);
		   checkDisplayGraph.setText("Display Visual Graph");		   
		  
		   vgf = new VisualGraphFrame();
		   vgf.setVisible(false);
		   vgf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		   
		   
		   
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
		   
		   checkDisplayGraph.addSelectionListener(new SelectionListener(){
			   public void widgetDefaultSelected(SelectionEvent e) {}
			   
			   public void widgetSelected(SelectionEvent e) {
				   checkDisplayGraphClick();
			   }
			   
		   });
		   
		   vgf.addWindowListener(new WindowListener(){
			   public void windowActivated(WindowEvent arg0) {}
			   public void windowClosing(WindowEvent arg0) {}
			   public void windowDeactivated(WindowEvent arg0) {}
			   public void windowDeiconified(WindowEvent arg0) {}
			   public void windowIconified(WindowEvent arg0) {}
			   public void windowOpened(WindowEvent arg0) {}	
			   public void windowClosed(WindowEvent arg0) {
				   InputUI.this.getDisplay().asyncExec(new Runnable(){
//						@Override
						public void run() {
							checkDisplayGraph.setSelection(false);
						}				   
				   });				   
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
		
		setInteractiveElements();
	}
	
	private boolean isFiltered()
	{
		if(todoFilters.size()!=doneFilters.size()) return false;
		if(initGraph==null) return false;
		if(filteredGraph==null) return false;
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

//					@Override
					public void run() {
						output.updateOutputs(filteredGraph);
						if(vgf!=null) vgf.setGraph(filteredGraph);
						doneFilters = new ArrayList<String>();
						for(String filter:todoFilters) doneFilters.add(filter);
						setClustered(false);
						setInteractiveElements();
					}									
				});
			}
				
		});
		filteringJob.schedule();
	}
	
		
	public void checkDisplayGraphClick() {
		boolean selection = checkDisplayGraph.getSelection();
		if(selection)
		{
			Graph g = null;
			if(slider.getSelection()==0) g = filteredGraph;
			if(slider.getSelection()==1) g = clusteredGraph;
			
			if(g!=null) vgf.setGraph(g);
			vgf.setVisible(true);
		}
		else
		{
			vgf.setVisible(false);
		}
		
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

//						@Override
						public void run() {
							setClustered(true);
							setInteractiveElements();
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
		checkDisplayGraphClick();
	}
	//User Interface events end==============================================================
	
	public void setInteractiveElements()
	{
		if(initGraph==null)
		{
			System.out.println("[InputUI]: initG = null");
			btnApplyFilters.setEnabled(false);
			slider.setEnabled(false);
			lblSeparation.setEnabled(false);
			btnAnalyse.setEnabled(false);
		}
		else
		{
			if(isFiltered())
			{
				btnApplyFilters.setEnabled(false);
				if(isClustered)
				{
					slider.setEnabled(true);
					lblSeparation.setEnabled(true);
					btnAnalyse.setEnabled(false);
				}
				else
				{
					slider.setEnabled(false);
					lblSeparation.setEnabled(false);
					btnAnalyse.setEnabled(true);
				}
			}
			else
			{
				btnApplyFilters.setEnabled(true);
				slider.setEnabled(false);
				lblSeparation.setEnabled(false);
				btnAnalyse.setEnabled(false);
			}
		}
	}

	
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


	public boolean isClustered() {
		return isClustered;
	}


	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}


	public void setDoneFilters() {
		for(String filter:todoFilters) doneFilters.add(filter);
	}	
	
	
}