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

import org.pfsw.odem.DependencyClassification;

/**
 * Expected dependencies can be used to annotate test files, and to verify the extraction tool.
 * The AST parser will look for annotations like this:
 * @ExpectedDependency(kind="use",target="Class4")
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ExpectedDependency {
	private DependencyClassification kind = DependencyClassification.NEEDS;
	private String target = null;
	
	public ExpectedDependency(String kind, String target) {
		super();
		this.target = target;
		if ("uses".equals(kind)) {
			this.kind=DependencyClassification.NEEDS;
		}
		else if ("extends".equals(kind)) {
			this.kind=DependencyClassification.EXTENSION;
		}
		else if ("implements".equals(kind)) {
			this.kind=DependencyClassification.IMPLEMENTATION;
		}
		else {
			System.out.println("Unknown value used for kind key in @ExpectedDependency annotation: "+kind);
		}
	}
	public DependencyClassification getKind() {
		return kind;
	}
	public void setKind(DependencyClassification kind) {
		this.kind = kind;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		final ExpectedDependency other = (ExpectedDependency) obj;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	public ExpectedDependency(DependencyClassification kind, String target) {
		super();
		this.kind = kind;
		this.target = target;
	}
	
	
}
