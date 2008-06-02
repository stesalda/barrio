package nz.ac.massey.cs.barrio.xmlExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.exporter.Exporter;

public class XmlExporter implements Exporter {

	private List<String> jars = new ArrayList<String>();
	
	public void export(Graph initGraph, Graph clusteredGraph, int sepLevel,
			List<String> filters, List<Edge> removed, Tree treePwMC, Tree treeCwMP) 
	{
		File file= setFile();
		try {
			PrintStream out = new PrintStream(file);
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");//start document
			out.println("<project>");//start project
			writeHeading(out, initGraph);
			writeProjectState(out, clusteredGraph, sepLevel, filters, removed, treePwMC, treeCwMP);
			out.println("</project>");//end project
			out.close();
			System.out.println("[xmlExporter]: file saved");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private File setFile()
	{
		Shell shell = new Shell();
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
	    String fileName = dlg.open();
	    shell.close();
	    if (fileName != null) {
	      System.out.println("[xmlExporter]: save " + fileName);
	      return new File(fileName);
	    }
		return null;
	}
	
	private String indent(int n)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; i++)
		{
			sb.append('\t');
		}
		return sb.toString();
	}
	
	private void writeHeading(PrintStream out, Graph initGraph)
	{
		out.print(indent(1));
		out.println("<heading>");//open heading
		writeFileContent(out, initGraph);
		writeInitialState(out, initGraph);
		out.print(indent(1));
		out.println("</heading>"); //end heading
	}
	
	@SuppressWarnings("unchecked")
	private void writeFileContent(PrintStream out, Graph initGraph)
	{
		Iterator<Vertex> vertIterator = initGraph.getVertices().iterator();
		while(vertIterator.hasNext())
		{
			Vertex v = vertIterator.next();
			String jar = v.getUserDatum("class.jar").toString();
			if(!jars.contains(jar)) jars.add(jar);
		}
		
		out.print(indent(2));
		out.println("<file-content>");//open file content
			Iterator<String> iter = jars.iterator();
			while(iter.hasNext())
			{
				out.print(indent(3));
				out.println("<file name=\""+iter.next()+"\" />");//add all project files analysed
			}
		out.print(indent(2));		
		out.println("</file-content>"); //close file-content
	}

	@SuppressWarnings("unchecked")
	private void writeInitialState(PrintStream out, Graph initGraph)
	{
		List<String> packs = new ArrayList<String>();
		Iterator<Vertex> vertIterator = initGraph.getVertices().iterator();
		while(vertIterator.hasNext())
		{
			Vertex v = vertIterator.next();
			String jar = v.getUserDatum("class.jar").toString();
			String pack = v.getUserDatum("class.packageName").toString();
			if(!packs.contains(jar+" "+pack)) packs.add(jar+" "+pack);
		}
		
		out.print(indent(2));
		out.println("<initial-state>"); //open initial state
			out.print(indent(3));
			out.print("<container count=\""); //add number of jar files in the project
			out.print(jars.size());
			out.println("\" />");
			
			out.print(indent(3));
			out.print("<package count=\""); //add number of packages in the project
			out.print(packs.size());
			out.println("\" />");
			
			out.print(indent(3));
			out.print("<class count=\"");
			out.print(initGraph.getVertices().size());
			out.println("\" />");
			
			out.print(indent(3));
			out.print("<relationship count=\"");
			out.print(initGraph.getEdges().size());
			out.println("\" />");
		out.print(indent(2));
		out.println("</initial-state>");
	}
	
	private void writeProjectState(PrintStream out, Graph clusteredGraph, int sepLevel,
			List<String> filters, List<Edge> removed, Tree treePwMC, Tree treeCwMP)
	{
		out.print(indent(1));
		out.println("<project-state>");
		writeInput(out, sepLevel, filters);
		writeOutput(out, clusteredGraph, removed, treePwMC, treeCwMP);
		out.print(indent(1));
		out.println("</project-state>");
	}
	
	private void writeInput(PrintStream out, int sepLevel, List<String> filters)
	{
		out.print(indent(2));
		out.println("<input>");
			out.print(indent(3));
			out.println("<active-filters>");
				Iterator<String> filtersIterator = filters.iterator();
				while(filtersIterator.hasNext())
				{
					out.print(indent(4));
					out.print("<filter name=\"");
					out.print(filtersIterator.next());
					out.println("\" />");
				}
			out.print(indent(3));
			out.println("</active-filters>");
			
			out.print(indent(3));
			out.print("<separation value=\"");
			out.print(sepLevel);
			out.println("\" />");
		out.print(indent(2));
		out.println("</input>");
	}
	
	
	private void writeOutput(PrintStream out, Graph clusteredGraph, List<Edge> removed, Tree treePwMC, Tree treeCwMP)
	{
		out.print(indent(2));
		out.println("<output>");
		writeRemovedEdges(out, removed);
		writeClusteredState(out, clusteredGraph);
		printTree1(out, treePwMC);
		printTree2(out, treeCwMP);
		out.print(indent(2));
		out.println("</output>");
	}

