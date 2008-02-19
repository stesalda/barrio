package nz.ac.massey.cs.barrio.abstractFilter;

import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.filters.NodeFilter;


public class AbstractFilter extends NodeFilter {

	

	public String getName() {
		// TODO Auto-generated method stub
		return "Abstract classes";
	}

	public boolean acceptVertex(Vertex v) {
		String abstractness = v.getUserDatum("class.isAbstract").toString();
		if (abstractness.equals("true")) return false;
		return true;
	}

}
