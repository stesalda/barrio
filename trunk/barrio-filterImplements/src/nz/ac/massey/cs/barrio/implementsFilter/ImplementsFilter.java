package nz.ac.massey.cs.barrio.implementsFilter;

import edu.uci.ics.jung.graph.Edge;
import nz.ac.massey.cs.barrio.filters.EdgeFilter;

public class ImplementsFilter extends EdgeFilter {

	public boolean acceptEdge(Edge e) {
		String type = e.getUserDatum("relationship.type").toString();
		if (type.equals("implements")) return false;
		return true;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Implements";
	}

}
