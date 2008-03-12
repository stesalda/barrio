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

/**
 * Represents references to types.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ClassRef {
	
	private String name = null;
	private String fullName = null;
	private PackageRef owner = null;
	private JavaType type = JavaType.CLASS;
	private String visibility = null;
	private boolean isAbstract = false;
	private boolean isFinal = false;


	public PackageRef getOwner() {
		return owner;
	}

	public void setOwner(PackageRef owner) {
		this.owner = owner;
		owner.getClasses().add(this);
	}

	public String getName() {
		return name;
	}
	
	public String getFullName() {
		if (fullName==null) {
			if (name==null) {
				return null;
			}
			else if (this.getOwner()==null) {
				return null;
			}
			else {
				fullName = this.getOwner().getName()+'.'+name;
			}
		}
		return fullName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "aClassRef["+this.getFullName()+']';
	}

	public JavaType getType() {
		return type;
	}

	public void setType(JavaType type) {
		this.type = type;
	}

	public void test() {
		// nothing to do here
	}

	public boolean hasTestCases() {
		return false;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
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
