package nz.ac.massey.cs.barrio.rules;

public class RuleCondition{
	
	private String conditionType;
	private String value;
	private boolean negated;
	
	
	public String getConditionType() {
		return conditionType;
	}
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isNegated() {
		return negated;
	}
	public void setNegated(boolean negated) {
		this.negated = negated;
	}

}
