package nz.ac.massey.cs.barrio.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nz.ac.massey.cs.barrio.graphManager.GraphManager;
import nz.ac.massey.cs.barrio.graphManager.KnownGraphManagers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class OutputGenerator {
	
	public void generateListRemovedEdges(List<Object[]> list, List<Edge> removedEdges)
	{
        int i = 1;
        list.clear();           
        if(removedEdges!=null) for(Edge e:removedEdges)
        {
            Vertex src = (Vertex) e.getEndpoints().getFirst();
            Vertex dest = (Vertex) e.getEndpoints().getSecond();
            
            Object[] data = new Object[6];

            data[0] = e.getUserDatum("id").toString();
//                      System.out.println("[OG]: "+data[0]);
            data[1] = String.valueOf(e.getUserDatum("relationship.separation"));
            
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
            i++;
        }
	}
	
	public void generateTableTree(Graph graph, Tree tree, boolean showSingletons) 
	{
		TreeColumn[] columns1 = tree.getColumns();
        for (int i = 0, n = columns1.length; i < n; i++) {
          columns1[i].dispose();
        }
		tree.removeAll();
		
		GraphManager manager = KnownGraphManagers.all().get(0);
		manager.setGraph(graph);
		Display display = tree.getDisplay();
		Color containerColor = new Color(display, 170,170,170);
		Color namespaceColor = new Color(display, 220,220,220);
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
        column.setText("Project");
        column.setMoveable(true);
        column.pack();
        List<String> projectClusters = manager.getProjectClusters(true);
        for(String projectClusterName: projectClusters)
        {
        	new TreeColumn(tree, SWT.LEFT).setText(projectClusterName);
        }
        List<String> projectRuleDefinedClusters = manager.getProjectRuleDefinedClusters();
        //System.out.println("[OG]: ruledefclust = " +projectRuleDefinedClusters.size());
        for(String ruleCluster: projectRuleDefinedClusters)
        {
        	new TreeColumn(tree, SWT.LEFT).setText(ruleCluster);
        }

		List<String> containers = manager.getContainers();
		for(String containerName: containers)
		{
			TreeItem containerTreeItem = new TreeItem(tree,0);
			containerTreeItem.setText(getContainerData(containerName, manager, tree));
			containerTreeItem.setBackground(containerColor);
			List<String> namespases = manager.getNamespaces(containerName);
			for(String namespaceName: namespases)
			{
				TreeItem namespaceTreeItem = new TreeItem(containerTreeItem,0);
				namespaceTreeItem.setText(getNamespaceData(containerName, namespaceName, manager, tree));
				namespaceTreeItem.setBackground(namespaceColor);
				List<String> classes = manager.getClasses(containerName, namespaceName);
				for(String className: classes)
				{
					TreeItem classTreeItem = new TreeItem(namespaceTreeItem,0);
					classTreeItem.setText(getClassData(containerName, namespaceName, className, manager, tree));
				}
			}
		}
		TreeColumn[] columns2 = tree.getColumns();
		columns2[0].pack();
        for (int i = 1, n = columns2.length; i < n; i++) 
        {
        	int clusterSize = manager.getProjectClusterSize(columns2[i].getText());
        	StringBuilder builder = new StringBuilder();
        	builder.append(columns2[i].getText());
        	if(columns2[i].getText().contains("cluster-"))
        	{
	        	builder.append(" - (");
	        	builder.append(manager.getProjectClusterSize(columns2[i].getText()));
	        	builder.append(')');
	        	columns2[i].setText(builder.toString());
        	}        	
        	columns2[i].pack();
        	if(!showSingletons && clusterSize==1) columns2[i].setWidth(0);
        }
	}
	
	private String[] getContainerData(String containerName, GraphManager manager, Tree tree)
	{
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		builder.append(containerName);
		builder.append("-(");
		builder.append(manager.getContainerSize(containerName));
		builder.append(')');
		result.add(builder.toString());
		List<String> ruleClusters = manager.getContainerRuleDefinedClusters(containerName);
		TreeColumn[] columns = tree.getColumns();
		for(int i=1; i<columns.length; i++)
		{
			int intersectionSize = manager.getIntersectionSize(containerName, columns[i].getText());
			if(intersectionSize>0) result.add(String.valueOf(intersectionSize));
			else result.add(" ");
		}
		
		String[] str = new String[result.size()];
		for(int i=0; i<result.size(); i++)
		{
			str[i] = result.get(i);
		}
		return str;
	}
	
	private String[] getNamespaceData(String containerName, String namespaceName, GraphManager manager, Tree tree)
	{
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		builder.append(namespaceName);
		builder.append("-(");
		builder.append(manager.getNamespaceSize(containerName, namespaceName));
		builder.append(')');
		result.add(builder.toString());
		List<String> clusters = manager.getNamespaceClusters(containerName, namespaceName, true);
		List<String> ruleClusters = manager.getNamespaceRuleDefinedClusters(containerName, namespaceName);
		TreeColumn[] columns = tree.getColumns();
		for(int i=1; i<columns.length; i++)
		{
			int intersectionSize = manager.getIntersectionSize(containerName, namespaceName, columns[i].getText());
			if(intersectionSize>0) result.add(String.valueOf(intersectionSize));
			else result.add(" ");
		}
		String[] str = new String[result.size()];
		for(int i=0; i<result.size(); i++)
		{
			str[i] = result.get(i);
		}
		return str;
	}
	
	private String[] getClassData(String containerName, String namespaceName, String className, GraphManager manager, Tree tree)
	{
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		builder.append(className);
		builder.append("-(");
		builder.append(manager.getClassAnnotation(containerName, namespaceName, className));
		builder.append(')');
		result.add(builder.toString());
		
		String cluster = manager.getClassCluster(containerName, namespaceName, className);
		List<String> ruleClusters = manager.getClassRuleDefinedClusters(containerName, namespaceName, className);
		TreeColumn[] columns = tree.getColumns();
		for(int i=1; i<columns.length; i++)
		{
			if(cluster.equals(columns[i].getText()) || ruleClusters.contains(columns[i].getText())) result.add("x");
			else result.add(" ");
		}
		String[] str = new String[result.size()];
		for(int i=0; i<result.size(); i++)
		{
			str[i] = result.get(i);
		}
		return str;
	}
}
