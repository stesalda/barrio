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
import java.util.Stack;
import java.util.StringTokenizer;
import org.eclipse.jdt.core.dom.*;
import org.pfsw.odem.DependencyClassification;


/**
 * Visitor used to extract type references from the AST of a class.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class ExtractTypeInfoVisitor extends ASTVisitor {

	private final static int DECLARATION = 0;
	private final static int BODY = 1;
	private Stack<SourceRef> owner = new Stack<SourceRef>(); // use stack to support inner classes
	private int context = DECLARATION;
	private VariableNameTracker varTracker = new VariableNameTracker();
	private boolean isTopLevelClass = true;
	
    public ExtractTypeInfoVisitor(SourceRef src) {
		super();
		this.owner.push(src);
		this.context = DECLARATION;
	}
    
    private SourceRef getSourceClass() {
    	return this.owner.peek();
    }
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		String annotation = node.getTypeName().toString();
		DependencyClassification refType = null;
		
		if ("test.ExpectUses".equals(annotation)) {
			refType = DependencyClassification.NEEDS;
		}
		else if ("test.ExpectImplements".equals(annotation)) {
			refType = DependencyClassification.IMPLEMENTATION;
		}
		else if ("test.ExpectExtends".equals(annotation)) {
			refType = DependencyClassification.EXTENSION;
		}
			
		if (refType!=null) {
			StringLiteral lit = (StringLiteral)node.getValue();			
			String value = lit.getLiteralValue();
			for (StringTokenizer tok = new StringTokenizer(value,",");tok.hasMoreTokens();) {
				String refedType = tok.nextToken().trim();
				ExpectedDependency constraint = new ExpectedDependency(refType,refedType);
				this.getSourceClass().getExpectedDependencies().add(constraint);
			}
		}
		else {
			String n = node.getTypeName().toString();
			this.add2used(n);
		}
		
		return false;
	}

	public boolean visit(CompilationUnit node) {
		this.varTracker.reset();
		return true;
	}

	public boolean visit(SingleVariableDeclaration node) {
		this.varTracker.addDefined(node.getName().toString());
		// there might still be type names in the initializer !	
		return true;
	}
	
	public boolean visit(Initializer node) {
		context = BODY;
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		context = BODY;
		this.varTracker.addDefined(node.getName().toString());
		// there might still be type names in the initializer !
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
        if (imp.isOnDemand()) getSourceClass().getImportedPackages().add(name);
        else {
        	getSourceClass().getImportedClasses().add(name);
        	// this.add2used(name); // add to uses list @TODO make this configurable
        }
        return false;
    }
	
	
	private void analyzeMemberAccess (Expression x) {

		if (x instanceof QualifiedName) {
			QualifiedName qn = (QualifiedName)x;
			// System.out.println("encountered qualified name " + qn);
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
					//System.out.println("add type ref " + name);
				}
				else {
					name = name+'.'+token;
					this.add2used(name);
					//System.out.println("add type ref " + name);
				}
			}
		}
		else if (x instanceof SimpleName) {
			SimpleName s = (SimpleName)x;
			if (!this.varTracker.isDefined(s.toString())) {
				this.add2used(s.toString());
				//System.out.println("add type ref " + s);
			}
		}
		else {
			System.out.println("encountered unknown element in member access node " + x);
		}

	
	}
	public boolean visit(SimpleName i) {
		if (!this.varTracker.isDefined(i.toString())) {
			// exclude method names
			if (!(i.getParent() instanceof MethodDeclaration))
					this.add2used(i.toString());
			//System.out.println("add type ref " + i);
		}
		return true;
	}
	public boolean visit(QualifiedName qn) {
		//System.out.println("encountered qualified name " + qn);
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
				//System.out.println("add type ref " + name);
			}
			else {
				name = name+'.'+token;
				this.add2used(name);
				//System.out.println("add type ref " + name);
			}
		}
		
		return true;
	}
	
	public boolean visit(PackageDeclaration p) {
		return false;
	}
    
    public boolean visit(TypeDeclaration t) {
    	if (!isTopLevelClass) {
    		SourceRef parent = this.owner.peek();
    		SourceRef ic = new SourceRef();
    		ic.setOuterClass(parent); 
    		ic.setName(parent.getName()+'.'+t.getName().toString());
    		this.owner.push(ic);
    		
    		// register references in both directions
    		parent.getUsedTypeNames().add(ic.getFullName());
    		ic.getUsedTypeNames().add(parent.getFullName());
    	}
        if (t.isInterface())
        	getSourceClass().setType(JavaType.INTERFACE);
        else {
        	getSourceClass().setType(JavaType.CLASS);
        }
        
        Type superClass = t.getSuperclassType();
        if (superClass!=null)
        	getSourceClass().getSuperTypeNames().add(superClass.toString());
        List interfaces = t.superInterfaceTypes();
        for (Object i:interfaces) {
        	if (t.isInterface()) {
        		getSourceClass().getSuperTypeNames().add(i.toString());
        	}
        	else {
        		getSourceClass().getInterfaceNames().add(i.toString());
        	}
        }
        applyModifier(t.getModifiers());
        
       
        isTopLevelClass = false;
        // TODO type parameters are not yet supported
        return true;
    }
    
    public void endVisit(TypeDeclaration t) {
    	if (!isTopLevelClass && this.owner.size()>1) {
    		this.owner.pop();
    	}
    }
	private void applyModifier(int flags) {
		this.getSourceClass().setAbstract(this.getSourceClass().getType()==JavaType.INTERFACE?true:Modifier.isAbstract(flags));
		this.getSourceClass().setFinal(Modifier.isFinal(flags));
		if (Modifier.isPublic(flags)) 
			this.getSourceClass().setVisibility("public");
		else if (Modifier.isProtected(flags))
			this.getSourceClass().setVisibility("protected");
		else if (Modifier.isPrivate(flags))
			this.getSourceClass().setVisibility("private");
		else 
			this.getSourceClass().setVisibility("default");
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		getSourceClass().setType(JavaType.ANNOTATION);
		applyModifier(node.getModifiers());
		return true;
	}

	public boolean visit(EnumDeclaration node) {
		getSourceClass().setType(JavaType.ENUMERATION);
		List interfaces = node.superInterfaceTypes();
        for (Object i:interfaces) {
        	getSourceClass().getInterfaceNames().add(i.toString());
        }
        applyModifier(node.getModifiers());
		return true;
	}

	// declaration of single variables
    public boolean visit(SimpleType t) {
    	if (context==BODY)
    		add2used(t.toString());
		return false;  
    }
    
	public boolean visit(MethodDeclaration m) {
		context = BODY;
    	List exceptions = m.thrownExceptions();
    	for (Object x:exceptions) {
    		Name n = (Name)x;
    		add2used(n.toString());
    	}
    	this.varTracker.down();
    	return true;
    }
	
	public boolean visit(FieldDeclaration node) {
		context = BODY;
		return true;
	}

	private void add2used(String type) {
    	if (type!=null && !isPrimitiveType(type)) {
    		//System.out.println(type + " referenced in " + owner.getFullName());
    		getSourceClass().getUsedTypeNames().add(type);
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
