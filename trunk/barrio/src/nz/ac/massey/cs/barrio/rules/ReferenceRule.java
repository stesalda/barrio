package nz.ac.massey.cs.barrio.rules;

import java.util.ArrayList;
import java.util.List;


public class ReferenceRule{
	
	private List<RuleCondition> conditions;
	private String result;
	
	public ReferenceRule()
	{
		conditions = new ArrayList<RuleCondition>();
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


	public List<RuleCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<RuleCondition> conditions) {
		this.conditions = conditions;
	}
	
	@Override
	public String toString() {
		if(conditions.size()<1 || result.length()<1) return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("IF ");
		boolean isFirst = true;
		for(RuleCondition cond:conditions)
		{
			if(!isFirst) buffer.append("AND ");
			if(cond.isNegated()) buffer.append("DOES NOT ");
			buffer.append(cond.getConditionType());
			buffer.append(" \"");
			buffer.append(cond.getValue());
			buffer.append("\" ");
			if(isFirst) isFirst = false;
		}
			
		buffer.append("THEN it is \"");
		buffer.append(result);
		buffer.append("\"");
		return buffer.toString();
	}
}
