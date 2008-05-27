package nz.ac.massey.cs.barrio.classifier;

import java.util.List;

import nz.ac.massey.cs.barrio.extensionFinder.ExtensionFinderUtil;

public class KnownClassifier {

private static List<Classifier> classifiers = null;
	
	public static List<Classifier> all() {
		if (classifiers==null) {
			 ExtensionFinderUtil<Classifier> extensionFinder = new ExtensionFinderUtil<Classifier>();
			 classifiers = extensionFinder.findExtensionObjects("nz.ac.massey.cs.barrio.classifier", "classifierClass");
		}
		return classifiers;
	};
}