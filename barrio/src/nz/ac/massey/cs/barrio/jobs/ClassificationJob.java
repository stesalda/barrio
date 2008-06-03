package nz.ac.massey.cs.barrio.jobs;

import java.util.Iterator;
import java.util.List;

import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.classifier.KnownClassifier;
import nz.ac.massey.cs.barrio.preferences.RuleStorage;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

public class ClassificationJob extends Job{
	
	private Graph graph;

	public ClassificationJob(Graph graph) {
		super("Classifying nodes");
		this.graph = graph;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		Classifier classifier = KnownClassifier.all().get(0);
		List<ReferenceRule> rules = getRules();
		
		int SCALE = graph.getVertices().size();
		monitor.beginTask("", SCALE);
		Iterator<Object> iter = graph.getVertices().iterator();
		while(iter.hasNext())
		{
			Vertex v = (Vertex) iter.next();
			classifier.classify(v, rules);
			monitor.worked(1);
//			if(canceled) return Status.CANCEL_STATUS;
//			return Status.OK_STATUS;
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	private List<ReferenceRule> getRules() 
	{
		RuleStorage storage = new RuleStorage(null);
		return storage.load();
	}

}
