package nz.ac.massey.cs.barrio.filters;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownEdgeFilters {

private static List<EdgeFilter> edgeFilters = null;
	
	public static List<EdgeFilter> all() {
		if (edgeFilters==null) {
			 ExtensionFinderUtil<EdgeFilter> extensionFinder = new ExtensionFinderUtil<EdgeFilter>();
			 edgeFilters = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.edgeFilter", "edgeFilterClass");
		}
		return edgeFilters;
	};
}
