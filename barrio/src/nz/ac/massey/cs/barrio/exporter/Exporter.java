package nz.ac.massey.cs.barrio.exporter;

import edu.uci.ics.jung.graph.Graph;

public interface Exporter {

	public void export(Graph initGraph, Graph clusteredGraph, int separation, String folderName);
}
