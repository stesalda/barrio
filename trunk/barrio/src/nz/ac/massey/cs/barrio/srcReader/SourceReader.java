package nz.ac.massey.cs.barrio.srcReader;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

public interface SourceReader {

	/**
	 * Creates an EPF job that uses a list of the Eclipse Java projects. The job uses only first item from the list
	 * in order to create an ODEM object in memory.
	 * @param projects the list of Eclipse Java projects
	 * @return job that creates ODEM object in memory 
	 */
	public Job getProjectReadingJob(List<IJavaProject> projects);
	
	/**
	 * Creates an EPF job that uses a list of the Eclipse Java projects and an output filepath. 
	 * The job uses only first item from the list in order to create an ODEM file in the filepath.
	 * @param projects the list of Eclipse Java projects
	 * @param filepath the desired ODEM filepath 
	 * @return job that creates ODEM file
	 */
	public Job getProjectReadingJob(List<IJavaProject> projects, String filepath);

	/**
	 * Returns byte array of the ODEM object
	 * @return byte array to represent ODEM object
	 */
	public byte[] getBuffer();
}
