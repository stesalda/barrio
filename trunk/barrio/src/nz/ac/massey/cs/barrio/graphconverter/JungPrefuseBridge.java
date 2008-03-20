package nz.ac.massey.cs.barrio.graphconverter;

import java.util.Iterator;


public class JungPrefuseBridge {
	
	public prefuse.data.Graph convert(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		prefuse.data.Graph prefuseGraph = new prefuse.data.Graph();
		if(jungGraph!=null)
		{
			setColumns(prefuseGraph, jungGraph);
			prefuseGraph.addColumn("class.expression", new LabelExpression("class.jar","class.packageName","class.name"));
			prefuseGraph.addColumn("class.icon", new ImageExpression("class.isInterface", "class.isException", "class.isAbstract", "class.access"));
			
			addNodes(prefuseGraph, jungGraph);
			addEdges(prefuseGraph, jungGraph);
		}
		return prefuseGraph;
	}




	private void setColumns(prefuse.data.Graph prefuseGraph, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		for(Object obj:jungGraph.getVertices())
		{
			edu.uci.ics.jung.graph.Vertex v = (edu.uci.ics.jung.graph.Vertex) obj;
			Iterator<String> keyIterator = v.getUserDatumKeyIterator();
			while(keyIterator.hasNext())
			{
				String key = keyIterator.next();
				prefuseGraph.addColumn(key, String.class);
			}
			break;
		}
		
		
		for(Object obj:jungGraph.getEdges())
		{
			edu.uci.ics.jung.graph.Edge e = (edu.uci.ics.jung.graph.Edge) obj;
			Iterator<String> keyIterator = e.getUserDatumKeyIterator();
			while(keyIterator.hasNext())
			{
				String key = keyIterator.next();
				prefuseGraph.addColumn(key, String.class);
			}
			break;
		}
	}

	
	
	
	private void addNodes(prefuse.data.Graph prefuseGraph, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		for(Object obj:jungGraph.getVertices())
		{
			edu.uci.ics.jung.graph.Vertex v = (edu.uci.ics.jung.graph.Vertex)obj;
			prefuse.data.Node node = prefuseGraph.addNode();
			Iterator<String> keyIterator = v.getUserDatumKeyIterator();
			while(keyIterator.hasNext())
			{
				String key = keyIterator.next();
				node.set(key, v.getUserDatum(key).toString());
			}
		}
	}





	private void addEdges(prefuse.data.Graph prefuseGraph, edu.uci.ics.jung.graph.Graph jungGraph) 
	{
		for(Object obj:jungGraph.getEdges())
		{
			edu.uci.ics.jung.graph.Edge e = (edu.uci.ics.jung.graph.Edge)obj;
			String srcName = e.getUserDatum("sourceName").toString();
			String destName = e.getUserDatum("targetName").toString();

			prefuse.data.Node src = null;
			prefuse.data.Node dest = null;
			
			Iterator<prefuse.data.Node> nodeIterator = prefuseGraph.nodes();
			while(nodeIterator.hasNext())
			{
				prefuse.data.Node node = nodeIterator.next();
				String nodeName = node.getString("class.packageName")+'.'+node.getString("class.name");
				if(srcName.equals(nodeName)) src = node;
				if(destName.equals(nodeName)) dest = node;
				
				if(src!=null && dest!=null)
				{
					prefuse.data.Edge edge = prefuseGraph.addEdge(src, dest);
					Iterator<String> keyIterator = e.getUserDatumKeyIterator();
					while(keyIterator.hasNext())
					{
						String key = keyIterator.next();
						edge.set(key, e.getUserDatum(key).toString());
					}
				}
			}
		}
	}




	
	
}
