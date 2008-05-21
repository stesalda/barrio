package nz.ac.massey.cs.barrio.rules;

public class RuleCondition {
	
	private String reference;
	private boolean negation;
	
	public String getReference() {
		return reference;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public boolean isNegated() {
		return negation;
	}
	
	public void setNegation(boolean negation) {
		this.negation = negation;
	}

}
