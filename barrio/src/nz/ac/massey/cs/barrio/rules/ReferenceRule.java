package nz.ac.massey.cs.barrio.rules;

import java.util.List;

public class ReferenceRule{
	
	private List<RuleCondition> conditionValues;
	private String result;

	public List<RuleCondition> getConditionValues() {
		return conditionValues;
	}

	public void setConditionValues(List<RuleCondition> conditionValues) {
		this.conditionValues = conditionValues;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		boolean isFirst = true;
		StringBuffer buffer = new StringBuffer();
		buffer.append("IF");
		for(RuleCondition condition:conditionValues)
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
