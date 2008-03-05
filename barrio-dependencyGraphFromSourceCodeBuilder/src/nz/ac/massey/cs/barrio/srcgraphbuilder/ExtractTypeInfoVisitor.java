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
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


/**
 * Visitor used to extract type references from the AST of a class.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ExtractTypeInfoVisitor extends ASTVisitor {

	private final static int DECLARATION = 0;
	private final static int BODY = 1;
	private SourceRef owner = null;
	private int context = DECLARATION;
	private VariableNameTracker varTracker = new VariableNameTracker();
	private boolean recordVarNames = false;
	
    public ExtractTypeInfoVisitor(SourceRef owner) {
		super();
		this.owner = owner;
		this.context = DECLARATION;
	}
    
    
	public boolean visit(CompilationUnit node) {
		this.varTracker.reset();
		return true;
	}

	




	@Override
	public void endVisit(SingleVariableDeclaration node) {
		this.recordVarNames =false;
	}


	@Override
	public void endVisit(VariableDeclarationFragment node) {
		this.recordVarNames =false;
	}


	@Override
	public boolean visit(SingleVariableDeclaration node) {
		this.recordVarNames=true;
		return true;
	}


	@Override
	public boolean visit(VariableDeclarationFragment node) {
		this.recordVarNames=true;
		return true;
	}


	public boolean visit(Block b) {
        context = BODY;
        this.varTracker.down();
        return true;
    }
	
	public void endVisit(Block b) {
        this.varTracker.up();
    }

	
	@Override
	public void endVisit(MethodDeclaration node) {
		this.varTracker.up();
	}

	public boolean visit(ImportDeclaration imp) {
        String name = imp.getName().getFullyQualifiedName();
        if (imp.isOnDemand()) owner.getImportedPackages().add(name);
        else owner.getImportedClasses().add(name);
        return false;
    }
	
	public boolean visit(MethodInvocation i) {
		// System.out.println("method invocation "+i);
		Expression x = i.getExpression();
		analyzeMemberAccess(x);
		return true;
	}
	
	private void analyzeMemberAccess (Expression x) {

		if (x instanceof QualifiedName) {
			QualifiedName qn = (QualifiedName)x;
			System.out.println("encountered qualified name " + qn);
			// check whether the first token is this, super or a known field
			// otherwise, we assume that this is the invocation of a static method
			// otherwise, we have to try (to resolve to type names) all combinations of substrings 
			// because we don't now whether tokens are fields or part of qualified type names
			String name = null;
			StringTokenizer tokenizer = new StringTokenizer(qn.toString(),".");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (name==null) {
					if (this.varTracker.isDefined(token)) { // there is a variable defined with this name
						break;
					}
					name=token;
					this.add2used(name);
					System.out.println("add type ref " + name);
				}
				else {
					name = name+'.'+token;
					this.add2used(name);
					System.out.println("add type ref " + name);
				}
			}
		}
		else if (x instanceof SimpleName) {
			SimpleName s = (SimpleName)x;
			if (!this.varTracker.isDefined(s.toString())) {
				this.add2used(s.toString());
				System.out.println("add type ref " + s);
			}
		}
		else {
			System.out.println("encountered something else " + x);
		}

	
	}
	public boolean visit(SimpleName i) {
		if (this.recordVarNames) {
			this.varTracker.addDefined(i.toString());
		}
		return true;
	}
	
	public boolean visit(PackageDeclaration p) {
		return false;
	}
	
	public boolean visit(Initializer i) {
		System.out.println("visiting initializer");
		return true;
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
    	if (context==BODY)
    		add2used(t.toString());
		return false;  
    }

    public boolean visit(MethodDeclaration m) {
    	List exceptions = m.thrownExceptions();
    	for (Object x:exceptions) {
    		Name n = (Name)x;
    		add2used(n.toString());
    	}
    	this.varTracker.down();
    	return true;
    }

	@Override
	public boolean visit(FieldAccess f) {
		// System.out.println("field access "+i);
		Expression x = f.getExpression();
		analyzeMemberAccess(x);
		return true;
	}


	private void add2used(String type) {
    	if (type!=null && !isPrimitiveType(type)) {
    		//System.out.println(type + " referenced in " + owner.getFullName());
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
