package nz.ac.massey.cs.barrio.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class OutputGenerator {
	
	private Graph initGraph = null;
	private Graph finalGraph = null;
	
	public OutputGenerator(Graph initGraph, Graph finalGraph)
	{
		this.initGraph = initGraph;
		this.finalGraph = finalGraph;
	}
	
	//Methods to output project description
	@SuppressWarnings("unchecked")
	public void generateProjectDescription(Tree tree)
	{
		if(initGraph!=null)
		{
			tree.removeAll();
			TreeItem jarcount = new TreeItem(tree, 0);
			jarcount.setText(" container(s)");
			TreeItem packcount = new TreeItem(tree, 0);
			packcount.setText(" package(s)");
			TreeItem classcount = new TreeItem(tree, 0);
			classcount.setText(initGraph.numVertices()+" classes");
			TreeItem edgecount = new TreeItem(tree, 0);
			edgecount.setText(initGraph.numEdges()+" relationships");
			TreeItem project = new TreeItem(tree, 0);
			project.setText("Project Structure");
			
			Iterator<Vertex> vertIter = initGraph.getVertices().iterator();
			while(vertIter.hasNext())
			{
				Vertex v = vertIter.next();
				String[] treeElement = new String[3];
				treeElement[0] = v.getUserDatum("class.jar").toString();
				treeElement[1] = v.getUserDatum("class.packageName").toString();
				treeElement[2] = v.getUserDatum("class.name").toString();
				
				addElement(project, treeElement);
			}
			countPackagesAndJars(tree, project);
			finaliseTree(tree.getItems());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void countPackagesAndJars(Tree t, TreeItem root)
	{
		int jarCount = 0;
		int packCount = 0;
		
		TreeItem[] jars = root.getItems();
		jarCount = jars.length;
		for(int i=0; i<jars.length; i++)
		{
			TreeItem[] packs = jars[i].getItems();
			packCount += packs.length;
		}
		
		TreeItem[] rootItems = t.getItems();
		for(int i=0; i<rootItems.length; i++)
		{
			if(rootItems[i].getText().equals(" container(s)")) rootItems[i].setText(jarCount+" container(s)");
			if(rootItems[i].getText().equals(" package(s)")) rootItems[i].setText(packCount+" package(s)");
		}
	}
	//project description methods end
	
	
	//Building output tree for packages with multiple clusters
	@SuppressWarnings("unchecked")
	public void generatePackagesWithMultipleClusters(Tree tree)
	{
		if(finalGraph!=null)
		{
			tree.removeAll();
			TreeItem root = new TreeItem (tree,0);
			root.setText("Packages with multiple clusters");
			
			Iterator<Vertex> vertexIterator = finalGraph.getVertices().iterator();
			while(vertexIterator.hasNext())
			{
				Vertex v = vertexIterator.next();
				
				String[] element = new String[4];
				element[0] = v.getUserDatum("class.jar").toString();
				element[1] = v.getUserDatum("class.packageName").toString();
				element[2] = v.getUserDatum("class.cluster").toString();
				element[3] = v.getUserDatum("class.name").toString();
				addElement(root, element);
			}
			removePackagesWithSingleCluster(root);
			finaliseTree(tree.getItems());
		}
	}
	
	private void removePackagesWithSingleCluster(TreeItem root)
	{
		TreeItem[] level1 = root.getItems();
		for(int i=0; i<level1.length; i++)
		{
			TreeItem[] level2 = level1[i].getItems();
			for(int j=0; j<level2.length; j++)
			{
				if(level2[j].getItems().length < 2) level2[j].dispose();
			}
		}
	}
	//end of bulding tree of packages with multiple packages
	
	
	//generate tree of clusters with multiple packages
	@SuppressWarnings("unchecked")
	public void generateClustersWithMuiltiplePackages(Tree tree)
	{
		if(finalGraph!=null)
		{
			tree.removeAll();
			TreeItem root = new TreeItem (tree,0);
			root.setText("Packages with multiple clusters");
			
			Iterator<Vertex> vertexIterator = finalGraph.getVertices().iterator();
			while(vertexIterator.hasNext())
			{
				Vertex v = vertexIterator.next();
				
				String[] element = new String[4];
				element[0] = v.getUserDatum("class.cluster").toString();
				element[1] = v.getUserDatum("class.jar").toString();
				element[2] = v.getUserDatum("class.packageName").toString();
				element[3] = v.getUserDatum("class.name").toString();
				addElement(root, element);
			}
			removeClustersWithSinglePackage(root);
			finaliseTree(tree.getItems());
		}
	}
	
	private void removeClustersWithSinglePackage(TreeItem root)
	{
		TreeItem[] level1 = root.getItems();
		for(int i=0; i<level1.length; i++)
		{
			TreeItem[] level2 = level1[i].getItems();
			if(level2.length < 2)
			{
				for(int j=0; j<level2.length; j++)
				{
					if(level2[j].getItems().length < 2) level2[j].dispose();
				}
			}
			if(level1[i].getItems().length < 1) level1[i].dispose();
		}
	}
	//done generating tree of clusters with multiple packages
	
	
	public void generateListRemovedEdges(List<String[]> list, List<Edge> removedEdges)
	{
		int i = 1;
		list.clear();		
		if(removedEdges!=null) for(Edge e:removedEdges)
		{
			Vertex src = (Vertex) e.getEndpoints().getFirst();
			Vertex dest = (Vertex) e.getEndpoints().getSecond();
			
			String[] data = new String[5];
			
			data[0] = ""+i;
			
			StringBuffer sourceStr = new StringBuffer();
			sourceStr.append(src.getUserDatum("class.jar").toString());
			sourceStr.append('\n');
			sourceStr.append(src.getUserDatum("class.packageName").toString());
			sourceStr.append('\n');
			sourceStr.append(src.getUserDatum("class.name").toString());
			data[1] = sourceStr.toString();
			
			data[2] = e.getUserDatum("relationship.type").toString();
			
			StringBuffer destStr = new StringBuffer();
			destStr.append(dest.getUserDatum("class.jar").toString());
			destStr.append('\n');
			destStr.append(dest.getUserDatum("class.packageName").toString());
			destStr.append('\n');
			destStr.append(dest.getUserDatum("class.name").toString());
			data[3] = destStr.toString();
			
			data[4] = e.getUserDatum("relationship.betweenness").toString();
			list.add(data);
//			System.out.println("[OG]: list = "+ list.size());
			i++;
		}
	}
	
	
	private void addElement(TreeItem parent, String[] element)
	{
		if(element.length==0) return;
		
		boolean contains = false;
		TreeItem newParent = null;
		
		TreeItem[] items = parent.getItems();
		for(int i=0; i<items.length; i++)
		{
			if(items[i].getText().equals(element[0]))
			{
				contains = true;
				newParent = items[i];
				break;
			}
		}
		
		if(!contains || newParent==null)
		{
			newParent = new TreeItem(parent, 0);
			newParent.setText(element[0]);
		}
		
		String[] newElement = new String [element.length - 1];
		for(int j=1; j<element.length; j++)
		{
			newElement[j-1] = element[j];
		}
		addElement(newParent, newElement);
	}
	
	private void finaliseTree(Object[] items)
	{
		if(items==null || items.length==0) return;
		
		List<TreeItem> newItems = new ArrayList<TreeItem>();
		
		for(int i=0; i<items.length; i++)
		{
			TreeItem[] children = ((TreeItem)items[i]).getItems();
			if(children.length > 0)
			{
				((TreeItem)items[i]).setText(((TreeItem)items[i]).getText()+" ("+children.length+")");
				for(int j=0; j<children.length; j++)
				{
					newItems.add(children[j]);
				}
			}
		}
		finaliseTree(newItems.toArray());
	}
}
