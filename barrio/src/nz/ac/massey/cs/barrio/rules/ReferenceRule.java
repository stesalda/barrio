package nz.ac.massey.cs.barrio.rules;

public class ReferenceRule implements Rule{
	
	private String conditionValue;
	private String result;
	private boolean not;
	
	public String getReference() {
		return conditionValue;
	}
	public void setReference(String reference) {
		this.conditionValue = reference;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public boolean isNegated() {
		return not;
	}
	public void setNegation(boolean not) {
		this.not = not;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("IF");
		if(not) buffer.append(" DOES NOT reference \"");
		else buffer.append(" references \"");
		buffer.append(conditionValue);
		buffer.append("\" THEN it is \"");
		buffer.append(result);
		buffer.append("\"");
		return buffer.toString();
	}
	
	public String getCondition() {
		return "references";
	}
	public String getConditionValue() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getResultStatement() {
		return "it is";
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
		
	}
	
	

}
