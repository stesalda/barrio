package nz.ac.massey.cs.barrio.classifier;

import java.util.List;

import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public interface Classifier {
	
	public void classify(Vertex v, List<ReferenceRule> rules);

}
