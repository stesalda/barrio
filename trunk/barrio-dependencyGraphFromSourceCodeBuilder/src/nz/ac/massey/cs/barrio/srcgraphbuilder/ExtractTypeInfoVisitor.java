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

import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;


/**
 * Visitor used to extract type references from the AST of a class.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ExtractTypeInfoVisitor extends ASTVisitor {

	private final static int DECLARATION = 0;
	private final static int BODY = 1;
	private SourceRef owner = null;
	private int context = DECLARATION;
	
    public ExtractTypeInfoVisitor(SourceRef owner) {
		super();
		this.owner = owner;
		this.context = DECLARATION;
	}

	public boolean visit(ImportDeclaration imp) {
        String name = imp.getName().getFullyQualifiedName();
        if (imp.isOnDemand()) owner.getImportedPackages().add(name);
        else owner.getImportedClasses().add(name);
        return false;
    }
    
    public boolean visit(TypeDeclaration t) {
        if (t.isInterface())
        	owner.setType(JavaType.INTERFACE);
        else {
        	owner.setType(JavaType.CLASS);
        }
        
        Type superClass = t.getSuperclassType();
        if (superClass!=null)
        	owner.setSuperClassName(superClass.toString());
        List interfaces = t.superInterfaceTypes();
        for (Object i:interfaces) {
        	owner.getInterfaceNames().add(i.toString());
        }

        // @fixme type parameters are not yet supported
        
        return true;
    }
    
    // declaration of single variables
    public boolean visit(SimpleType t) {
    	add2used(t.toString());
		return true;  
    }

    public boolean visit(MethodDeclaration m) {
    	List exceptions = m.thrownExceptions();
    	for (Object x:exceptions) {
    		Name n = (Name)x;
    		add2used(n.toString());
    	}
    	return true;
    }
    private void add2used(String type) {
    	if (type!=null && !isPrimitiveType(type)) {
    		System.out.println(type + " referenced in " + owner.getFullName());
    		owner.getUsedTypeNames().add(type);
    	}
    }
    private boolean isPrimitiveType(String t) {
    	return "void".equals(t) || 
    		"int".equals(t) ||
    		"long".equals(t) ||
    		"short".equals(t) ||
    		"byte".equals(t) ||
    		"char".equals(t) ||
    		"double".equals(t) ||
    		"float".equals(t) ||
    		"boolean".equals(t);    	
    } 
    

}
