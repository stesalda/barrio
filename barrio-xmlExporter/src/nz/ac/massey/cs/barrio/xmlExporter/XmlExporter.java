package nz.ac.massey.cs.barrio.xmlExporter;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.graphManager.GraphManager;
import nz.ac.massey.cs.barrio.graphManager.KnownGraphManagers;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class XmlExporter implements Exporter {
	
	public void export(Graph graph, int separation, List<Edge> removedEdges, List<String> filters, String filename) 
	{
		PrintStream out = null;
		try {
			out = new PrintStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		if(out==null) return;
		if(graph==null) return;	
		GraphManager gm = KnownGraphManagers.all().get(0);
		gm.setGraph(graph);
		
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.print("<graph file=\"");
		out.print(graph.getUserDatum("file"));
		out.println("\">");
		
		for(Edge e:removedEdges)
		{
			out.print("<removed-relationship source=\"");
			out.print(((Vertex)e.getEndpoints().getFirst()).getUserDatum("class.packageName"));
			out.print('.');
			out.print(((Vertex)e.getEndpoints().getFirst()).getUserDatum("class.name"));
			out.print("\" target=\"");
			out.print(((Vertex)e.getEndpoints().getSecond()).getUserDatum("class.packageName"));
			out.print('.');
			out.print(((Vertex)e.getEndpoints().getSecond()).getUserDatum("class.name"));
			out.print("\" type=\"");
			out.print(e.getUserDatum("relationship.type"));
			out.println("\" />");
		}
		
		int componentCount = 1;
		for(String cluster:gm.getProjectClusters(false))
		{
			out.print("<component-");
			out.print(componentCount);
			out.println(">");
			
			for(String containerName: gm.getContainers())
			{
				for(String namespace:gm.getNamespaces(containerName))
				{
					if(gm.getNamespaceClusters(containerName, namespace, false).contains(cluster))
					{
						out.print("<package name=\"");
						out.print(namespace);
						out.println("\">");
						
						for(String className:gm.getClasses(containerName, namespace))
						{
							if(gm.getClassCluster(containerName, namespace, className).equals(cluster))
							{
								out.print("<class name=\"");
								out.print(className);
								out.println("\" />");
							}
						}
						
						out.println("</package>");
					}
				}
			}

			out.print("</component-");
			out.print(componentCount);
			out.println(">");
			componentCount++;
		}
		
		
		
		
		
		out.println("</graph>");
		out.close();
	}
}