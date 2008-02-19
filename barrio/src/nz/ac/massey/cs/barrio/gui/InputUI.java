package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;
import nz.ac.massey.cs.barrio.filters.KnownEdgeFilters;
import nz.ac.massey.cs.barrio.filters.KnownNodeFilters;
import nz.ac.massey.cs.barrio.filters.NodeFilter;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.outputs.OutputGenrator;
import nz.ac.massey.cs.barrio.visual.PrefuseGraphBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
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
import org.xml.sax.SAXException;

import prefuse.Display;
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
			}
		}
		else
		{
			if(activeFilters.contains(check.getText()))
			{
				activeFilters.remove(check.getText());
			}
		}
		
		Iterator<Filter> filterIterator = knownFilters.iterator();
		while (filterIterator.hasNext())
		{
			Filter filter = filterIterator.next();
			if(activeFilters.contains(filter.getName()))
				filteredGraph = filter.filter(filteredGraph).assemble();
		}
	
		btnRefreshClick(null, null);


		
//		System.out.println("[InputUI]: filters = "+activeFilters);
	}
	
	private void btnBrowseClick()
	{
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[] { "XML Files", "All Files" });
		dlg.setFilterExtensions(new String[] { "*.xml", "*.*" });
		dlg.setFilterPath(" ");
	    String filename = dlg.open();
	    shell.close();
	    List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		reader.read(filename);
  	  	
  	  	graph = new GraphMLFile().load("c:/dcaPlugin/jGraph.xml");
  	  	
  	  	OutputGenrator.initGraph = graph;
		OutputGenrator.generateProjectDescription(OutputUI.treeProject);
		OutputUI.treeProject.update();
		
		filteredGraph = (Graph) graph.copy();
    }
	
	private void sliderMove(Label label, int value)
	{
		label.setText("Separation level = "+ value);
		separationLevel = value;
	}
	
	private void btnRefreshClick(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters) {
		// TODO Auto-generated method stub
		//filter(nodeFilters, edgeFilters);
		cluster();
		
		if(filteredGraph!=null){
		OutputGenrator.clusteredGraph = filteredGraph;
		OutputGenrator.generatePackagesWithMultipleClusters(OutputUI.treePwMC);
		OutputUI.treePwMC.update();
		OutputGenrator.generateClustersWithMuiltiplePackages(OutputUI.treeCwMP);
		OutputUI.treeCwMP.update();
		
		List<String[]> list = new ArrayList<String[]>();
		OutputGenrator.generateListRemovedEdges(list, removedEdges);
		OutputUI.updateTable(list);
		
		PrefuseGraphBuilder pgb = new PrefuseGraphBuilder(filteredGraph, removedEdges);
		pgb.buildPrefuseGraph();
		Display dis = pgb.getDisplay();
		dis.setLayout(new BorderLayout());
		
		OutputUI.panelGraph.removeAll();
		OutputUI.panelGraph.add(dis, 0);
		OutputUI.panelGraph.doLayout();
		OutputUI.panelGraph.repaint();
		}
	}
	
	
	private void btnExportClick()
	{
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		e.export(graph, filteredGraph, separationLevel, activeFilters, removedEdges, OutputUI.treePwMC, OutputUI.treeCwMP);
	}
	
	private void filter(List<NodeFilter> nodeFilters, List<EdgeFilter> edgeFilters)
	{
		filteredGraph = (Graph) graph.copy();
		
		Iterator<NodeFilter> nodeFilterIterator = nodeFilters.iterator();
		while (nodeFilterIterator.hasNext())
		{
			Filter nodeFilter = nodeFilterIterator.next();
			if(activeFilters.contains(nodeFilter.getName())) {
				System.out.println("[inputUI]: filter = "+nodeFilter.getName());
				filteredGraph = nodeFilter.filter(filteredGraph).assemble();
			}
		}
		
		Iterator<EdgeFilter> edgeFilterIterator = edgeFilters.iterator();
		while (edgeFilterIterator.hasNext())
		{
			Filter edgeFilter = edgeFilterIterator.next();
			if(activeFilters.contains(edgeFilter.getName())) {
				System.out.println("[inputUI]: filter = "+edgeFilter.getName());
				filteredGraph = edgeFilter.filter(filteredGraph).assemble();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void cluster()
	{
		Clusterer c = KnownClusterer.all().get(0);
		c.cluster(filteredGraph, separationLevel);
		removedEdges = c.getEdgesRemoved();
		removeEdges();
	}


	private void removeEdges() 
	{
		Iterator<Edge> iter = removedEdges.iterator();
		while(iter.hasNext())
		{
			Edge e = iter.next();
			filteredGraph.removeEdge(e);
		}
		
	}
}
