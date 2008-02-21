package nz.ac.massey.cs.barrio.visual;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;

import nz.ac.massey.cs.barrio.constants.BarrioConstants;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;

public class JungPrefuseBridge {
	
	public prefuse.data.Graph convert(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		prefuse.data.Graph prefuseGraph = null;
		if(jungGraph!=null)
		{
			writePrefuseGraphMl(jungGraph);
			try {
				GraphMLReader reader = new GraphMLReader();
				prefuseGraph = reader.readGraph(BarrioConstants.PREFUSE_GRAPH_FILE);
				prefuseGraph.addColumn("class.expression", new LabelExpression("class.jar","class.packageName","class.name"));
				prefuseGraph.addColumn("class.icon", new ImageExpression("class.isInterface", "class.isException", "class.isAbstract", "class.access"));
			} catch (DataIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prefuseGraph;
	}

	
	
	
	@SuppressWarnings("unchecked")
	private void writePrefuseGraphMl(edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		try {
			PrintStream out = new PrintStream(BarrioConstants.PREFUSE_GRAPH_FILE);
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">");
			out.println("<graph edgedefault=\"directed\">");
			out.println();
			out.println("<!-- data schema -->");
			
			Iterator iter = jungGraph.getVertices().iterator();
			edu.uci.ics.jung.graph.Vertex vert = (edu.uci.ics.jung.graph.Vertex) iter.next();
			Iterator<String> keyIter = vert.getUserDatumKeyIterator();
			while(keyIter.hasNext())
			{
				String key = keyIter.next();
				out.print("<key id=\"");
				out.print(key);
				out.print("\" for=\"node\" attr.name=\"");
				out.print(key);
				out.println("\" attr.type=\"string\" />");
			}
			
			Iterator iter2 = jungGraph.getEdges().iterator();
			edu.uci.ics.jung.graph.Edge edge = (edu.uci.ics.jung.graph.Edge) iter2.next();
			Iterator<String> keyIter2 = edge.getUserDatumKeyIterator();
			while(keyIter2.hasNext())
			{
				String key = keyIter2.next();
				out.print("<key id=\"");
				out.print(key);
				out.print("\" for=\"edge\" attr.name=\"");
				out.print(key);
				out.println("\" attr.type=\"string\" />");
			}
			

			out.println();
			out.println("<!-- nodes -->");
			
			Iterator<edu.uci.ics.jung.graph.Vertex> vertexIterator = jungGraph.getVertices().iterator();
			while(vertexIterator.hasNext())
			{
				edu.uci.ics.jung.graph.Vertex v = vertexIterator.next();
				out.print("<node id=\"");
				out.print(v.getUserDatum("class.id").toString());
				out.println("\">");
				
				Iterator<String> keyIterator = v.getUserDatumKeyIterator();
				while(keyIterator.hasNext())
				{
					String key = keyIterator.next();
					String value = v.getUserDatum(key).toString();

					out.print("<data key=\"");
					out.print(key);
					out.print("\">");
					out.print(value);
					out.print("</data>");				
				}
				
				out.println("</node>");
			}
			
			out.println();
			out.println("<!-- edges -->");
			
			Iterator<edu.uci.ics.jung.graph.Edge> edgeIterator = jungGraph.getEdges().iterator();
			while(edgeIterator.hasNext())
			{
				edu.uci.ics.jung.graph.Edge e = edgeIterator.next();
				out.print("<edge source=\"");
				out.print(e.getUserDatum("sourceId").toString());
				out.print("\" target=\"");
				out.print(e.getUserDatum("targetId"));
				out.println("\">");
				
				Iterator<String> keyIterator = e.getUserDatumKeyIterator();
				while(keyIterator.hasNext())
				{
					String key = keyIterator.next();
					String value = e.getUserDatum(key).toString();
					
					out.print("<data key=\"");
					out.print(key);
					out.print("\">");
					out.print(value);
					out.print("</data>");
				}
				out.println("</edge>");
			}

			out.println("</graph>");
			out.println("</graphml>");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
