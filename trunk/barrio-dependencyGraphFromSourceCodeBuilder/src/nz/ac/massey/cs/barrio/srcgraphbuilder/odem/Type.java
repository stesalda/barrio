/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package nz.ac.massey.cs.barrio.srcgraphbuilder.odem;

import java.util.*;
import org.pfsw.odem.*;

/**
 * Simple PLOJO based implementation of the ODEM API.
 * @see http://www.programmers-friend.org/
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class Type extends ExplorableElement implements IType {

	private TypeClassification classification = null;
	private DependencySet<IType, IType> dependencies = null;
	private Collection<IType> directReferredTypes = new HashSet<IType>();
	private INamespace namespace = null;
	private String unqualifiedName = null;
	private Visibility visibility = null;
	private boolean isAbstract = false;
	private boolean isFinal = false;

	// TODO
	public boolean isDirectExtensionOf(IType t) {
		return false;
	}
	// TODO
	public boolean isDirectImplementorOf(IType arg0) {
		return false;
	}

	public TypeClassification getClassification() {
		return classification;
	}
	public void setClassification(TypeClassification classification) {
		this.classification = classification;
	}
	public DependencySet<IType, IType> getDependencies() {
		return dependencies;
	}
	public void setDependencies(DependencySet<IType, IType> dependencies) {
		this.dependencies = dependencies;
	}
	public Collection<IType> getDirectReferredTypes() {
		return directReferredTypes;
	}
	public void setDirectReferredTypes(Collection<IType> directReferredTypes) {
		this.directReferredTypes = directReferredTypes;
	}
	public INamespace getNamespace() {
		return namespace;
	}
	public void setNamespace(INamespace namespace) {
		this.namespace = namespace;
	}
	public String getUnqualifiedName() {
		return unqualifiedName;
	}
	public void setUnqualifiedName(String unqualifiedName) {
		this.unqualifiedName = unqualifiedName;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}


}
