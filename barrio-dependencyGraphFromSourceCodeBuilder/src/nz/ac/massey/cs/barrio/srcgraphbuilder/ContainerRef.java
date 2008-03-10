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

/**
 * Represents containers (jars, folders and othert classpath components).
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ContainerRef {
	private boolean isSourceContainer = false;
	private String name = null;
	private Collection<PackageRef> packages = new ArrayList<PackageRef>();
	public boolean isSourceContainer() {
		return isSourceContainer;
	}
	public void setSourceContainer(boolean isSourceContainer) {
		this.isSourceContainer = isSourceContainer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<PackageRef> getPackages() {
		return packages;
	}
	
	public int getClassCount() {
		int s = 0;
		for (PackageRef p:packages) {
			s = s+p.getClassCount();
		}
		return s;
	}
	public String toString() {
		return "aContainerRef["+this.name+']';
	}
	
	public void test() {
		for (PackageRef p:packages) {
			p.test();
		}
	}
	
	public boolean hasTestCases() {
		if (!this.isSourceContainer())
			return false;
		for (PackageRef p:packages) {
			if (p.hasTestCases())
				return true;
		}
		return false;
	}
	
}
