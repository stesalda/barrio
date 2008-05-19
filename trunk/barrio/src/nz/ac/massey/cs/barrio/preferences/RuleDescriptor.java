package nz.ac.massey.cs.barrio.preferences;

public class RuleDescriptor {
	
	private String reference;
	private String result;
	private boolean not;
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public boolean isNot() {
		return not;
	}
	public void setNot(boolean not) {
		this.not = not;
	}

}
