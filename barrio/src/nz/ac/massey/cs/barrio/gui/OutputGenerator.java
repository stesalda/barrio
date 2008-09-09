package nz.ac.massey.cs.barrio.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class OutputGenerator {
	
	private Graph finalGraph = null;
	private int separation;
	
	public OutputGenerator(Graph graph, int separation)
	{
		finalGraph = (Graph) graph.copy();
		this.separation = separation;

		System.out.println("[OG]: sep = "+separation+"; graph-e = "+graph.numEdges());
		Set<Edge> edges = finalGraph.getEdges();
		List<Edge> edgesToRemove = new ArrayList<Edge>();
		Iterator<Edge> edgeIter = finalGraph.getEdges().iterator();
		while(edgeIter.hasNext())
		{
			Edge e = edgeIter.next();
			String value = e.getUserDatum("relationship.separation").toString();
			int sep = finalGraph.numEdges();
			if(!value.equals("null")) sep = Integer.valueOf(value);
			if(sep <= separation) edgesToRemove.add(e);
		}
		for(Edge e:edgesToRemove)
		{
			finalGraph.removeEdge(e);
		}
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		clusterer.nameClusters(finalGraph);
		System.out.println("[OG]: edges = "+finalGraph.numEdges());
	}
	
	
	public void generateListRemovedEdges(List<Object[]> list, List<Edge> removedEdges)
	{
		int i = 1;
		list.clear();		
		if(removedEdges!=null) for(Edge e:removedEdges)
		{
			Vertex src = (Vertex) e.getEndpoints().getFirst();
			Vertex dest = (Vertex) e.getEndpoints().getSecond();
			
			Object[] data = new Object[7];

			data[0] = e.getUserDatum("id").toString();
//			System.out.println("[OG]: "+data[0]);
			data[1] = String.valueOf(i);
			
			StringBuffer sourceStr = new StringBuffer();
			sourceStr.append(src.getUserDatum("class.jar").toString());
			sourceStr.append('\n');
			sourceStr.append(src.getUserDatum("class.packageName").toString());
			sourceStr.append('\n');
			sourceStr.append(src.getUserDatum("class.name").toString());
			data[2] = sourceStr.toString();
			
			data[3] = e.getUserDatum("relationship.type").toString();
			
			StringBuffer destStr = new StringBuffer();
			destStr.append(dest.getUserDatum("class.jar").toString());
			destStr.append('\n');
			destStr.append(dest.getUserDatum("class.packageName").toString());
			destStr.append('\n');
			destStr.append(dest.getUserDatum("class.name").toString());
			data[4] = destStr.toString();
			
			data[5] = e.getUserDatum("relationship.betweenness").toString();
			list.add(data);
			data[6] = false;
//			System.out.println("[OG]: list = "+ list.size());
			i++;
		}
	}
	
	

	public void generateTableTree(Tree tree) 
	{
	    
	    TreeColumn[] columns1 = tree.getColumns();
	    for (int i = 0, n = columns1.length; i < n; i++) {
	      columns1[i].dispose();
	    }
	    tree.removeAll();

	    List<TableRowData> list = generateTableData(tree);

	    // Create the data
	    for (TableRowData data:list)
	    {
	    	addItem(data, tree);
	    }
		
	    
	    // Pack the columns
	    TreeColumn[] columns = tree.getColumns();
	    for (int i = 0, n = columns.length; i < n; i++) {
	      columns[i].pack();
	    }
	}
	
	private void addItem(TableRowData data, Tree tree)
	{
		TreeItem container = null;
		for(TreeItem item:tree.getItems())
		{
			if(item.getText().equals(data.getContainer())) 
			{
				if(!item.isDisposed()) container = item;
				break;
			}
		}
		if(container == null) container = new TreeItem(tree, SWT.NONE);
		container.setText(data.getContainer());
		
		TreeItem namespace = null;
		for(TreeItem item:container.getItems())
		{
			if(item.getText().equals(data.getNamespace())) 
			{
				namespace = item;
				break;
			}
		}
		if(namespace == null) namespace = new TreeItem(container, SWT.NONE);
		namespace.setText(data.getNamespace());
		
		TreeItem className = new TreeItem(namespace, SWT.NONE);
		className.setText(data.getClassName());
		
		for(int i=tree.getColumnCount()-1; i>-1; i--)
		{
			if(data.getClusters().contains(tree.getColumn(i).getText()))
				className.setText(i, "x");
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<TableRowData> generateTableData(Tree tree)
	{
		List<String> dependencyClusters = new ArrayList<String>();
		List<String> ruleDefinedClusters = new ArrayList<String>();
		List<TableRowData> result = new ArrayList<TableRowData>();
		
		Iterator iter = finalGraph.getVertices().iterator();
		while(iter.hasNext())
		{
			Vertex v = (Vertex) iter.next();
			TableRowData temp = new TableRowData();
			temp.setContainer(v.getUserDatum("class.jar").toString());
			temp.setNamespace(v.getUserDatum("class.packageName").toString());
			temp.setClassName(v.getUserDatum("class.name").toString());
			
			List<String> clusters = new ArrayList<String>();
			if(!dependencyClusters.contains(v.getUserDatum("class.cluster").toString()))
				dependencyClusters.add(v.getUserDatum("class.cluster").toString());
			clusters.add(v.getUserDatum("class.cluster").toString());
			
			for(String ruleCluster : getRuleDefinedClusters(v))
			{
				if(!ruleDefinedClusters.contains(ruleCluster)) ruleDefinedClusters.add(ruleCluster);
				clusters.add(ruleCluster);
			}
			temp.setClusters(clusters);
			result.add(temp);
		}
		
		
		
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Project");
		column.setMoveable(true);
		int cols = dependencyClusters.size();
		System.out.println("[OG]: clusters = "+cols);
		for(int i=0; i<cols; i++)
		{
			new TreeColumn(tree, SWT.LEFT).setText(dependencyClusters.get(i));
		}
		cols = ruleDefinedClusters.size();
		for(int i=0; i<cols; i++)
		{
			if(ruleDefinedClusters.get(i).equals("null")) continue;
			new TreeColumn(tree, SWT.LEFT).setText(ruleDefinedClusters.get(i));
		}
		
		return result;
	}
	
	
	private List<String> getRuleDefinedClusters(Vertex v)
	{
		List<String> result = new ArrayList<String>();
		String s = v.getUserDatum("classification").toString();
		StringTokenizer st = new StringTokenizer(s, ",[] ");
		while(st.hasMoreTokens())
		{
			result.add(st.nextToken());
		}
		return result;
	}
	
	
	
	private class TableRowData{
		private String container;
		private String namespace;
		private String className;
		private List<String> clusters;
		
		public String getContainer() {
			return container;
		}
		public void setContainer(String container) {
			this.container = container;
		}
		public String getNamespace() {
			return namespace;
		}
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public List<String> getClusters() {
			return clusters;
		}
		public void setClusters(List<String> clusters) {
			this.clusters = clusters;
		}
	}
}
