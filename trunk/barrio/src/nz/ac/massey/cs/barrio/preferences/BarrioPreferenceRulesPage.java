package nz.ac.massey.cs.barrio.preferences;

import java.util.ArrayList;

import nz.ac.massey.cs.barrio.Activator;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//setDescription("A demonstration of a preference page implementation");
		ruleList = new ArrayList<ReferenceRule>();
		
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
		final Composite top = new Composite(parent, SWT.LEFT);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(2,false));
				
		Label listLabel = new Label(top, SWT.NONE);
		listLabel.setText("Defined rules:");

		new Label(top, SWT.NULL);

		ruleListSWT = new List(top, SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 200;
		ruleListSWT.setLayoutData(gd);
		
		//for(int i=0; i<10; i++) ruleListSWT.add("list item "+ i);
				
		Composite buttonsComposite = new Composite(top, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonsComposite.setLayout(new GridLayout());
		
		Button addRuleButton = new Button(buttonsComposite, SWT.PUSH);
		addRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addRuleButton.setText("Add Rule");
		addRuleButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				buttonAddRuleClick(top);
			}
			
		});
		
		Button editRuleButton = new Button(buttonsComposite, SWT.PUSH);
		editRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editRuleButton.setText("Edit Rule");
		editRuleButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				ReferenceRule oldRule = ruleList.get(ruleListSWT.getSelectionIndex());
				AddRuleDialog dialog = new AddRuleDialog(top.getShell(), oldRule);
				ReferenceRule newRule = dialog.open();
				ruleList.set(ruleListSWT.getSelectionIndex(), newRule);
				ruleListSWT.setItem(ruleListSWT.getSelectionIndex(), newRule.toString());				
			}
			
		});
		
		Button removeRuleButton = new Button(buttonsComposite, SWT.PUSH);
		removeRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeRuleButton.setText("Remove");
		removeRuleButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				ruleListSWT.remove(ruleListSWT.getSelectionIndex());
			}
		});
		
		return top;
	}
	

	protected void buttonAddRuleClick(Composite top) {
		AddRuleDialog dialog = new AddRuleDialog(top.getShell(), null);
		ReferenceRule rule = dialog.open();
		System.out.println("rule = "+rule);
		if(rule!=null)	
		{
			ruleList.add(rule);
			ruleListSWT.add(rule.toString());
		}
	}

}