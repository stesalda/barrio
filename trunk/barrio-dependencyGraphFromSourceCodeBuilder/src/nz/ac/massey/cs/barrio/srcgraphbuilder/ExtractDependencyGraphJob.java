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

import java.util.*;

import nz.ac.massey.cs.barrio.srcgraphbuilder.odem.ExplorationContext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 * Job used to extract the dependency graph from a Java project.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public abstract class ExtractDependencyGraphJob  extends Job {

	protected IJavaProject project = null;
	protected Collection<ContainerRef> containers = new ArrayList<ContainerRef> ();
	protected Map<String,Collection<ClassRef>> classesByName = new HashMap<String,Collection<ClassRef>>();
	protected Map<String,ClassRef> classesByFullName = new HashMap<String,ClassRef>();
	protected Map<String,ClassRef> coreJavaClassesByName = new HashMap<String,ClassRef>();

	// root - this is the object we create
	private ExplorationContext context = null;
	
	public ExtractDependencyGraphJob(IJavaProject project) {
		super("Loading graph dependency extractor...");
		setUser(true); // this is a user-spawned job
		this.project = project;
		
		this.context = new ExplorationContext();
		this.context.setName(project.getElementName());
		this.context.setDescription("Eclipse Java project "+project.getElementName());
	}

	public IStatus createError(String message, Exception e) {
		return new Status(Status.ERROR, Activator.PLUGIN_ID, Status.ERROR, message, e);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			int SCALE = 100000;
			monitor.beginTask("extracting dependency graph from Java project", SCALE);
			monitor.subTask("gathering classes");
			try {
				for (IJavaElement e:project.getChildren()) {
					//System.out.println("child"+e+" - "+e.getClass());
					if (e instanceof IPackageFragmentRoot ) {
						// sources
						IPackageFragmentRoot  pfr  = (IPackageFragmentRoot )e;
						if (pfr.getKind()==pfr.K_SOURCE) {
							ContainerRef c = new ContainerRef();
							c.setSourceContainer(true);
							c.setName(pfr.getElementName());
							c.setArchive(pfr.isArchive());
							this.containers.add(c);
							gatherSources(c,pfr);
							// System.out.println("Added container " + c.getName() + " - contains " + c.getClassCount() + " sources");
						}
						else if (pfr.getKind()==pfr.K_BINARY) {
							ContainerRef c = new ContainerRef();
							c.setSourceContainer(false);
							c.setName(pfr.getElementName());
							this.containers.add(c);
							gatherBinaries(c,pfr);
							// System.out.println("Added container " + c.getName() + " - contains " + c.getClassCount() + " bin classes");
						}
						else {
							System.err.println("Unhandled Java element type: " + e);
						}
					}
				}
			}			
			catch (Exception x) {
				x.printStackTrace();
			}
			// STEP2 - parse sources
			monitor.worked((int)(0.08*SCALE));
			monitor.subTask("parsing classes");
			List<PackageRef> packages = new ArrayList<PackageRef>();
			for (ContainerRef c:containers) {
				if (c.isSourceContainer()) {
					for (PackageRef p:c.getPackages()) {
						if (p.getClassCount()>0)
							packages.add(p);
					}
				}
			}
			monitor.internalWorked((int)(0.1*SCALE));
			int delta = (int)(SCALE*0.5 / packages.size());
			for (PackageRef p:packages) {
				monitor.subTask("parsing classes in package " + p.getName());

				monitor.worked(delta);
				for (ClassRef c:p.getClasses()) {
					SourceRef src = (SourceRef)c;
					ExtractTypeInfoVisitor v = new ExtractTypeInfoVisitor(src);
					ASTParser parser = ASTParser.newParser(AST.JLS3);
					parser.setSource(src.getCompilationUnit());
					parser.setResolveBindings(false);
					ASTNode node = parser.createAST(null);
					node.accept(v);
				}
			}
			
			// STEP3 - resolve references
			
			for (PackageRef p:packages) {
				monitor.subTask("resolving references " + p.getName());
				monitor.worked(delta);
				for (ClassRef c:p.getClasses()) {
					SourceRef src = (SourceRef)c;
					resolveReferences(src);
				}
			}			
			
			// STEP5: testing
			for (ContainerRef c:containers) {
				if (c.hasTestCases())
						c.test();
			}
			
			// STEP6 export graph
			monitor.subTask("exporting dependency graph");
			exportGraph();
			
			monitor.done();
			return Status.OK_STATUS;
		} finally {

		}
	}

	protected abstract void exportGraph();

	private void resolveReferences(SourceRef src) {
		ClassRef target = null;
		for (String t:src.getInterfaceNames()) {
			target = this.resolveReference(src,t);
			if (target!=src)
				src.getInterfaces().add(target);
		}
		for (String t:src.getUsedTypeNames()) {
			target = this.resolveReference(src,t);
			if (target!=src)
				src.getUsedClasses().add(target);
		}
		for (String t:src.getSuperTypeNames()) {
			target = this.resolveReference(src,t);
			if (target!=src)
				src.getSuperTypes().add(target);
		}
		
		src.setResolved(true);
		
	}
	/**
	 * Compiler tries to resolve a name in the following order:
	 * <ol>
	 * <li>inner classes</li>
	 * <li>imported classes</li>
	 * <li>classes in same package</li>
	 * <li>classes in imported packages</li>
	 * <li>classes in java.lang</li>
	 * </ol>
	 */
	private ClassRef resolveReference(SourceRef src,String type) {
		
		
		ClassRef c = null;
		boolean isFullName = type.indexOf('.')>-1;
		
		// TODO inner classes
		
		// fully qualified classes
		c = this.classesByFullName.get(type);
		
		// imported classes
		if (c==null) {
			for (String i:src.getImportedClasses()) {
				if (i.endsWith(type)) {
					c = this.classesByFullName.get(i);
					break;
				}
			}
		}
		
		// classes in this package
		if (!isFullName && c==null) {
			for (ClassRef t:src.getOwner().getClasses()) {
				if (t.getName().equals(type)) {
					c = t;
				}
			}
		}
		
		// imported packages
		if (c==null) {
			for (String i:src.getImportedPackages()) {
				c = this.classesByFullName.get(i+'.'+type);
				if (c!=null) break;
			}			
		}
		
		// java.lang classes
		if (!isFullName && c==null) {
			c = this.coreJavaClassesByName.get(type);
		}
		
		if (c==null) {
			//System.err.println("cannot resolve type reference " + type + " in " + src);
		}	
		/*
		else {
			System.out.println("resolved type reference " + type + " in " + src + " to " + c);
		}
		*/
		return c;
	}

	private void gatherSources(ContainerRef c,IPackageFragmentRoot prf) {
		try {
			for (IJavaElement e:prf.getChildren()) {
				if (e instanceof IPackageFragment){
					PackageRef p = new PackageRef();
					p.setContainer(c);
					p.setName(e.getElementName());
					gatherSources(p,(IPackageFragment)e);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}		
	}
	
	private void gatherBinaries(ContainerRef c,IPackageFragmentRoot prf) {
		try {
			for (IJavaElement e:prf.getChildren()) {
				if (e instanceof IPackageFragment){
					PackageRef p = new PackageRef();
					p.setContainer(c);
					p.setName(e.getElementName());
					gatherBinaries(p,(IPackageFragment)e);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}		
	}
	
	private void gatherSources(PackageRef p,IPackageFragment pf) {
		// filter used for debugging
		// if (!p.getName().endsWith("inheritance")) return;
		
		boolean isJavaLang = "java.lang".equals(p.getName());
		try {
			for (IJavaElement e:pf.getChildren()) {
				if (e instanceof ICompilationUnit ) {
					String n = e.getElementName();
					n = n.endsWith(".java")?n.substring(0,n.length()-5):n;
					n = normalizeClassName(n);
					SourceRef c = new SourceRef();
					c.setName(n);
					c.setOwner(p);
					c.setCompilationUnit((ICompilationUnit)e);
					if (isJavaLang) {
						this.coreJavaClassesByName.put(n,c);
					}
					registerType(c);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}		
	}
	
	private void gatherBinaries(PackageRef p,IPackageFragment pf) {
		boolean isJavaLang = "java.lang".equals(p.getName());
		try {
			for (IJavaElement e:pf.getChildren()) {
				if (e instanceof IClassFile ) {
					String n = e.getElementName();
					n = n.endsWith(".class")?n.substring(0,n.length()-6):n;
					n = normalizeClassName(n);
					ClassRef c = new ClassRef();
					c.setName(n);
					c.setOwner(p);
					if (isJavaLang) {
						this.coreJavaClassesByName.put(n,c);
					}
					registerType(c);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}		
	}
	// TODO return null for anonymous classes
	private String normalizeClassName (String n) {
		return n.replace('$','.');
	}
	private void registerType(ClassRef c) {
		// there should be only one
		this.classesByFullName.put(c.getFullName(),c);
		// but more than one for a name
		Collection<ClassRef> types = this.classesByName.get(c.getName());
		if (types==null) {
			types = new ArrayList<ClassRef>();
			this.classesByName.put(c.getName(),types);
		}
		types.add(c);
	}
}
