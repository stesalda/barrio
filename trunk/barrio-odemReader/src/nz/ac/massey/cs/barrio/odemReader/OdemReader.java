package nz.ac.massey.cs.barrio.odemReader;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nz.ac.massey.cs.barrio.inputReader.InputReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class OdemReader implements InputReader{

	private List<TempEdge> tempEdges;

	public void read(String filename, Graph graph) 
	{	
		if(filename==null || filename.length()<1) return;
		//graph = new DirectedSparseGraph();
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(filename));
			
			tempEdges = new ArrayList<TempEdge>();
			addNodes(doc, graph);
			addEdges(doc, graph);
			
			System.out.println("[OdemReader]: "+graph.getVertices().size()+"  "+graph.getEdges().size());
		} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	private void addNodes(Document doc, Graph graph) {
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
							
							Vertex v = new DirectedSparseVertex();
							v.addUserDatum("class.id", nodeId, UserData.SHARED);
							v.addUserDatum("class.jar", containerStr.substring(containerStr.lastIndexOf('/')+1), UserData.SHARED);
							v.addUserDatum("class.packageName", namespaceStr, UserData.SHARED);
							v.addUserDatum("class.name", typeStr.substring(typeStr.lastIndexOf('.')+1), UserData.SHARED);
							v.addUserDatum("class.cluster", "null", UserData.SHARED);
							
							String isInterface = "false";
							if(typeAttr.getNamedItem("classification")!=null)
								isInterface = String.valueOf(typeAttr.getNamedItem("classification").getNodeValue().equals("interface"));
							v.addUserDatum("class.isInterface", isInterface, UserData.SHARED);
							
							String isAbstract = "false";
							if(typeAttr.getNamedItem("isAbstract")!=null && typeAttr.getNamedItem("isAbstract").getNodeValue().equals("yes"))
								isAbstract = "true";
							v.addUserDatum("class.isAbstract", isAbstract, UserData.SHARED);
							
							v.addUserDatum("class.isException", String.valueOf(typeStr.endsWith("Exception")), UserData.SHARED);
							
							String access = "null";
							if(typeAttr.getNamedItem("visibility")!=null)
								access = typeAttr.getNamedItem("visibility").getNodeValue();
							v.addUserDatum("class.access", access, UserData.SHARED);

							v.addUserDatum("node.isSelected", "false", UserData.SHARED);
							graph.addVertex(v);
							
							if(type.getNodeType()==Node.ELEMENT_NODE)
							{
								Element typeElement = (Element) type;
								NodeList relationships = typeElement.getElementsByTagName("depends-on");
								for(int e=0; e<relationships.getLength(); e++)
								{
									Node relationship = relationships.item(e);
									NamedNodeMap relationshipAttr = relationship.getAttributes();
									
									TempEdge tempEdge = new TempEdge();
									tempEdge.setSource(typeStr);
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
	
	
	
	

	
	private void addEdges(Document doc, Graph graph) {
		int edgeId = 0;
		for(TempEdge temp:tempEdges)
		{
			Vertex src = null;
			Vertex dest = null;
			for(Object obj:graph.getVertices())
			{
				Vertex v = (Vertex) obj;
				String vName = v.getUserDatum("class.packageName").toString()+'.'+v.getUserDatum("class.name").toString();
				
				if(vName.equals(temp.getSource())) src = v;
				if(vName.equals(temp.getTarget())) dest = v;
				
				if(src!=null && dest!=null)
				{
					String srcName = src.getUserDatum("class.packageName").toString()+'.'+src.getUserDatum("class.name").toString();
					String destName = dest.getUserDatum("class.packageName").toString()+'.'+dest.getUserDatum("class.name").toString();
					Edge e = new DirectedSparseEdge(src, dest);
					e.addUserDatum("id", "edge-"+edgeId, UserData.SHARED);
					e.addUserDatum("sourceName", srcName, UserData.SHARED);
					e.addUserDatum("targetName", destName, UserData.SHARED);
					e.addUserDatum("relationship.type", temp.getType(), UserData.SHARED);
					e.addUserDatum("edge.isSelected", "false", UserData.SHARED);
					e.addUserDatum("relationship.state", "null", UserData.SHARED);
					e.addUserDatum("relationship.betweenness", "null", UserData.SHARED);
					graph.addEdge(e);
					break;
				}
			}
		}		
	}
}
