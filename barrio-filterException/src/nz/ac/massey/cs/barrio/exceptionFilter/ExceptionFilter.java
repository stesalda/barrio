package nz.ac.massey.cs.barrio.exceptionFilter;

import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.filters.NodeFilter;

public class ExceptionFilter extends NodeFilter {

	public boolean acceptVertex(Vertex v) {
		String isException = v.getUserDatum("class.isException").toString();
		if (isException.equals("true")) return false;
		return true;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Exceptions";
	}

}