	@SuppressWarnings("unchecked")
	private void writeClusteredState(PrintStream out, Graph clusteredGraph)
	{
		List<String> cJars = new ArrayList<String>();
		List<String> cPacks = new ArrayList<String>();
		
		Iterator<Vertex> vertIterator = clusteredGraph.getVertices().iterator();
		while(vertIterator.hasNext())
		{
			Vertex v = vertIterator.next();
			String jar = v.getUserDatum("class.jar").toString();
			String pack = v.getUserDatum("class.packageName").toString();
			if(!cJars.contains(jar)) cJars.add(jar);
			if(!cPacks.contains(jar+" "+pack)) cPacks.add(jar+" "+pack);
		}
		
		out.print(indent(3));
		out.println("<clustered-state>");
			out.print(indent(4));
			out.print("<container count=\"");
			out.print(cJars.size());
			out.println("\" />");
		
			out.print(indent(4));
			out.print("<package count=\"");
			out.print(cPacks.size());
			out.println("\" />");
			
			out.print(indent(4));
			out.print("<class count=\"");
			out.print(clusteredGraph.getVertices().size());
			out.println("\" />");
			
			out.print(indent(4));
			out.print("<relationship count=\"");
			out.print(clusteredGraph.getEdges().size());
			out.println("\" />");
		out.print(indent(3));
		out.println("</clustered-state>");
	}

	private void writeRemovedEdges(PrintStream out, List<Edge> removed) 
	{
		out.print(indent(3));
		out.print("<edges-removed-by-separation count=\"");
		out.print(removed.size());
		out.println("\">");
			Iterator<Edge> removedIterator = removed.iterator();
			while(removedIterator.hasNext())
			{
				Edge e = removedIterator.next();
				Vertex src = (Vertex) e.getEndpoints().getFirst();
				Vertex dest = (Vertex) e.getEndpoints().getSecond();
				
				out.print(indent(4));
				out.print("<edge source-container=\"");
				out.print(src.getUserDatum("class.jar").toString());
				out.print("\" source-class=\"");
				out.print(src.getUserDatum("class.fullName").toString());
				out.print("\" destination-container=\"");
				out.print(dest.getUserDatum("class.jar").toString());
				out.print("\" destination-class=\"");
				out.print(dest.getUserDatum("class.fullName").toString());
				out.print("\" type=\"");
				out.print(e.getUserDatum("relationship.type").toString());
				out.print("\" betweenness=\"");
				out.print(e.getUserDatum("relationship.betweenness").toString());
				out.println("\" />");
			}
		out.print(indent(3));
		out.println("</edges-removed-by-separation>");
	}
	
	private void printTree1(PrintStream out, Tree treePwMC)
	{
		out.print(indent(3));
		out.println("<packages-with-multiple-clusters>");
		TreeItem[] root = treePwMC.getItems();
		TreeItem[] jars = root[0].getItems();
		for(int i=0; i<jars.length; i++)
		{
			String jarName = getName(jars[i].getText());
			out.print(indent(4));
			out.print("<container name=\"");
			out.print(jarName);
			out.println("\">");
			TreeItem[] packs = jars[i].getItems();
			for(int j=0; j<packs.length; j++)
			{
				String packageName = getName(packs[j].getText());
				out.print(indent(5));
				out.print("<package name=\"");
				out.print(packageName);
				out.println("\">");
				TreeItem[] clusters = packs[j].getItems();
				for(int n=0; n<clusters.length; n++)
				{
					String clusterName = getName(clusters[n].getText());
					out.print(indent(6));
					out.print("<cluster name=\"");
					out.print(clusterName);
					out.println("\">");
					TreeItem[] classes = clusters[n].getItems();
					for(int m=0; m<classes.length; m++)
					{
						out.print(indent(7));
						out.print("<class name=\"");
						out.print(classes[m].getText());
						out.println("\" />");
					}
					out.print(indent(6));
					out.println("</cluster>");
				}
				out.print(indent(5));
				out.println("</package>");
			}
			out.print(indent(4));
			out.println("</container>");
		}
		out.print(indent(3));
		out.println("</packages-with-multiple-clusters>");
	}
	
	private void printTree2(PrintStream out, Tree treeCwMP)
	{
		out.print(indent(3));
		out.println("<clusters-with-multiple-packages>");
		TreeItem[] root = treeCwMP.getItems();
		TreeItem[] clusters = root[0].getItems();
		for(int i=0; i<clusters.length; i++)
		{
			String clusterName = getName(clusters[i].getText());
			out.print(indent(4));
			out.print("<cluster name=\"");
			out.print(clusterName);
			out.println("\">");
			TreeItem[] jars = clusters[i].getItems();
			for(int j=0; j<jars.length; j++)
			{
				String jarName = getName(jars[j].getText());
				out.print(indent(5));
				out.print("<container name=\"");
				out.print(jarName);
				out.println("\">");
				TreeItem[] packs = jars[j].getItems();
				for(int n=0; n<packs.length; n++)
				{
					String packageName = getName(packs[n].getText());
					out.print(indent(6));
					out.print("<package name=\"");
					out.print(packageName);
					out.println("\">");
					TreeItem[] classes = packs[n].getItems();
					for(int m=0; m<classes.length; m++)
					{
						out.print(indent(7));
						out.print("<class name=\"");
						out.print(classes[m].getText());
						out.println("\" />");
					}
					out.print(indent(6));
					out.println("</package>");
				}
				out.print(indent(5));
				out.println("</container>");
			}
			out.print(indent(4));
			out.println("</cluster>");
		}
		out.print(indent(3));
		out.println("</clusters-with-multiple-packages>");
	}
	
	private String getName(String str)
	{
		int i;
		if(str.contains(" ")) i =str.lastIndexOf(' ');
		else i = str.length()-1;
		return str.substring(0, i);
	}
}
