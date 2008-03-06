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

public class Namespace extends ExplorableElement implements INamespace {

	public boolean acceptNamespaces(INamespacesVisitor arg0) {
		return false;
	}

	private Collection<IType> types = new ArrayList<IType>();

	public Collection<IType> getTypes() {
		return types;
	}

	public void setTypes(Collection<IType> types) {
		this.types = types;
	}

}
