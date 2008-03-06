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

public class ExplorationContext extends ExplorationModelObject implements IExplorationContext {

	private Collection<IContainer> containers = new ArrayList<IContainer>();
	private String description = null;
	public Collection<IContainer> getContainers() {
		return containers;
	}
	public void setContainers(Collection<IContainer> containers) {
		this.containers = containers;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExplorationContext other = (ExplorationContext) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}


}
