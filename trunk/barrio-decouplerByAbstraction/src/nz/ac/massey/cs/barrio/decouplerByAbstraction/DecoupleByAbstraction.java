package nz.ac.massey.cs.barrio.decouplerByAbstraction;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.motifFinder.Motif;
import nz.ac.massey.cs.barrio.motifFinder.MotifInstance;

public class DecoupleByAbstraction implements Motif {

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<MotifInstance> findAll(Graph graph) {
		List<MotifInstance> instances = new ArrayList<MotifInstance>();
		List<Vertex> possibleAbstractProviders = new ArrayList<Vertex>();
		List<Vertex> possibleConcreteProviders = new ArrayList<Vertex>();
		List<Vertex> possibleClients = new ArrayList<Vertex>();
		
		
		Iterator<Vertex> vertexIterator = graph.getVertices().iterator();
		while(vertexIterator.hasNext())
		{
			Vertex v = vertexIterator.next();
			if(v.getUserDatum("class.isAbstract").equals("true")) possibleAbstractProviders.add(v);
			else
			{
				possibleConcreteProviders.add(v);
				possibleClients.add(v);
			}
			
		}
		PrintStream out = null;
		try {
			out = new PrintStream("C:/Users/Slava/Desktop/motifs.txt");
			out.println("cl = client");
			out.println("AP = Abstract Provider");
			out.println("CP = Concrete Provider");
			out.println();
			out.println();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		DijkstraShortestPath pathFinder = new DijkstraShortestPath(graph);
		for(Vertex client:possibleClients)
		{
			for(Vertex abstractProvider: possibleAbstractProviders)
			{
				boolean isValidPath1 = true;
				List<Edge> path1 = pathFinder.getPath(client, abstractProvider);
				if(path1.size()<1) continue;
				for(Edge e:path1)
				{
					if(!e.getUserDatum("relationship.type").equals("uses")) 
					{
						isValidPath1 = false;
						break;
					}
				}
				if(!isValidPath1) continue;
				
				for(Vertex concreteProvider: possibleConcreteProviders)
				{
					boolean isValidPath = true;
					List<Edge> path2 = pathFinder.getPath(concreteProvider, abstractProvider);
					if(path2.size()<1) continue;
					for(Edge e:path2)
					{
						if(e.getUserDatum("relationship.type").equals("uses"))
						{
							isValidPath = false;
							break;
						}
					}
					if(!isValidPath) continue;
					
					List<Edge> path3 = pathFinder.getPath(client, concreteProvider);
					if(path3.size()<1) continue;
					for(Edge e:path3)
					{
						if(!e.getUserDatum("relationship.type").equals("uses"))
						{
							isValidPath = false;
							break;
						}
					}
					if(!isValidPath) continue;					

					HashMap instanceMap = new HashMap<String, Vertex>();
					instanceMap.put("Client", client);
					instanceMap.put("AbstractProvider", abstractProvider);
					instanceMap.put("ConcreteProvider", concreteProvider);
					DBAInstance instance = new DBAInstance(instanceMap);
					instances.add(instance);
					
					if(out!=null) 
					{
						out.print("cl->AP = ");
						print(out, path1);
						out.print("CP->AP = ");
						print(out, path2);
						out.print("cl->CP = ");
						print(out, path3);
						out.println();
					}
				}
			}
		}
		
		if(out!=null){
			out.println("end");
			out.close();
		}
		return instances.iterator();
	}
	
	

	private void print(PrintStream out, List<Edge> path) 
	{
		Vertex v = (Vertex) path.get(0).getEndpoints().getFirst();
		out.print(v.getUserDatum("class.name"));
		for(Edge e:path)
		{
			out.print(" -(");
			out.print(e.getUserDatum("relationship.type"));
			out.print(")-> ");
			out.print(((Vertex)e.getEndpoints().getSecond()).getUserDatum("class.name"));
		}
		out.println();
	}



	@Override
	public List<String> getRoleNames() {
		List<String> list = new ArrayList<String>();
		list.add("Client");
		list.add("AbstractProvider");
		list.add("ConcreteProvider");
		return list;
	}

	
	private class DBAInstance implements MotifInstance
	{
		HashMap<String, Vertex> instanceMap = new HashMap<String, Vertex>();

		public DBAInstance(HashMap<String, Vertex> instanceMap)
		{
			this.instanceMap = instanceMap;
		}
		
		@Override
		public Vertex getInstance(String roleName) {
			return instanceMap.get(roleName);
		}
		
	}
}
