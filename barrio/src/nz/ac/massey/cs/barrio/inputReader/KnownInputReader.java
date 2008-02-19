package nz.ac.massey.cs.barrio.inputReader;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownInputReader {

private static List<InputReader> readers = null;
	
	public static List<InputReader> all() {
		if (readers==null) {
			 ExtensionFinderUtil<InputReader> extensionFinder = new ExtensionFinderUtil<InputReader>();
			 readers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.inputReader", "inputReaderClass");
		}
		return readers;
	};
}