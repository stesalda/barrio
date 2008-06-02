package nz.ac.massey.cs.barrio.preferences;

import java.io.File;
import java.util.ArrayList;

import nz.ac.massey.cs.barrio.Activator;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
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
		
		
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
	}
	
	
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performCancel() {
		System.out.println("[BarrioPreferencePage]: Cancel pressed");
		return super.performCancel();
	}
	
	@Override
	public boolean performOk() {
		System.out.println("[BarrioPreferencePage]: OK pressed");
		RuleStorage storage = new RuleStorage(null);
		storage.store(ruleList);
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		System.out.println("[BarrioPreferencePage]: Apply pressed");
		super.performApply();
	}
	
	@Override
	protected void performDefaults() {
		System.out.println("[BarrioPreferencePage]: Defaults pressed");
		super.performDefaults();
	}
	
	protected Control createContents(Composite parent) {
		ruleList = new ArrayList<ReferenceRule>();
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
				updateSWTList();
			}
			
		});
		
		Button removeRuleButton = new Button(buttonsComposite, SWT.PUSH);
		removeRuleButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeRuleButton.setText("Remove");
		removeRuleButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				ruleList.remove(ruleListSWT.getSelectionIndex());
				updateSWTList();
			}
		});
		
		new Label(buttonsComposite, SWT.NULL);
		
		Button saveRulesButton = new Button(buttonsComposite, SWT.PUSH);
		saveRulesButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		saveRulesButton.setText("Save");
		saveRulesButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(top.getShell(), SWT.SAVE);
				RuleStorage storage = new RuleStorage(dialog.open());
				storage.store(ruleList);
			}
		});
		
		Button loadRulesButton = new Button(buttonsComposite, SWT.PUSH);
		loadRulesButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		loadRulesButton.setText("Load");
		loadRulesButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(top.getShell(), SWT.OPEN);
				RuleStorage storage = new RuleStorage(dialog.open());
				ruleList = storage.load();
				updateSWTList();
			}
		});
		populateList();
		return top;
	}
	

	private void populateList() {
		RuleStorage storage = new RuleStorage(null);
		if(storage.load()!=null) ruleList = storage.load();
		updateSWTList();
	}

	protected void buttonAddRuleClick(Composite top) {
		AddRuleDialog dialog = new AddRuleDialog(top.getShell(), null);
		ReferenceRule rule = dialog.open();
		ruleList.add(rule);
		updateSWTList();
	}
	
	private void updateSWTList()
	{
		ruleListSWT.removeAll();
		for(ReferenceRule rule:ruleList)
		{
			ruleListSWT.add(rule.toString());
		}
	}

}