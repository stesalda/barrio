package nz.ac.massey.cs.barrio.rules;

import java.util.List;

public class ReferenceRule{
	
	private List<RuleCondition> ruleConditions;
	private String result;

	public List<RuleCondition> getConditionValue() {
		return ruleConditions;
	}

	public String getResult() {
		return result;
	}

	public void setConditionValues(List<RuleCondition> conditionValues) {
		this.ruleConditions = conditionValues;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		boolean isFirst = true;
		StringBuffer buffer = new StringBuffer();
		buffer.append("IF");
		for(RuleCondition condition:ruleConditions)
		{
			if(!isFirst) buffer.append(" AND");
			if(condition.isNegated()) buffer.append(" DOES NOT reference \"");
			else buffer.append(" references \"");
			buffer.append(condition.getReference());
			buffer.append("\"");
			isFirst = false;
		}
		
		buffer.append(" THEN it is \"");
		buffer.append(result);
		buffer.append("\"");
		return buffer.toString();
	}
}
