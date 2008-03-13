package nz.ac.massey.cs.barrio.inputReader;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownInputReader {

private static List<InputReaderJob> readers = null;
	
	public static List<InputReaderJob> all() {
		if (readers==null) {
			 ExtensionFinderUtil<InputReaderJob> extensionFinder = new ExtensionFinderUtil<InputReaderJob>();
			 readers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.inputReader", "inputReaderClass");
		}
		return readers;
	};
}