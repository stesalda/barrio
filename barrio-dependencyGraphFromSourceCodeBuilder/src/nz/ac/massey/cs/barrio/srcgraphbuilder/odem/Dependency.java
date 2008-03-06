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

import org.pfsw.odem.*;

/**
 * Simple PLOJO based implementation of the ODEM API.
 * @see http://www.programmers-friend.org/
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class Dependency<S extends IExplorableElement,T extends IExplorableElement> extends ObjectProperties implements IDependency<S,T> {

	private DependencyClassification dependencyClassification = null;
	private S sourceElement = null;
	private T targetElement = null;
	public DependencyClassification getDependencyClassification() {
		return dependencyClassification;
	}
	public void setDependencyClassification(
			DependencyClassification dependencyClassification) {
		this.dependencyClassification = dependencyClassification;
	}
	public S getSourceElement() {
		return sourceElement;
	}
	public void setSourceElement(S sourceElement) {
		this.sourceElement = sourceElement;
	}
	public T getTargetElement() {
		return targetElement;
	}
	public void setTargetElement(T targetElement) {
		this.targetElement = targetElement;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((dependencyClassification == null) ? 0
						: dependencyClassification.hashCode());
		result = prime * result
				+ ((sourceElement == null) ? 0 : sourceElement.hashCode());
		result = prime * result
				+ ((targetElement == null) ? 0 : targetElement.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Dependency other = (Dependency) obj;
		if (dependencyClassification == null) {
			if (other.dependencyClassification != null)
				return false;
		} else if (!dependencyClassification
				.equals(other.dependencyClassification))
			return false;
		if (sourceElement == null) {
			if (other.sourceElement != null)
				return false;
		} else if (!sourceElement.equals(other.sourceElement))
			return false;
		if (targetElement == null) {
			if (other.targetElement != null)
				return false;
		} else if (!targetElement.equals(other.targetElement))
			return false;
		return true;
	}


}
