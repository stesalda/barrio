package nz.ac.massey.cs.barrio.srcReader;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

public interface SourceReader {
	
	public Job getProjectReadingJob(List<IJavaProject> projects);

	public String getBuffer();
}
