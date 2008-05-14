package nz.ac.massey.cs.barrio.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class CustomJob extends Job {

	public CustomJob(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public byte[] getBuffer()
	{
		return null;
	}

}
