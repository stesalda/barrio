package nz.ac.massey.cs.barrio.srcgraphbuilder;

import java.io.IOException;
import java.io.PrintStream;
/**
 * Copyright 2008 Jens Dietrich Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */


import java.util.Collection;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Exports the dependency graph into ODEM - this is the format used by Manfred's CDA.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class OdemXMLEncoder {
	
	private PrintStream out = null;
	
	public OdemXMLEncoder(PrintStream out) {
		super();
		this.out = out;
	}
	
	public void encode(IJavaProject project,Collection<ContainerRef> containers) throws IOException {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE ODEM PUBLIC \"-//PFSW//DTD ODEM 1.1\" \"http://pfsw.org/ODEM/schema/dtd/odem-1.1.dtd\">"); 
		out.println("<ODEM version=\"1\">");
		out.println("<header>");
		out.println("<created-by>");
		out.println("<exporter version=\"1.1\">");
		out.println(this.getClass().getName());
		out.println("</exporter>");
		out.println("<provider>Jens Dietrich, Massey University</provider>");
		out.println("</created-by>");
		out.println("</header>");
		
		out.print("<context name=\"");
		out.print("Eclipse Project ");
		out.print(project.getElementName());
		out.println("\">");
		
		for (ContainerRef c:containers) {
			// do only export source containers, skip referenced jars etc
			if (c.isSourceContainer()) {
				encode(c);
			}
		}
		
		out.println("</context>");
		out.println("</ODEM>");
	}
	private void encode(ContainerRef container) {
		out.print("<container classification=\"");
		out.print(container.isArchive()?"jar":"dir");
		out.print("\" name=\"");
		out.print(container.getName());
		out.println("\">");
		for (PackageRef p:container.getPackages()) {
			// skip empty packages (this are folders like "com" or "nz" )
			if (p.getClassCount()>0) {
				encode(p);
			}
		}
		out.println("</container>");
	}
	
	private void encode(PackageRef container) {
		out.print("<namespace name=\"");
		out.print(container.getName());
		out.println("\">");
		for (ClassRef c:container.getClasses()) {
			encode((SourceRef)c); 
		}
		out.println("</namespace>");
	}
	
	private void encode(SourceRef src) {
		// <type visibility="default" classification="class" name="junit.extensions.ActiveTestSuite$1">
		out.print("<type name=\"");
		out.print(src.getName());
		
		out.print("\" visibility=\"");
		out.print(src.getVisibility());
		
		out.print("\" classification=\"");
		if (src.getType()==JavaType.ANNOTATION)
			out.print("annotation");
		else if (src.getType()==JavaType.INTERFACE)
			out.print("interface");
		else if (src.getType()==JavaType.ENUMERATION)
			out.print("enum");
		else if (src.getType()==JavaType.CLASS)
			out.print("class");
		else
			out.print("unknown");
		
		out.print("\" isAbstract=\"");
		out.print(src.isAbstract()?"yes":"no");
		
		out.print("\" isFinal=\"");
		out.print(src.isFinal()?"yes":"no");
		
		out.println("\">");
		
		out.print("<dependencies count=\"");
		out.print(getDependencyCount(src));
		out.println("\">");
		
		for (ClassRef target:src.getSuperTypes()) {
			encode(target,"extends");
		}
		for (ClassRef target:src.getInterfaces()) {
			encode(target,"implements");
		}
		for (ClassRef target:src.getUsedClasses()) {
			encode(target,"uses");
		}
		
		out.println("</dependencies>");
		out.println("</type>");
	}
	private void encode(ClassRef target,String classification) {
		if (target==null)
			return;
		
		// <depends-on classification="extends" name="java.util.ArrayList" />
		out.print("<depends-on classification=\"");
		out.print(classification);
		out.print("\" name=\"");
		out.print(target.getFullName());
		out.println("\" />");
	}

	private int getDependencyCount(SourceRef src) {
		int count = src.getSuperTypes().size();
		count = count+src.getInterfaces().size();
		count = count+src.getUsedClasses().size();
		return count;
		
	}

}
