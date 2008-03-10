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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.pfsw.odem.DependencyClassification;

/**
 * References to types defined in the project (i.e., for which we have the source code).
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class SourceRef extends ClassRef {
	private ICompilationUnit compilationUnit = null;
	
	// tmp variables
	private Collection<String> importedPackages = new ArrayList<String>();
	private Collection<String> importedClasses = new ArrayList<String>();
	// TODO check static imports
	private String superClassName = null;
	private Collection<String> interfaceNames = new ArrayList<String>();
	private Collection<String> typeParameterNames = new ArrayList<String>();
	private Collection<String> usedTypeNames = new HashSet<String>();
	// final relationships
	private Collection<ClassRef> interfaces = new HashSet<ClassRef>();
	private ClassRef superClass = null;
	private Collection<ClassRef> usedClasses = new HashSet<ClassRef>();
	private boolean resolved = false;
	// test cases built from @ExpectedDependency(kind="use",target="MyClass") annotations
	private Collection<ExpectedDependency> expectedDependencies = null;
	
	
	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public Collection<String> getImportedPackages() {
		return importedPackages;
	}

	public Collection<String> getImportedClasses() {
		return importedClasses;
	}
	
	public String toString() {
		return "aSrcRef["+this.getFullName()+']';
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public Collection<String> getInterfaceNames() {
		return interfaceNames;
	}

	public Collection<String> getTypeParameterNames() {
		return typeParameterNames;
	}

	public Collection<String> getUsedTypeNames() {
		return usedTypeNames;
	}

	public ClassRef getSuperClass() {
		return superClass;
	}

	public void setSuperClass(ClassRef superClass) {
		this.superClass = superClass;
	}

	public Collection<ClassRef> getInterfaces() {
		return interfaces;
	}

	public Collection<ClassRef> getUsedClasses() {
		return usedClasses;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
		// rest tmp variables
		importedPackages = null;
		importedClasses = new ArrayList<String>();
		superClassName = null;
		interfaceNames = null;
		typeParameterNames = null;
		usedTypeNames = null;
		
		// copy hashsets into arrays to get rid of nulls
		ArrayList<ClassRef> list = new ArrayList();
		for (ClassRef c:this.usedClasses) {
			if (c!=null)
				list.add(c);
		}
		usedClasses = list;
		
		list = new ArrayList();
		for (ClassRef c:this.interfaces) {
			if (c!=null)
				list.add(c);
		}
		interfaces = list;
		
		
	}

	public Collection<ExpectedDependency> getExpectedDependencies() {
		if (this.expectedDependencies==null) {
			expectedDependencies = new HashSet<ExpectedDependency>();
		}
		return expectedDependencies;
	}
	public boolean hasTestCases() {
		return expectedDependencies!=null;
	}
	/**
	 * Compare expected and encountered dependencies.
	 */
	public void test() {
		if (!hasTestCases()) 
			return;
		
		// gather expected
		Collection<String> expectedUsedTypes = new HashSet<String>();
		Collection<String> expectedImplementedTypes = new HashSet<String>();
		String expectedSuperClass = null;
		for (ExpectedDependency expected:this.expectedDependencies) {
			if (expected.getKind()==DependencyClassification.NEEDS) {
				expectedUsedTypes.add(expected.getTarget());
			}
			else if (expected.getKind()==DependencyClassification.EXTENSION) {
				expectedSuperClass=expected.getTarget();
			}
			else if (expected.getKind()==DependencyClassification.IMPLEMENTATION) {
				expectedImplementedTypes.add(expected.getTarget());
			}
		}
		// gather computed
		Collection<String> computedUsedTypes = new HashSet<String>();
		Collection<String> computedImplementedTypes = new HashSet<String>();
		String computedSuperClass = this.superClass==null?null:this.superClass.getFullName();
		
		for (ClassRef c:this.usedClasses) {
			if (c!=null && !c.getFullName().equals(this.getFullName())) // remove self reference
				computedUsedTypes.add(c.getFullName());
		}
		for (ClassRef c:this.interfaces) {
			if (c!=null)
				computedImplementedTypes.add(c.getFullName());
		}
		// compare
		boolean ok = true;
		boolean allOk = true;
		if (expectedSuperClass==null)
			ok = computedSuperClass==null;
		else 
			ok = expectedSuperClass.equals(computedSuperClass);
		allOk = allOk && ok;
		if (!ok) {
			System.err.println("test failed in " + this.getFullName());
			System.err.println("expected superclass: " + expectedSuperClass);
			System.err.println("computed superclass: " + computedSuperClass);
		}
		
		ok = expectedImplementedTypes.equals(computedImplementedTypes);		
		if (!ok) {
			System.err.println("test failed in " + this.getFullName());
			System.err.println("expected interfaces: " + expectedImplementedTypes);
			System.err.println("computed interfaces: " + computedImplementedTypes);
		}
		allOk = allOk && ok;
		
		ok = expectedUsedTypes.equals(computedUsedTypes);		
		if (!ok) {
			System.err.println("test failed in " + this.getFullName());
			System.err.println("expected used types: " + expectedUsedTypes);
			System.err.println("computed used types: " + computedUsedTypes);
		}
		allOk = allOk && ok;
		
		if (allOk) {
			System.out.println("tests succeeded for " + this.getFullName());
		}
		
		
	}
}
