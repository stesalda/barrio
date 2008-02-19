package nz.ac.massey.cs.barrio.exporter;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownExporter {

private static List<Exporter> exporters = null;
	
	public static List<Exporter> all() {
		if (exporters==null) {
			 ExtensionFinderUtil<Exporter> extensionFinder = new ExtensionFinderUtil<Exporter>();
			 exporters = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.exporter", "exporterClass");
		}
		return exporters;
	};
}