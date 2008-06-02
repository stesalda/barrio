package nz.ac.massey.cs.barrio.preferences;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;
import nz.ac.massey.cs.barrio.rules.RuleCondition;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
	private Table table;
	
	private ReferenceRule rule;
	
	public AddRuleDialog(Shell parent, ReferenceRule rule)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		container = new ArrayList<WidgetContainer>();
		if(rule!=null) this.rule = rule;
		else this.rule = new ReferenceRule();
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

	    Composite tableComposite = new Composite(parent, SWT.NONE);
	    table = buildTable(tableComposite);
	    if(rule.getConditions()!=null && rule.getConditions().size()>0)
	    {
	    	for(RuleCondition c:rule.getConditions())
	    		addCondition(c);
	    }
	    else addCondition(null);
	    
	    Button addConditionButton = new Button(parent, SWT.PUSH);
	    addConditionButton.setText("Add Condition");	
	    addConditionButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				addCondition(null);
				setButtonRemove();
			    setButtonOk();
			}	    	
	    });
	    
	    Composite resultComposite = new Composite(parent, SWT.BORDER);
	    resultComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    resultComposite.setLayout(new GridLayout(2,false));
	    
	    
	    Label label2 = new Label(resultComposite, SWT.NONE);
	    label2.setText("THEN");
	    
	    resultTxt = new Text(resultComposite, SWT.BORDER);
	    resultTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    if(rule.getResult()!=null) resultTxt.setText(rule.getResult());
	    
	    Composite buttonComposite = new Composite(parent, SWT.BORDER);
	    buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    buttonComposite.setLayout(new GridLayout(2,true));
	    
	    btnCancel = new Button(buttonComposite, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    btnOk = new Button(buttonComposite, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    setButtonRemove();
	    setButtonOk();
	}
	
	private Table buildTable(Composite parent)
	{
	    Table table = new Table(parent, SWT.MULTI|SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    table.setHeaderVisible(false);
	    table.setLinesVisible(true);
	    table.setSize(360,150);

	    new TableColumn(table, SWT.RIGHT);
	    new TableColumn(table, SWT.RIGHT);
	    new TableColumn(table, SWT.RIGHT);
	    new TableColumn(table, SWT.RIGHT);
		
	    for (int i=0; i<table.getColumnCount(); i++) {
	        table.getColumn (i).pack ();
	        if(i==0) table.getColumn(i).setWidth(50);
	        if(i==1) table.getColumn(i).setWidth(100);
	        if(i==2) table.getColumn(i).setWidth(150);
	        if(i==3) table.getColumn(i).setWidth(30);
	        	
	        
	    }
	    return table;
	}

	private void addCondition(RuleCondition condition)
	{
		final TableItem item = new TableItem(table, SWT.NONE);
		final String[] types = new String[]{"references"};
		TableEditor editor;
	    
	    editor = new TableEditor(table);
	    Button buttonNot = new Button(table, SWT.CHECK);
	    buttonNot.setText("not");
	    buttonNot.pack();
	    if(condition!=null) buttonNot.setSelection(condition.isNegated());
	    editor.horizontalAlignment = SWT.CENTER;
	    editor.minimumWidth = buttonNot.getSize ().x;
	    editor.setEditor(buttonNot, item, 0);
	    
	    editor = new TableEditor(table);
	    CCombo conditionType = new CCombo(table, SWT.NONE);
	    conditionType.setItems(types);
	    if(condition!=null) conditionType.select(getIndex(types, condition.getConditionType()));
	    else conditionType.select(0);
	    editor.grabHorizontal = true;
	    editor.minimumWidth = 50;
	    editor.setEditor(conditionType, item, 1);

	    editor = new TableEditor(table);
	    Text value = new Text(table, SWT.NONE);
	    if(condition!=null) value.setText(condition.getValue());
	    value.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				setButtonOk();
				
			}
	    	
	    });
	    editor.grabHorizontal = true;
	    editor.minimumWidth = 50;
	    editor.setEditor(value, item, 2);
	    
	    editor = new TableEditor(table);
	    final Button buttonRemove = new Button(table, SWT.PUSH);
	    buttonRemove.setText("x");
	    buttonRemove.setData("index", table.getItemCount()-1);
	    buttonRemove.setToolTipText("Remove condition");
	    buttonRemove.pack();
	    editor.horizontalAlignment = SWT.CENTER;
	    editor.minimumWidth = buttonRemove.getSize().x;
	    editor.setEditor(buttonRemove, item, 3);
	    	    
	    WidgetContainer wc = new WidgetContainer();
	    wc.setNegation(buttonNot);
	    wc.setType(conditionType);
	    wc.setValue(value);
	    wc.setBtnRemove(buttonRemove);
	    
	    container.add(wc);
	    
	    buttonRemove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				buttonRemoveClick(table.getParent(), buttonRemove.getData("index"));
			}
	    	
	    });
	}
	
	
	private int getIndex(String[] types, String conditionType) {
		for(int i=0; i<types.length; i++)
		{
			if(types[i].equals(conditionType)) return i;
		}
		return 0;
	}
	
	

	protected void buttonRemoveClick(Composite parent, Object data) {

		int removed = Integer.parseInt(data.toString());
		container.remove(removed);
		List<RuleCondition> temp = new ArrayList<RuleCondition>();
		for(WidgetContainer wc : container)
		{
			RuleCondition rc = new RuleCondition();
			rc.setNegated(wc.getNegation().getSelection());
			rc.setConditionType(wc.getType().getItem(wc.getType().getSelectionIndex()));
			rc.setValue(wc.getValue().getText());
			temp.add(rc);
		}
		container = new ArrayList<WidgetContainer>();		
		table.dispose();
		
		table = buildTable(parent);
		for(RuleCondition rc : temp)
		{
			addCondition(rc);
		}
		setButtonRemove();
	    setButtonOk();
		
	}

	private void setEvents(final Shell shell)
	{	    
	    btnOk.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) 
			{
				rule = new ReferenceRule();
				List<RuleCondition> conditions = new ArrayList<RuleCondition>();
				for(WidgetContainer wc:container)
				{
					RuleCondition rc = new RuleCondition();
					rc.setNegated(wc.getNegation().getSelection());
					rc.setConditionType(wc.getType().getItem(wc.getType().getSelectionIndex()));
					rc.setValue(wc.getValue().getText());
					conditions.add(rc);
				}
				rule.setConditions(conditions);
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
	
	private void setButtonRemove()
	{
		if(container.size()==1) container.get(0).getBtnRemove().setEnabled(false);
		else container.get(0).getBtnRemove().setEnabled(true);
	}
	
	private boolean isGrammarComplete()
	{
		boolean result = true;
		for(WidgetContainer c:container)
		{
			if(c.getValue().getText().length()<1) return false;
		}
		if(resultTxt.getText().length()<1) return false;
		return result;
	}
}