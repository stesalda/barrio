package nz.ac.massey.cs.barrio.filters;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownNodeFilters {
	
	private static List<NodeFilter> nodeFilters = null;
	
	public static List<NodeFilter> all() {
		if (nodeFilters==null) {
			 ExtensionFinderUtil<NodeFilter> extensionFinder = new ExtensionFinderUtil<NodeFilter>();
			 nodeFilters = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.nodeFilter", "nodeFilterClass");
		}
		return nodeFilters;
	};

}
