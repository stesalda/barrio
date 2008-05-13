package nz.ac.massey.cs.barrio.srcReader;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

import nz.ac.massey.cs.barrio.srcReader.SourceReader;
import nz.ac.massey.cs.barrio.srcgraphbuilder.ExtractDependencyGraph2OdemFileJob;
import nz.ac.massey.cs.barrio.srcgraphbuilder.ExtractDependencyGraph2OdemInMemoryJob;

public class SourceCodeReader implements SourceReader{

	private String  buffer; 
	@Override
	public ExtractDependencyGraph2OdemInMemoryJob getProjectReadingJob(List<IJavaProject> projects) 
	{
		if(projects.size()<1) return null;
		IJavaProject project = projects.get(0);
		ExtractDependencyGraph2OdemInMemoryJob job = new ExtractDependencyGraph2OdemInMemoryJob(project);
		buffer = job.getBuffer();
		return job;
	}

	@Override
	public String getBuffer() {
		return buffer;
	}

}
