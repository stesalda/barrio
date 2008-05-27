package nz.ac.massey.cs.barrio.preferences;

import nz.ac.massey.cs.barrio.Activator;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class BarrioPreferenceRulesPage 
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {
	
	private List ruleListSWT;
	private java.util.List<ReferenceRule> ruleList;

	public BarrioPreferenceRulesPage() {
		super(GRID);
		//setPreferenceStore(Activator.getDefault().getPreferenceStore());
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		RuleListEditor editor = new RuleListEditor("ruleListEditor","Defined Rules: ",getFieldEditorParent());
		
		addField(editor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

//	@Override
//	protected void performDefaults() {
//		System.out.println("[BarrioRulePage]: defaults pressed");
//		super.performDefaults();
//	}
	
	

}