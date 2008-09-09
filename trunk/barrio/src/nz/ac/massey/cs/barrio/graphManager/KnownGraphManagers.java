package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownGraphManagers {

private static List<GraphManager> graphManagers = null;
	
	public static List<GraphManager> all() {
		if (graphManagers==null) {
			 ExtensionFinderUtil<GraphManager> extensionFinder = new ExtensionFinderUtil<GraphManager>();
			 graphManagers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.graphManager", "graphManagerClass");
		}
		return graphManagers;
	};
}
