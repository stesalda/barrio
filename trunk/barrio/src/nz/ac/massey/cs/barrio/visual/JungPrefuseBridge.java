package nz.ac.massey.cs.barrio.visual;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;

import nz.ac.massey.cs.barrio.constants.BarrioConstants;

import edu.uci.ics.jung.io.GraphMLFile;

public class JungPrefuseBridge {
	
	public prefuse.data.Graph convert(edu.uci.ics.jung.graph.Graph jungGraph)
	{
		prefuse.data.Graph prefuseGraph = null;
		if(jungGraph!=null)
		{
			try {
				GraphMLFile graphMl = new GraphMLFile();
				graphMl.save(jungGraph, new PrintStream(BarrioConstants.PREFUSE_GRAPH_FILE));
				GraphMLReader reader = new GraphMLReader();
				prefuseGraph = reader.readGraph(BarrioConstants.PREFUSE_GRAPH_FILE);
				prefuseGraph.addColumn("class.expression", new LabelExpression("class.jar","class.packageName","class.name"));
				prefuseGraph.addColumn("class.icon", new ImageExpression("class.isInterface", "class.isException", "class.isAbstract", "class.access"));
				
				GraphMLWriter writer = new GraphMLWriter();
				writer.writeGraph(prefuseGraph, BarrioConstants.TEST_GRAPH_FILE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prefuseGraph;
	}
	
}
