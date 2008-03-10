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
 * References packages.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class PackageRef {
	private ContainerRef container = null;
	private String name = null;
	private Collection<ClassRef> classes = new ArrayList<ClassRef>();
	public ContainerRef getContainer() {
		return container;
	}
	public void setContainer(ContainerRef container) {
		this.container = container;
		container.getPackages().add(this);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<ClassRef> getClasses() {
		return classes;
	}
	public int getClassCount() {
		return classes.size();
	}
	public String toString() {
		return "aPackageRef["+this.name+']';
	}
	
	public void test() {
		for (ClassRef c:classes) {
			c.test();
		}
	}
	
	public boolean hasTestCases() {
		for (ClassRef c:classes) {
			if (c.hasTestCases())
				return true;
		}
		return false;
	}
	
	
}
