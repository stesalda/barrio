package nz.ac.massey.cs.barrio.jobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.classifier.KnownClassifier;
import nz.ac.massey.cs.barrio.clusterer.Clusterer;
import nz.ac.massey.cs.barrio.clusterer.KnownClusterer;
import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.inputReader.InputReader;
import nz.ac.massey.cs.barrio.inputReader.KnownInputReader;
import nz.ac.massey.cs.barrio.inputReader.UnknownInputException;
import nz.ac.massey.cs.barrio.preferences.RuleStorage;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;

public class MainJob extends Job {

	private File folder;
	private String filename = "";
	private List<Edge> removedEdges;
	
	public MainJob(File folder) {
		super("Progress");
		this.folder = folder;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		File[] files = folder.listFiles();
		int SCALE = files.length;
		monitor.beginTask("Processing", SCALE);
		
		for(int i=0; i<files.length; i++)
		{
			if(!files[i].getName().endsWith(".odem")) continue;
			removedEdges = new ArrayList<Edge>();
			monitor.subTask("Analysing file: "+files[i].getName());
			filename = files[i].getName();
			Graph graph = buildGraph(files[i]);
			classifyGraph(graph);
			clusterGraph(graph, 0);
			clusterGraph(graph, 1);
			clusterGraph(graph, 2);
			clusterGraph(graph, 3);			
			monitor.worked(1);
		}		
		
		monitor.done();		
		return Status.OK_STATUS;
	}
	
	private Graph buildGraph(File file)
	{
		Graph graph = null;
		
		List<InputReader> readers = KnownInputReader.all();
		InputReader reader = readers.get(0);
		
		try {
			graph = reader.read(file);
		} catch (UnknownInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return graph;
	}
	
	private void classifyGraph(Graph graph)
	{
		Classifier classifier = KnownClassifier.all().get(0);
		RuleStorage storage = new RuleStorage(null);
		List<ReferenceRule> rules = storage.load();
		
		Iterator<Object> iter = graph.getVertices().iterator();
		while(iter.hasNext())
		{
			Vertex v = (Vertex) iter.next();
			classifier.classify(v, rules);
		}
	}
	
	private void clusterGraph(Graph graph, int separation)
	{
		if(graph.numVertices()<10) return;
				
		List<Clusterer> clusterers = KnownClusterer.all();
		Clusterer clusterer = clusterers.get(0);
		HashMap<String,Integer> clusts = clusterer.nameClusters(graph);
		int numClusters = clusts.size();

		String resultFolder = "/random";
		if(graph.numVertices()>=10 && graph.numVertices()<50)  resultFolder = "/size10to50";
		if(graph.numVertices()>=50 && graph.numVertices()<200)  resultFolder = "/size50to200";
		if(graph.numVertices()>=200 && graph.numVertices()<600)  resultFolder = "/size200to600";
		if(graph.numVertices()>=600)  resultFolder = "/size600plus";
		
		if(separation==0)
		{
			printGraph(graph, separation, removedEdges, folder.getAbsolutePath()+resultFolder);
			return;
		}
		
		int sep = 1;
		while(graph.numEdges()>0)
		{
			clusterer.cluster(graph);
			List<Edge> newRemovedEdges = clusterer.getEdgesRemoved();
			for(Edge e:newRemovedEdges) e.setUserDatum("relationship.separation", sep, UserData.SHARED);
			removedEdges.addAll(newRemovedEdges);
			int newNumClusters = clusterer.nameClusters(graph).size();
			sep++;
			if(numClusters!= newNumClusters) break;
		}		
		clusterer.nameClusters(graph);		
		printGraph(graph, separation, removedEdges, folder.getAbsolutePath()+resultFolder);
	}

	private void printGraph(Graph graph, int separation, List<Edge> removedEdges, String folderName) {
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		String name = folderName+"/"+this.filename+"-sep"+separation+".xml";
		e.export(graph, separation, removedEdges, new ArrayList<String>(), name);
		
	}

}
