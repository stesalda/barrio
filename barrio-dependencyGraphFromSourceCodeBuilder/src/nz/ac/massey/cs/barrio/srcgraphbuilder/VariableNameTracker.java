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

import java.util.*;

/**
 * Utility to keep track of variables defined in the current scope.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */
public class VariableNameTracker {
	
	private List<Set<String>> variables = new ArrayList<Set<String>>();
	private int counter = 0;
	
	public VariableNameTracker() {
		super();
		variables.add(new HashSet<String>());
	}
	public void reset() {
		counter = 0;
		for (Set<String> set:variables) {
			set.clear();
		}
	}
	public void down() {
		counter = counter+1;
		if (variables.size()<=counter) {
			variables.add(new HashSet<String>());
		}
	}
	public void up() {
		variables.get(counter).clear();
		counter = counter-1;
	}
	public void addDefined(String variable) {
		// System.out.println("var defined at level " + counter + " : " + variable);
		variables.get(counter).add(variable);
	}
	public boolean isDefined(String variable) {
		// first check for pseudo variables
		if ("this".equals(variable) || "super".equals(variable)) {
			return true;
		}
		// then check for defined variables
		for (int i=0;i<counter+1;i++) {
			if (variables.get(counter).contains(variable))
				return true;
		}
		return false;
	}
	

}
