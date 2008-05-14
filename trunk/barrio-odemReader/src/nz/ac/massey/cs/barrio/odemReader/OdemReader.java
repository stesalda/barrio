package nz.ac.massey.cs.barrio.odemReader;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;

public class OdemReader implements InputReader {

	private List<String> nodes;
	private List<TempEdge> tempEdges;
	private List<String> edges;

	public Graph read(Object input) throws UnknownInputException, IOException
	{	
		InputStream stream = null;
		Graph graph = new DirectedSparseGraph();
		if(input == null) return graph;
		
		if(input instanceof File && input!=null) 
			stream = new FileInputStream((File)input);
		
		if(input instanceof byte[]) 
			stream = new ByteArrayInputStream(((byte[])input));
		
		
		System.out.println("[OdemReader]: stream = "+stream.toString());
		
		if(stream!=null) try 
		{			
			System.out.println("[OdemReader]: stream not null");
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(stream);
			
			java.io.StringWriter buffer = new java.io.StringWriter();
			BufferedWriter out = new BufferedWriter(buffer);
	
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.write('\n');
			out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"");
			out.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			out.write(" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">");
			out.write('\n');
			out.write("<graph edgedefault=\"directed\">");
			out.write('\n');
	
			nodes = new ArrayList<String>();
			tempEdges = new ArrayList<TempEdge>();
			edges = new ArrayList<String>();
			writeNodes(doc, out);
			buildEdgeList();
			writeEdges(out);
	
			out.write("</graph>");
			out.write('\n');
			out.write("</graphml>");
			out.close();
			
			Reader reader = new java.io.StringReader(buffer.toString());
			GraphMLFile graphml = new GraphMLFile();
			graph = graphml.load(reader);
//			System.out.println("[OdemReader]: graph = "+graph.getVertices().size()+" nodes, "+graph.getEdges().size()+" edges");
			
		} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return graph;
	}
	
	private void writeNodes(Document doc, BufferedWriter out)
	{
		int nodeId = 0;
		NodeList containerList = doc.getElementsByTagName("container");
		for(int i=0; i<containerList.getLength(); i++)
		{
			Node container = containerList.item(i);
			NamedNodeMap containerAttr = container.getAttributes();
			if(container.getNodeType()==Node.ELEMENT_NODE)
			{
				Element containerElement = (Element) container;
				NodeList namespaces = containerElement.getElementsByTagName("namespace");
				for(int j=0; j<namespaces.getLength(); j++)
				{
					Node namespace = namespaces.item(j);
					NamedNodeMap namespaceAttr = namespace.getAttributes();
					if(namespace.getNodeType()==Node.ELEMENT_NODE)
					{
						Element namespaceElement = (Element) namespace;
						NodeList types = namespaceElement.getElementsByTagName("type");
						for(int k=0; k<types.getLength(); k++)
						{
							Node type = types.item(k);
							NamedNodeMap typeAttr = type.getAttributes();
							String containerStr = containerAttr.getNamedItem("name").getNodeValue();
							String namespaceStr = namespaceAttr.getNamedItem("name").getNodeValue();
							String typeStr = typeAttr.getNamedItem("name").getNodeValue();
							try {
								out.write("<node id=\"");
								out.write(String.valueOf(nodeId));
								out.write("\" class.id=\"");
								out.write(String.valueOf(nodeId));
								out.write("\" class.jar=\"");
								out.write(containerStr.substring(containerStr.lastIndexOf('/')+1));
								out.write("\" class.packageName=\"");
								out.write(namespaceStr);
								out.write("\" class.name=\"");
								out.write(typeStr.substring(typeStr.lastIndexOf('.')+1));
								out.write("\" class.cluster=\"null\" class.isInterface=\"");
								
								if(typeAttr.getNamedItem("classification")!=null)
									out.write(String.valueOf(typeAttr.getNamedItem("classification").getNodeValue().equals("interface")));
								else out.write("null");
								
								out.write("\" class.isAbstract=\"");
								if(typeAttr.getNamedItem("isAbstract")!=null && typeAttr.getNamedItem("isAbstract").getNodeValue().equals("yes"))
									out.write("true");
								else out.write("false");
								out.write("\" class.isException=\"");
								out.write(String.valueOf(typeStr.endsWith("Exception")));
								out.write("\" class.access=\"");
								
								if(typeAttr.getNamedItem("visibility")!=null)
									out.write(typeAttr.getNamedItem("visibility").getNodeValue());
								else out.write("null");
								
								out.write("\" node.isSelected=\"false\" />");
								out.write('\n');
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							
							if(typeStr.contains(namespaceStr)) nodes.add(nodeId, typeStr);
							else nodes.add(nodeId, namespaceStr+'.'+typeStr);
							
							if(type.getNodeType()==Node.ELEMENT_NODE)
							{
								Element typeElement = (Element) type;
								NodeList relationships = typeElement.getElementsByTagName("depends-on");
								for(int e=0; e<relationships.getLength(); e++)
								{
									Node relationship = relationships.item(e);
									NamedNodeMap relationshipAttr = relationship.getAttributes();
									
									TempEdge tempEdge = new TempEdge();
									tempEdge.setSource(String.valueOf(nodeId));
									tempEdge.setType(relationshipAttr.getNamedItem("classification").getNodeValue());
									tempEdge.setTarget(relationshipAttr.getNamedItem("name").getNodeValue());
									
									tempEdges.add(tempEdge);
								}
							}
							nodeId++;
						}
					}
				}
			}
		}
	}

	
	private void buildEdgeList()
	{
		for(int i=0; i<tempEdges.size(); i++)
		{
			TempEdge te = tempEdges.get(i);
			for(int j=0; j<nodes.size(); j++)
			{
				if(te.getTarget().equals(nodes.get(j)))
				{
					StringBuffer buffer = new StringBuffer();
					buffer.append("<edge id=\"edge-");
					buffer.append(i);
					buffer.append("\" source=\"");
					buffer.append(te.getSource());
					buffer.append("\" target=\"");
					buffer.append(j);
					buffer.append("\" sourceId=\"");
					buffer.append(te.getSource());
					buffer.append("\" targetId=\"");
					buffer.append(j);
					buffer.append("\" relationship.type=\"");
					buffer.append(te.getType());
					buffer.append("\" edge.isSelected=\"false\" relationship.state=\"null");
					buffer.append("\" relationship.betweenness=\"null\" />");
					edges.add(buffer.toString());
					break;
				}
			}
		}
	}
	
	
	private void writeEdges(BufferedWriter out)
	{
		Iterator<String> iter = edges.iterator();
		while(iter.hasNext())
		{
			try {
				out.write(iter.next());
				out.write('\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
