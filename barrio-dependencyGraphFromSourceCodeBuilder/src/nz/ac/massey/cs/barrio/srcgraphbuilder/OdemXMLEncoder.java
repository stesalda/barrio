package nz.ac.massey.cs.barrio.srcgraphbuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
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
	
	private StringWriter out = null;
	
	public OdemXMLEncoder(StringWriter out) {
		super();
		this.out = out;
	}
	
	public void encode(IJavaProject project,Collection<ContainerRef> containers) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.write("<!DOCTYPE ODEM PUBLIC \"-//PFSW//DTD ODEM 1.1\" \"http://pfsw.org/ODEM/schema/dtd/odem-1.1.dtd\">"); 
		out.write("<ODEM version=\"1\">");
		out.write("<header>");
		out.write("<created-by>");
		out.write("<exporter version=\"1.1\">");
		out.write(this.getClass().getName());
		out.write("</exporter>");
		out.write("<provider>Jens Dietrich, Massey University</provider>");
		out.write("</created-by>");
		out.write("</header>");
		
		out.write("<context name=\"");
		out.write("Eclipse Project ");
		out.write(project.getElementName());
		out.write("\">");
		
		for (ContainerRef c:containers) {
			// do only export source containers, skip referenced jars etc
			if (c.isSourceContainer()) {
				encode(c);
			}
		}
		
		out.write("</context>");
		out.write("</ODEM>");
	}
	private void encode(ContainerRef container) {
		out.write("<container classification=\"");
		out.write(container.isArchive()?"jar":"dir");
		out.write("\" name=\"");
		out.write(container.getName());
		out.write("\">");
		for (PackageRef p:container.getPackages()) {
			// skip empty packages (this are folders like "com" or "nz" )
			if (p.getClassCount()>0) {
				encode(p);
			}
		}
		out.write("</container>");
	}
	
	private void encode(PackageRef container) {
		out.write("<namespace name=\"");
		out.write(container.getName());
		out.write("\">");
		for (ClassRef c:container.getClasses()) {
			encode((SourceRef)c); 
		}
		out.write("</namespace>");
	}
	
	private void encode(SourceRef src) {
		// <type visibility="default" classification="class" name="junit.extensions.ActiveTestSuite$1">
		out.write("<type name=\"");
		out.write(src.getFullName());
		
		out.write("\" visibility=\"");
		out.write(src.getVisibility());
		
		out.write("\" classification=\"");
		if (src.getType()==JavaType.ANNOTATION)
			out.write("annotation");
		else if (src.getType()==JavaType.INTERFACE)
			out.write("interface");
		else if (src.getType()==JavaType.ENUMERATION)
			out.write("enum");
		else if (src.getType()==JavaType.CLASS)
			out.write("class");
		else
			out.write("unknown");
		
		out.write("\" isAbstract=\"");
		out.write(src.isAbstract()?"yes":"no");
		
		out.write("\" isFinal=\"");
		out.write(src.isFinal()?"yes":"no");
		
		out.write("\">");
		
		out.write("<dependencies count=\"");
		out.write(getDependencyCount(src));
		out.write("\">");
		
		for (ClassRef target:src.getSuperTypes()) {
			encode(target,"extends");
		}
		for (ClassRef target:src.getInterfaces()) {
			encode(target,"implements");
		}
		for (ClassRef target:src.getUsedClasses()) {
			encode(target,"uses");
		}
		
		out.write("</dependencies>");
		out.write("</type>");
	}
	private void encode(ClassRef target,String classification) {
		if (target==null)
			return;
		
		// <depends-on classification="extends" name="java.util.ArrayList" />
		out.write("<depends-on classification=\"");
		out.write(classification);
		out.write("\" name=\"");
		out.write(target.getFullName());
		out.write("\" />");
	}

	private int getDependencyCount(SourceRef src) {
		int count = src.getSuperTypes().size();
		count = count+src.getInterfaces().size();
		count = count+src.getUsedClasses().size();
		return count;
		
	}

}
