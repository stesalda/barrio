package nz.ac.massey.cs.barrio.extendsFilter;

import edu.uci.ics.jung.graph.Edge;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;

public class ExtendsFilter extends EdgeFilter {

	public boolean acceptEdge(Edge e) {
		String type = e.getUserDatum("relationship.type").toString();
		if (type.equals("extends")) return false;
		return true;
	}

	public String getName() {
		return "Extends";
	}

}
