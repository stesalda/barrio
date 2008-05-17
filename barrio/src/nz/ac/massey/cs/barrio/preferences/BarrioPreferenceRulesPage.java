package nz.ac.massey.cs.barrio.preferences;

import nz.ac.massey.cs.barrio.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class BarrioPreferenceRulesPage 
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	public BarrioPreferenceRulesPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	protected Control createContents(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT|SWT.BORDER);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(2,false));
				
		Label listLabel = new Label(top, SWT.NONE);
		listLabel.setText("Defined rules:");

		new Label(top, SWT.NULL);

		String[] s = {"1","2","3","4","5","6","7","8","9","0 Extension point found nz.ac.massey.cs.barrio.edgeFilter",
					  "1","2","3","4","5","6","7","8","9","0"};

		List ruleListSWT = new List(top, SWT.V_SCROLL|SWT.BORDER);
		ruleListSWT.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		ruleListSWT.setItems(s);
				
		Composite buttonsComposite = new Composite(top, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonsComposite.setLayout(new GridLayout());
		
		Button addRuleButton = new Button(buttonsComposite, SWT.PUSH);
		addRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addRuleButton.setText("Add Rule");
		
		Button editRuleButton = new Button(buttonsComposite, SWT.PUSH);
		editRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editRuleButton.setText("Edit Rule");
		
		Button removeRuleButton = new Button(buttonsComposite, SWT.PUSH);
		removeRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeRuleButton.setText("Remove");
		

		new Label(top, SWT.NULL).setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING)); 

		new Label(top, SWT.NULL).setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING)); 

		return top;
	}

}