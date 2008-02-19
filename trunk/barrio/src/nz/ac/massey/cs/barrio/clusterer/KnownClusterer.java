package nz.ac.massey.cs.barrio.clusterer;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownClusterer {

private static List<Clusterer> clusterers = null;
	
	public static List<Clusterer> all() {
		if (clusterers==null) {
			 ExtensionFinderUtil<Clusterer> extensionFinder = new ExtensionFinderUtil<Clusterer>();
			 clusterers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.clusterer", "clustererClass");
		}
		return clusterers;
	};
}