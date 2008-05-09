/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


package nz.ac.massey.cs.barrio.srcgraphbuilder;


import java.io.StringWriter;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Extracts the dependency graph, and exports it to a memory buffer using the ODEM-XML format.
 * The memory buffer can be accessed using getBuffer().
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ExtractDependencyGraph2OdemInMemoryJob extends ExtractDependencyGraphJob {
	
	private String buffer = null;
	
	public ExtractDependencyGraph2OdemInMemoryJob(IJavaProject project) {
		super(project);
	}

	protected void exportGraph() {
		StringWriter out = new StringWriter();
		try {
			new OdemXMLEncoder(out).encode(this.project,this.containers);
			this.buffer = out.toString();
		}
		catch (Exception x) {
			// TODO better exception handling (return IStatus)
			x.printStackTrace();
		}
		finally {
			try {
				out.close();
			}
			catch (Exception x) {}
		}

	}

	public String getBuffer() {
		return buffer;
	}
}
