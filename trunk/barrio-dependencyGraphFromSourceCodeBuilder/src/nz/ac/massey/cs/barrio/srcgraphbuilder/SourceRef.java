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

/**
 * References to types defined in the project (i.e., for which we have the source code).
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class SourceRef extends ClassRef {
	private ICompilationUnit compilationUnit = null;
	
	// tmp variables
	private Collection<String> importedPackages = new ArrayList<String>();
	private Collection<String> importedClasses = new ArrayList<String>();
	private String superClassName = null;
	private Collection<String> interfaceNames = new ArrayList<String>();
	private Collection<String> typeParameterNames = new ArrayList<String>();
	private Collection<String> usedTypeNames = new HashSet<String>();
	// final relationships
	private Collection<ClassRef> interfaces = new ArrayList<ClassRef>();
	private ClassRef superClass = null;
	private Collection<ClassRef> usedClasses = new ArrayList<ClassRef>();
	private boolean resolved = false;
	
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
		
	}
}
