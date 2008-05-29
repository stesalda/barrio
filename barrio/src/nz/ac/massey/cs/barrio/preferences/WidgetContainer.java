package nz.ac.massey.cs.barrio.preferences;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class WidgetContainer {
	
	private Button negation;
	private Combo type;
	private Text value;
	
	public Button getNegation() {
		return negation;
	}
	public void setNegation(Button negation) {
		this.negation = negation;
	}
	public Combo getType() {
		return type;
	}
	public void setType(Combo type) {
		this.type = type;
	}
	public Text getValue() {
		return value;
	}
	public void setValue(Text value) {
		this.value = value;
	}

}
