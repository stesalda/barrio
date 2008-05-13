package nz.ac.massey.cs.barrio.srcReader;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownSourceReader {

private static List<SourceReader> readers = null;
	
	public static List<SourceReader> all() {
		if (readers==null) {
			 ExtensionFinderUtil<SourceReader> extensionFinder = new ExtensionFinderUtil<SourceReader>();
			 readers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.srcReader", "sourceReaderClass");
		}
		return readers;
	};
}