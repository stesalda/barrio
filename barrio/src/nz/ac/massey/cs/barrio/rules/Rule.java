package nz.ac.massey.cs.barrio.rules;

public interface Rule {
	
	public String getCondition();
	
	public void setConditionValue(String conditionValue);
	public String getConditionValue();

	public void setNegation(boolean negation);
	public boolean isNegated();
	
	public String getResultStatement();
	
	public void setResult(String result);
	public String getResult();

}
