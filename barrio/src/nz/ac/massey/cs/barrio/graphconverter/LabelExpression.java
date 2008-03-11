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
		String s1 = t.get(jar).toString();
		String s2 = t.get(packageName).toString();
		String s3 = t.get(className).toString();
		
		
		return s1 + '\n' + s2 + '\n' + s3;
	}

	public Class getType(Schema arg0) {
		// TODO Auto-generated method stub
		return String.class;
	}

}
