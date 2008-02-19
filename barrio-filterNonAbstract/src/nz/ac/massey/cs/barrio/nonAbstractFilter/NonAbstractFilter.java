package nz.ac.massey.cs.barrio.nonAbstractFilter;

import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.filters.NodeFilter;

public class NonAbstractFilter extends NodeFilter {

	public boolean acceptVertex(Vertex v) {
		String abstractness = v.getUserDatum("class.isAbstract").toString();
		if (abstractness.equals("true")) return true;
		return false;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Non-abstract classes";
	}

}
