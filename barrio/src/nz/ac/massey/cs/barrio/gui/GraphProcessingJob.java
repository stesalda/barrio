package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
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
import nz.ac.massey.cs.barrio.visual.DisplayBuilder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import prefuse.Display;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserData;

public class GraphProcessingJob extends Job {

	private String filename;	
	private List<String> filters;
	private int separation;
	private boolean isInit;

	private Graph initGraph;
	private Graph finalGraph;
	private List<Edge> removedEdges;
	

	private boolean viewContainers;
	private boolean viewPackages;
	private boolean viewClusters;
	private boolean viewEdges;
	private OutputGenerator og;
	
	public GraphProcessingJob(String filename, List<String> filters, int separation) {
		super("Processing graph");
		this.filename = filename;
		this.filters = filters;
		this.separation = separation;

		initGraph = null;
		finalGraph = null;
		removedEdges = new ArrayList<Edge>();
	}
	
	
	public void setIsInit(boolean isInit)
	{
		this.isInit = isInit;
	}

	
	public void setViewElements(boolean viewContainers, boolean viewPackages, boolean viewClusters, boolean viewEdges)
	{
		this.viewContainers = viewContainers;
		this.viewPackages = viewPackages;
		this.viewClusters = viewClusters;
		this.viewEdges = viewEdges;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(filename!=null && filename.length()>0)
		{
			int SCALE = 4+filters.size()+separation;
			monitor.beginTask("Processing Graph", SCALE);
			
			if(isInit) 
			{
				readInput(monitor);
				buildGraph(monitor);
			}else monitor.worked(2);
			
			if(initGraph==null) return Status.CANCEL_STATUS;
			finalGraph = (Graph) initGraph.copy();
			
			filterGraph(monitor);
			clusterGraph(monitor);
			buildVisual(monitor);
			
			updateVisualElements();
			monitor.done();
			return Status.OK_STATUS;
		}
		
		return Status.CANCEL_STATUS;
	}




	private void readInput(IProgressMonitor monitor) {
		
		monitor.subTask("Reading Input File");
		List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		reader.read(filename);
		monitor.worked(1);
	}
	
	
	
	
	private void buildGraph(IProgressMonitor monitor) {
		
		monitor.subTask("Building Graph");
		GraphMLFile graphML = new GraphMLFile();
		initGraph = graphML.load("barrioPlugin/jGraph.xml");
		monitor.worked(1);		
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
		}
	}


	
	
	@SuppressWarnings("unchecked")
	private void clusterGraph(IProgressMonitor monitor) {

		String subtask = "Removing Separation Level ";
		
		Set<Edge> edges = finalGraph.getEdges();
		for(Edge edge:edges) 
			edge.setUserDatum("relationship.betweenness", "null", UserData.SHARED);
		
		for(int i=0; i<separation; i++)
		{
			monitor.subTask(subtask+(i+1));
			List<Clusterer> clusterers = KnownClusterer.all();
			Clusterer clusterer = clusterers.get(0);
			clusterer.cluster(finalGraph);
			removedEdges.addAll(clusterer.getEdgesRemoved());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			monitor.worked(1);
		}	
		
		for(Edge edge:removedEdges)
		{
			edge.setUserDatum("relationship.state", "removed", UserData.SHARED);
			finalGraph.addEdge(edge);
		}
	}




	private void buildVisual(IProgressMonitor monitor) {

		monitor.subTask("Produsing Visualisation");

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
        updateConatainerAggregates();
        updatePackageAggregates();
        updateDependencyClusterAggregates();
        updateVisualRemovedEdges();
    }
    
    @SuppressWarnings("unchecked")
    private void updateConatainerAggregates()
    {
        System.out.println("[InputUI]: view containers = " + viewContainers);
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
    	System.out.println("[InputUI]: view packages = " + viewPackages);
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
        System.out.println("[InputUI]: view clusters = " + viewClusters);
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
        System.out.println("[InputUI]: view edges = " + viewEdges);
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
}
