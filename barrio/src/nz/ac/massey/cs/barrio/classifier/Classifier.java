package nz.ac.massey.cs.barrio.classifier;

import java.util.List;

import edu.uci.ics.jung.graph.Vertex;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public interface Classifier {
	
	public List<String> classify(Vertex v, List<ReferenceRule> rules);

}
