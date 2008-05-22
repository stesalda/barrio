package nz.ac.massey.cs.barrio.rules;

public class RuleCondition{
	
	private String reference;
	private boolean negated;
	
	public String getReference() {
		return reference;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public boolean isNegated() {
		return negated;
	}
	
	public void setNegated(boolean negation) {
		this.negated = negation;
	}

}
