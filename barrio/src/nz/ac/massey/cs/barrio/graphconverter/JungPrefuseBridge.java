package nz.ac.massey.cs.barrio.graphconverter;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import edu.uci.ics.jung.graph.Edge;


import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;

public class JungPrefuseBridge {
	
	public prefuse.data.Graph convert(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		prefuse.data.Graph prefuseGraph = null;
		if(jungGraph!=null)
		{
			java.io.StringWriter out = new java.io.StringWriter();
			writePrefuseGraphMl(out, jungGraph);
			try {
				byte[] stringBytes = out.toString().getBytes("UTF-8"); 
				InputStream stream = new ByteArrayInputStream(stringBytes);
				
				GraphMLReader reader = new GraphMLReader();
				prefuseGraph = reader.readGraph(stream);
				prefuseGraph.addColumn("class.expression", new LabelExpression("class.jar","class.packageName","class.name"));
				prefuseGraph.addColumn("class.icon", new ImageExpression("class.isInterface", "class.isException", "class.isAbstract", "class.access"));
			} catch (DataIOException e) {
				//e.printStackTrace();
				prefuseGraph = new Graph();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}				
		}
		return prefuseGraph;
	}

	
	
	
	@SuppressWarnings("unchecked")
	private void writePrefuseGraphMl(StringWriter out, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		//PrintStream out = new PrintStream(".tempGraph.xml");
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
		out.write("<graph edgedefault=\"directed\">\n");
		out.write('\n');
		out.write("<!-- data schema -->\n");
		
		if(jungGraph.getVertices().size()>0)
		{
			Iterator iter = jungGraph.getVertices().iterator();
			edu.uci.ics.jung.graph.Vertex vert = (edu.uci.ics.jung.graph.Vertex) iter.next();
			Iterator<String> keyIter = vert.getUserDatumKeyIterator();
			while(keyIter.hasNext())
			{
				String key = keyIter.next();
				out.write("<key id=\"");
				out.write(key);
				out.write("\" for=\"node\" attr.name=\"");
				out.write(key);
				out.write("\" attr.type=\"string\" />\n");
			}
		}
		
		if(jungGraph.getEdges().size()>0)
		{
			Iterator iter2 = jungGraph.getEdges().iterator();
			edu.uci.ics.jung.graph.Edge edge = (edu.uci.ics.jung.graph.Edge) iter2.next();
			Iterator<String> keyIter2 = edge.getUserDatumKeyIterator();
			while(keyIter2.hasNext())
			{
				String key = keyIter2.next();
				out.write("<key id=\"");
				out.write(key);
				out.write("\" for=\"edge\" attr.name=\"");
				out.write(key);
				out.write("\" attr.type=\"string\" />\n");
			}
		}

		out.write('\n');
		out.write("<!-- nodes -->\n");
		
		Iterator<edu.uci.ics.jung.graph.Vertex> vertexIterator = jungGraph.getVertices().iterator();
		while(vertexIterator.hasNext())
		{
			edu.uci.ics.jung.graph.Vertex v = vertexIterator.next();
			out.write("<node id=\"");
			out.write(v.getUserDatum("class.id").toString());
			out.write("\">\n");
			
			Iterator<String> keyIterator = v.getUserDatumKeyIterator();
			while(keyIterator.hasNext())
			{
				String key = keyIterator.next();
				String value = v.getUserDatum(key).toString();

				out.write("<data key=\"");
				out.write(key);
				out.write("\">");
				out.write(value);
				out.write("</data>\n");				
			}
			
			out.write("</node>\n");
		}
		
		out.write('\n');
		out.write("<!-- edges -->\n");
		
		for(Object obj:jungGraph.getEdges())
		{
			edu.uci.ics.jung.graph.Edge e = (Edge) obj;
			out.write("<edge source=\"");
			out.write(e.getUserDatum("sourceId").toString());
			out.write("\" target=\"");
			out.write(e.getUserDatum("targetId").toString());
			out.write("\">\n");
			
			Iterator<String> keyIterator = e.getUserDatumKeyIterator();
			while(keyIterator.hasNext())
			{
				String key = keyIterator.next();
				String value = e.getUserDatum(key).toString();
				
				out.write("<data key=\"");
				out.write(key);
				out.write("\">");
				out.write(value);
				out.write("</data>\n");
			}
			out.write("</edge>\n");
		}

		out.write("</graph>\n");
		out.write("</graphml>\n");
		
	}
	
}
