package nz.ac.massey.cs.barrio.inputReader;

import java.io.IOException;

import edu.uci.ics.jung.graph.Graph;


public interface InputReader {

	public Graph read(Object input) throws UnknownInputException, IOException;
}
