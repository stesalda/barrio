package nz.ac.massey.cs.barrio.xmlExporter;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.graphManager.GraphManager;
import nz.ac.massey.cs.barrio.graphManager.KnownGraphManagers;
import edu.uci.ics.jung.graph.Graph;

public class XmlExporter implements Exporter {
	
	public void export(Graph initGraph, Graph finalGraph, int separation, String folderName) 
	{
		PrintStream out = null;
		String filename = finalGraph.getUserDatum("file").toString();
		filename = filename.substring(filename.lastIndexOf('\\'),filename.lastIndexOf('.'));
		try {
			out = new PrintStream(folderName+filename+".xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		if(out==null) return;
				
		GraphManager gm = KnownGraphManagers.all().get(0);
		gm.setGraph(initGraph);
		int numNamespaces = 0;
		int numClasses = 0;
		int numCwMC = 0;
		int numCwMCns = 0;
		int numNwMC = 0;
		int numNwMCns = 0;
		for(String containerName:gm.getContainers())
		{
			numNamespaces += gm.getNamespaces(containerName).size();
			if(gm.getContainerClusters(containerName, true).size()>1) numCwMC++;
			if(gm.getContainerClusters(containerName, false).size()>1) numCwMCns++;
			for(String namespaceName:gm.getNamespaces(containerName))
			{
				numClasses += gm.getClasses(containerName, namespaceName).size();
				if(gm.getNamespaceClusters(containerName, namespaceName, true).size()>1) numNwMC++;
				if(gm.getNamespaceClusters(containerName, namespaceName, false).size()>1) numNwMCns++;
			}
		}
		
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.print("<graph file=\"");
		out.print(finalGraph.getUserDatum("file"));
		out.println("\">");
		out.print("<containers count =\"");
		out.print(gm.getContainers().size());
		out.println("\">");
		for(String c:gm.getContainers())
		{
			out.print("<container name=\"");
			out.print(c);
			out.println("\" />");
		}
		out.println("</containers>");
		out.print("<namespaces count =\"");

		out.print(numNamespaces);
		out.println("\" />");
		out.print("<classes count =\"");
		out.print(numClasses);
		out.println("\" />");
			
		out.println("<separation value =\"0\">");
		out.print("<clusters count=\"");
		out.print(gm.getProjectClusters(true).size());
		out.print("\" non-singleton-count=\"");
		out.print(gm.getProjectClusters(false).size());
		out.println("\" />");
		out.print("<relationships count=\"");
		out.print(initGraph.numEdges());
		out.println("\" />");
		
		
		
		out.print("<containers-with-clusters count=\"");
		out.print(numCwMC);
		out.print("\" non-singleton-count=\"");
		out.print(numCwMCns);
		out.println("\" />");
		
		out.print("<namesapses-with-clusters count=\"");
		out.print(numNwMC);
		out.print("\" non-singleton-count=\"");
		out.print(numNwMCns);
		out.println("\" />");		
		out.println("</separation>");
		
		gm.setGraph(finalGraph);
		numCwMC = 0;
		numCwMCns = 0;
		numNwMC = 0;
		numNwMCns = 0;
		for(String containerName:gm.getContainers())
		{
			if(gm.getContainerClusters(containerName, true).size()>1) numCwMC++;
			if(gm.getContainerClusters(containerName, false).size()>1) numCwMCns++;
			for(String namespaceName:gm.getNamespaces(containerName))
			{
				if(gm.getNamespaceClusters(containerName, namespaceName, true).size()>1) numNwMC++;
				if(gm.getNamespaceClusters(containerName, namespaceName, false).size()>1) numNwMCns++;
			}
		}
		out.print("<separation value =\"");
		out.print(separation);
		out.println("\">");
		out.print("<clusters count=\"");
		out.print(gm.getProjectClusters(true).size());
		out.print("\" non-singleton-count=\"");
		out.print(gm.getProjectClusters(false).size());
		out.println("\" />");
		out.print("<relationships count=\"");
		out.print(finalGraph.numEdges());
		out.println("\" />");
		
		
		
		out.print("<containers-with-clusters count=\"");
		out.print(numCwMC);
		out.print("\" non-singleton-count=\"");
		out.print(numCwMCns);
		out.println("\" />");
		
		out.print("<namesapses-with-clusters count=\"");
		out.print(numNwMC);
		out.print("\" non-singleton-count=\"");
		out.print(numNwMCns);
		out.println("\" />");		
		out.println("</separation>");
		
		
		out.println("</graph>");
		out.close();
	}
}
