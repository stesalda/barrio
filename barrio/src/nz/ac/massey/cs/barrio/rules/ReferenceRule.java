package nz.ac.massey.cs.barrio.rules;


public class ReferenceRule{
	
	private RuleCondition condition1;
	private RuleCondition condition2;
	private String result;
	
	public ReferenceRule()
	{
		super();
		condition1 = null;
		condition2 = null;
		result = null;
	}
	
	public ReferenceRule(String str)
	{
		StringBuffer buffer = new StringBuffer();
		for(int i=(str.lastIndexOf("\"")-1); i>0; i--)
		{
			if(str.charAt(i)=='\"') break;
			buffer.append(str.charAt(i));
		}
		result = buffer.reverse().toString();
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	public RuleCondition getCondition1() {
		return condition1;
	}

	public void setCondition1(RuleCondition condition1) {
		this.condition1 = condition1;
	}

	public RuleCondition getCondition2() {
		return condition2;
	}

	public void setCondition2(RuleCondition condition2) {
		this.condition2 = condition2;
	}
	
	@Override
	public String toString() {
		if(condition1==null || result.length()<1) return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("IF");
		if(condition1.isNegated()) buffer.append(" DOES NOT reference \"");
		else buffer.append(" references \"");
		buffer.append(condition1.getReference());
		buffer.append("\"");
		if(condition2!=null){
			buffer.append(" AND");
			if(condition2.isNegated()) buffer.append(" DOES NOT reference \"");
			else buffer.append(" references \"");
			buffer.append(condition2.getReference());
			buffer.append("\"");
		}
			
				
		buffer.append(" THEN it is \"");
		buffer.append(result);
		buffer.append("\"");
		return buffer.toString();
	}
}
