package nz.ac.massey.cs.barrio.dialogs;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import edu.uci.ics.jung.graph.Graph;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;

public class SrcCodeReader implements InputReader {

	public Graph read(Object input) throws UnknownInputException, IOException {

		if(!(input instanceof List)) throw new UnknownInputException();
		List projects = (List) input;
		
		for(Object obj:projects)
		{
			if(!(obj instanceof IJavaProject)) throw new UnknownInputException();
			IJavaProject project = (IJavaProject) obj;
			
			
		}
		
		return null;
	}

}
