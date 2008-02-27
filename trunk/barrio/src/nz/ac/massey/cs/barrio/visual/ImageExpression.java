package nz.ac.massey.cs.barrio.visual;


import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractExpression;

public class ImageExpression extends AbstractExpression{

	private String isInterface;
	private String isException;
	private String isAbstract;
	private String access;
	
	public ImageExpression(String isInterface, String isException, String isAbstract, String access)
	{
		this.isInterface = isInterface;
		this.isException = isException;
		this.isAbstract = isAbstract;
		this.access = access;
	}
	
	@Override
	public Object get(Tuple t) {
		boolean isI = t.get(isInterface).toString().equals("true");
		boolean isE = t.get(isException).toString().equals("true");
		boolean isA = t.get(isAbstract).toString().equals("true");
		boolean isP = t.get(access).toString().equals("private");
		boolean isClass = !isI && !isE;
		
		
		if(isI && isP) return "images/ip.png";
		if(isI) return "images/interface.png";
		
		if(isE && isP && isA) return "images/epa.png";
		if(isE && isP) return "images/ep.png";
		if(isE && isA) return "images/ea.png";
		if(isE) return "images/exception.png";
		
		if(isClass && isP && isA) return "images/cpa.png";
		if(isClass && isP) return "images/cp.png";
		if(isClass && isA) return "images/ca.png";
		if(isClass) return ImageExpression.class.getResource("images/class.png").toString();
		
		return null;
	}

	public Class<String> getType(Schema arg0) 
	{
		return String.class;
	}

}
