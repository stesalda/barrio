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

public class Container extends ExplorableElement implements IContainer {

	private ContainerClassification classification = null;
	private Collection<INamespace> namespaces = new ArrayList<INamespace>();
	private IContainer parentContainer = null;
	private Collection<IContainer> subContainers = new ArrayList<IContainer>();

	public boolean acceptContainers(IContainersVisitor v) {
		return false;
	}

	public boolean acceptNamespaces(INamespacesVisitor v) {
		return false;
	}

	public ContainerClassification getClassification() {
		return classification;
	}

	public void setClassification(ContainerClassification classification) {
		this.classification = classification;
	}

	public Collection<INamespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Collection<INamespace> namespaces) {
		this.namespaces = namespaces;
	}

	public IContainer getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(IContainer parentContainer) {
		this.parentContainer = parentContainer;
	}

	public Collection<IContainer> getSubContainers() {
		return subContainers;
	}

	public void setSubContainers(Collection<IContainer> subContainers) {
		this.subContainers = subContainers;
	}


}
