package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.graphconverter.JungPrefuseBridge;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import prefuse.Display;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

public class GraphProcessingJob extends Job {

	private Object input;
	private Graph initGraph;
	private Graph finalGraph;

	
	private boolean isInit;
	private List<String> filters;
	private int separation;
	private List<Edge> removedEdges;
	private boolean canceled;
	
	private boolean viewContainers;
	private boolean viewPackages;
	private boolean viewClusters;
	private boolean viewEdges;
	
	private OutputGenerator og;
	
	public GraphProcessingJob(Object input, Graph initGraph, Graph finalGraph) {
		super("Processing graph");
		this.input = input;
		
		this.initGraph = initGraph;
		this.finalGraph = finalGraph;
		
		filters = null;
		separation = 0;		
		canceled = false;
		removedEdges = new ArrayList<Edge>();
	}
	
	
	
	@Override
	protected void canceling() {
//		System.out.println("[JOB]: canceling called");
		canceled = true;
		
	}
	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(input!=null)
		{
			int SCALE = 4+filters.size()+separation;
			monitor.beginTask("Processing Graph", SCALE);
			
			if(isInit) 
			{
				readInput(monitor);
			}else monitor.worked(2);
			
//			System.out.println("[Job]: graph = "+initGraph.getVertices().size()+ "  "+initGraph.getEdges().size());
			
			if(initGraph==null) return Status.CANCEL_STATUS;
			finalGraph = (Graph) initGraph.copy();
			
			filterGraph(monitor);
			clusterGraph(monitor);
			buildVisual(monitor);
			
			updateVisualElements();
			monitor.done();
			
			if(canceled) return Status.CANCEL_STATUS;
			return Status.OK_STATUS;
		}
		
		return Status.CANCEL_STATUS;
	}




	private void readInput(IProgressMonitor monitor) {
		if(canceled) return;
		
		monitor.subTask("Reading Input File");
		List<InputReader> readers = KnownInputReader.all();
		initGraph = new DirectedSparseGraph();
		boolean done = false;
		for(InputReader reader:readers)
		{
			try {
				reader.read(input, initGraph);
				done = true;
				break;
			} catch (UnknownInputException e) {
				//e.printStackTrace();
			} 
			catch (IOException e) {
				//e.printStackTrace();
				if (e instanceof java.net.ConnectException)
				{
					showConnectionErrorMessage();
					return;
				}
			}
		}
		if(!done) showUnknownInputMessage();
		monitor.worked(1);
	}
	
	private void showUnknownInputMessage()
	{
		OutputUI.display.asyncExec(new Runnable(){

			public void run() {
				Shell s = new Shell();
				MessageBox mb = new MessageBox(s, SWT.ICON_ERROR);
				mb.setText("Barrio Error: Unknown Input");
				mb.setMessage("Cannot build graph from this input type!!!\n Refer to user manual.");
				int rc = mb.open();
				if(rc==SWT.OK) s.dispose();
			}
		});
	}
	
	private void showConnectionErrorMessage()
	{
		OutputUI.display.asyncExec(new Runnable(){

			public void run() {
				Shell s = new Shell();
				MessageBox mb = new MessageBox(s, SWT.ICON_ERROR);
				mb.setText("Barrio Network Connection Error");
				mb.setMessage("Could not connect to DTD file!!!\n Check your network settings.");
				int rc = mb.open();
				if(rc==SWT.OK) s.dispose();
			}
		});
	}


	
	
	
	private void filterGraph(IProgressMonitor monitor) {
		
		List<Filter> knownFilters = new ArrayList<Filter>();
		knownFilters.addAll(KnownNodeFilters.all());
		knownFilters.addAll(KnownEdgeFilters.all());
		monitor.subTask("Filtering Graph");
		for(Filter filter:knownFilters)
		{
			Filter f = filter;
			if(filters.contains(filter.getName()))
			{
				finalGraph = f.filter(finalGraph).assemble();
			}
			monitor.worked(1);
			if(canceled) return;
		}
	}


	
	
	@SuppressWarnings("unchecked")
	private void clusterGraph(IProgressMonitor monitor) {

		String subtask = "Removing Separation Level ";
		
		Set<Edge> edges = finalGraph.getEdges();
		for(Edge edge:edges) 
			edge.setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		removedEdges.clear();
		
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		for(int i=0; i<separation; i++)
		{
			monitor.subTask(subtask+(i+1));
			clusterer.cluster(finalGraph);
			removedEdges.addAll(clusterer.getEdgesRemoved());
						
			monitor.worked(1);
			if(canceled) return;
		}	
		clusterer.nameClusters(finalGraph);
		
		for(Edge edge:removedEdges)
		{
			edge.setUserDatum("relationship.state", "removed", UserData.SHARED);
			finalGraph.addEdge(edge);
		}
	}




	private void buildVisual(IProgressMonitor monitor) {

		if(canceled) return;
		monitor.subTask("Producing Visualisation");

		updateOutputs();
		monitor.worked(1);
		
		JungPrefuseBridge bridge = new JungPrefuseBridge();
		DisplayBuilder disBuilder = new DisplayBuilder();
		Display dis = disBuilder.getDisplay(bridge.convert(finalGraph));
		dis.setLayout(new BorderLayout());
		
		OutputUI.panelGraph.removeAll();
		OutputUI.panelGraph.add(dis, 0);
		OutputUI.panelGraph.doLayout();
		OutputUI.panelGraph.repaint();
		
		monitor.worked(1);
		
	}
	
	
	
	
	private void updateOutputs()
    {
		if(canceled) return;
		if(OutputUI.display !=null && OutputUI.display instanceof org.eclipse.swt.widgets.Display) 
		{
			OutputUI.display.asyncExec(new Runnable(){

				public void run() {
					if(isInit)
					{
						og = new OutputGenerator(initGraph, finalGraph);
						og.generateProjectDescription(OutputUI.treeProject);
					}	
					
			        og.generatePackagesWithMultipleClusters(OutputUI.treePwMC);
			        og.generateClustersWithMuiltiplePackages(OutputUI.treeCwMP);
			        
			        List<String[]> list = new ArrayList<String[]>();
			        og.generateListRemovedEdges(list, removedEdges);
			        OutputUI.updateTable(list);
					
				}
			});
		}
	}
	
	
	
	
	private void updateVisualElements()
    {
		if(canceled) return;
        updateConatainerAggregates();
        updatePackageAggregates();
        updateDependencyClusterAggregates();
        updateVisualRemovedEdges();
    }
    
	
	
	
    @SuppressWarnings("unchecked")
    private void updateConatainerAggregates()
    {
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
	
	
	
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}



	public void setFilters(List<String> filters) {
		this.filters = filters;
	}



	public void setSeparation(int separation) {
		this.separation = separation;
	}



	public void setViewContainers(boolean viewContainers) {
		this.viewContainers = viewContainers;
	}



	public void setViewPackages(boolean viewPackages) {
		this.viewPackages = viewPackages;
	}



	public void setViewClusters(boolean viewClusters) {
		this.viewClusters = viewClusters;
	}



	public void setViewEdges(boolean viewEdges) {
		this.viewEdges = viewEdges;
	}



	public Graph getInitGraph() {
		return initGraph;
	}


	public Graph getFinalGraph() {
		return finalGraph;
	}


	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}



	public boolean isCanceled() {
		return canceled;
	}



	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
