package nz.ac.massey.cs.barrio.motifFinder;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownMotifs {

private static List<Motif> motifs = null;
	
	public static List<Motif> all() {
		if (motifs==null) {
			 ExtensionFinderUtil<Motif> extensionFinder = new ExtensionFinderUtil<Motif>();
			 motifs = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.motifFinder", "motifFinderClass");
		}
		return motifs;
	};
}