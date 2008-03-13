package nz.ac.massey.cs.barrio.inputReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class InputReaderJob extends Job{

	protected String filename;
	
	public InputReaderJob() {
		super("Reading XML file");
	}
	
	

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}

}
