package nz.ac.massey.cs.barrio.preferences;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class WidgetContainer {
	
	private Button negation;
	private CCombo type;
	private Text value;
	private Button btnRemove;
	
	public Button getNegation() {
		return negation;
	}
	public void setNegation(Button negation) {
		this.negation = negation;
	}
	public CCombo getType() {
		return type;
	}
	public void setType(CCombo type) {
		this.type = type;
	}
	public Text getValue() {
		return value;
	}
	public void setValue(Text value) {
		this.value = value;
	}
	public Button getBtnRemove() {
		return btnRemove;
	}
	public void setBtnRemove(Button btnRemove) {
		this.btnRemove = btnRemove;
	}

}
