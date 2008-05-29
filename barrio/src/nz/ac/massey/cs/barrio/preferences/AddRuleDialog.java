package nz.ac.massey.cs.barrio.preferences;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;
import nz.ac.massey.cs.barrio.rules.RuleCondition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class AddRuleDialog extends Dialog{
	
	private List<WidgetContainer> container;
	private Text resultTxt;
	private Button btnCancel;
	private Button btnOk;
	
	private ReferenceRule rule;
	
	public AddRuleDialog(Shell parent, ReferenceRule rule)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.rule = rule;
		container = new ArrayList<WidgetContainer>();
	}
	
	public ReferenceRule open()
	{
		Shell shell = new Shell(getParent(), getStyle());
	    createContents(shell);
	    setEvents(shell);
	    shell.pack();
	    shell.open();
	    Display display = getParent().getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    
		return rule;
	}
	
	
	private void createContents(final Shell shell) {
	    shell.setLayout(new FillLayout());
	    shell.setText("Add Rule:");
	    
	    Composite parent = new Composite(shell, SWT.NONE);
	    parent.setLayout(new GridLayout());
	    
	    //row
	    Label label = new Label(parent, SWT.NONE);
	    label.setText("IF");
	    //row ends

	    String[] titles = {"negation", "condition", "value", "remove"};
	    final Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    table.setHeaderVisible(true);
	    for(String title:titles)
	    {
	    	TableColumn column = new TableColumn(table, SWT.RIGHT);
	        column.setText(title);
	    }
	    addCondition(table);
	    
	    for (int i=0; i<titles.length; i++) {
	        table.getColumn (i).pack ();
	      } 
	    GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_BOTH);
		gd.heightHint = 150;
		gd.widthHint = 250;
		table.setLayoutData(gd);
	    
	    Button addConditionButton = new Button(parent, SWT.PUSH);
	    addConditionButton.setText("Add Condition");	
	    addConditionButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				addCondition(table);
			}
	    	
	    });
	    
	    Composite resultComposite = new Composite(parent, SWT.BORDER);
	    resultComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    resultComposite.setLayout(new GridLayout(2,false));
	    
	    
	    Label label2 = new Label(resultComposite, SWT.NONE);
	    label2.setText("THEN");
	    
	    resultTxt = new Text(resultComposite, SWT.BORDER);
	    resultTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    
	    Composite buttonComposite = new Composite(parent, SWT.BORDER);
	    buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    buttonComposite.setLayout(new GridLayout(2,true));
	    
	    btnCancel = new Button(buttonComposite, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    btnOk = new Button(buttonComposite, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    setButtonOk();
	}
	
	
	private void addCondition(Table table)
	{
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, "not");
		item.setText(1, "conditionType");
		item.setText(2, "value");
		item.setText(3, "remove");
		

	    final String[] types = new String[]{"references"};
//		Button checkNot = new Button(conditionsComposite, SWT.CHECK);
//	    checkNot.setText("not");
//	    Combo conditionType = new Combo(conditionsComposite, SWT.NULL);
//	    for(String s:types) conditionType.add(s);
//	    Text condition = new Text(conditionsComposite, SWT.BORDER);
//	    condition.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//	    Button remove = new Button(conditionsComposite, SWT.NULL);
//	    remove.setText("X");
//	    
//	    WidgetContainer wc = new WidgetContainer();
//	    wc.setNegation(checkNot);
//	    wc.setType(conditionType);
//	    wc.setValue(condition);
//	    
//	    container.add(wc);
	}
	
	
	private void setEvents(final Shell shell)
	{
		
	    
	    btnOk.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				
				rule = new ReferenceRule();
				
				rule.setResult(resultTxt.getText());
				
				shell.close();
			}
	    });
	    
	    btnCancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}	    	
	    });
	    
	      
	    resultTxt.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				setButtonOk();
			}	    	
	    });
	}
	
	
	private void setButtonOk()
	{
	    if(isGrammarComplete()) btnOk.setEnabled(true);
	    else btnOk.setEnabled(false);
	}
	
	
	private boolean isGrammarComplete()
	{
		boolean result = false;
		
		
		return result;
	}
}