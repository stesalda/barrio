package nz.ac.massey.cs.barrio.exporter;

import java.util.List;

import org.eclipse.swt.widgets.Tree;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;



public interface Exporter {

	public void export(Graph initGrapg, Graph clusteredGraph, int sepLevel, 
						List<String> filters, List<Edge> removed, Tree treePwMC, Tree treeCwMP);
}
