package nz.ac.massey.cs.barrio.odemReader;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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
		Graph graph = new DirectedSparseGraph();
		String filename = input.toString();
		String jfilename = ".jungGraph.xml";
		if(filename==null || filename.length()<1) return null;
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(filename));
			
			PrintStream out = new PrintStream(jfilename);
	
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.print("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"");
			out.print(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			out.println(" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">");
			out.println("<graph edgedefault=\"directed\">");
	
			nodes = new ArrayList<String>();
			tempEdges = new ArrayList<TempEdge>();
			edges = new ArrayList<String>();
			writeNodes(doc, out);
			buildEdgeList();
			writeEdges(out);
	
			out.println("</graph>");
			out.println("</graphml>");
			out.close();
			
			GraphMLFile graphml = new GraphMLFile();
			graph = graphml.load(jfilename);
			File file = new File(jfilename);
			System.out.println("[OdemReader]: graph = "+graph.getVertices().size()+" nodes, "+graph.getEdges().size()+" edges");
			file.delete();
		} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return graph;
	}
	
	private void writeNodes(Document doc, PrintStream out)
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
							out.print("<node id=\"");
							out.print(nodeId);
							out.print("\" class.id=\"");
							out.print(nodeId);
							out.print("\" class.jar=\"");
							out.print(containerStr.substring(containerStr.lastIndexOf('/')+1));
							out.print("\" class.packageName=\"");
							out.print(namespaceStr);
							out.print("\" class.name=\"");
							out.print(typeStr.substring(typeStr.lastIndexOf('.')+1));
							out.print("\" class.cluster=\"null\" class.isInterface=\"");
							
							if(typeAttr.getNamedItem("classification")!=null)
								out.print(typeAttr.getNamedItem("classification").getNodeValue().equals("interface"));
							else out.print("null");
							
							out.print("\" class.isAbstract=\"");
							if(typeAttr.getNamedItem("isAbstract")!=null && typeAttr.getNamedItem("isAbstract").getNodeValue().equals("yes"))
								out.print("true");
							else out.print("false");
							out.print("\" class.isException=\"");
							out.print(typeStr.endsWith("Exception"));
							out.print("\" class.access=\"");
							
							if(typeAttr.getNamedItem("visibility")!=null)
								out.print(typeAttr.getNamedItem("visibility").getNodeValue());
							else out.print("null");
							
							out.println("\" node.isSelected=\"false\" />");
							
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
					buffer.append("\" edge.isSelected=\"false\" relationship.state=\"");
					buffer.append("\" relationship.betweenness=\"null\" />");
					edges.add(buffer.toString());
					break;
				}
			}
		}
	}
	
	
	private void writeEdges(PrintStream out)
	{
		Iterator<String> iter = edges.iterator();
		while(iter.hasNext())
		{
			out.println(iter.next());
		}
	}
}
