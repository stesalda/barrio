package nz.ac.massey.cs.barrio.nonPublicFilter;

import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.filters.NodeFilter;

public class NonPublicFilter extends NodeFilter {

	public boolean acceptVertex(Vertex v) {
		String access = v.getUserDatum("class.access").toString();
		if (access.equals("public")) return true;
		return false;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Non public types";
	}

}
