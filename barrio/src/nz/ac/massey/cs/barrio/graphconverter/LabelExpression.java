package nz.ac.massey.cs.barrio.graphconverter;

import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractExpression;

public class LabelExpression extends AbstractExpression{

	private String jar;
	private String packageName;
	private String className;
	
	public LabelExpression(String jar, String packageName, String className)
	{
		this.jar = jar;
		this.packageName = packageName;
		this.className = className;
	}
	
	@Override
	public Object get(Tuple t) {
		String s1 = "";
		String s2 = "";
		String s3 = "";
		if(t.canGetString(jar)) s1 = t.get(jar).toString();
		if(t.canGetString(packageName)) s2 = t.get(packageName).toString();
		if(t.canGetString(className)) s3 = t.get(className).toString();
		
		StringBuilder builder = new StringBuilder();
		if(s1.length()>0) {
			builder.append(s1);
		}
		if(s2.length()>0) {
			builder.append('\n');
			builder.append(s2);
		}
		if(s3.length()>0) {
			builder.append('\n');
			builder.append(s3);
		}
		
		return builder.toString();
	}

	public Class getType(Schema arg0) {
		// TODO Auto-generated method stub
		return String.class;
	}

}
