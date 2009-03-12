package nz.ac.massey.cs.barrio.inputReader;

import java.io.IOException;

import edu.uci.ics.jung.graph.Graph;


public interface InputReader {

	/**
	 * Builds the instance of the jung.graph.Graph object
	 * @param input the ODEM object or the filepath of the ODEM file 
	 * @return the built graph
	 * @throws UnknownInputException
	 * @throws IOException
	 */
	public Graph read(Object input) throws UnknownInputException, IOException;
}
