package nz.ac.massey.cs.barrio.preferences;

import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;
import nz.ac.massey.cs.barrio.rules.RuleCondition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddRuleDialog extends Dialog{
	
	private ReferenceRule rule;
	
	public AddRuleDialog(Shell parent, ReferenceRule rule)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.rule = rule;
	}
	
	public ReferenceRule open()
	{
		Shell shell = new Shell(getParent(), getStyle());
	    createContents(shell);
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
	    parent.setLayout(new GridLayout(3, true));
	    
	    //row 1 of elements starts
	    final Label label1 = new Label(parent, SWT.NONE);
	    label1.setText("IF references");
	    label1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    final Text conditionTxt1 = new Text(parent, SWT.BORDER);
	    conditionTxt1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    final Button checkNot1 = new Button(parent, SWT.CHECK);
	    checkNot1.setText("NOT");
	    
	    //row 1 ends
	    
	    
	    //row 2 starts
	    new Label(parent, SWT.NULL);
	    
	    final Button checkAnd = new Button(parent, SWT.CHECK);
	    checkAnd.setText("AND");
	    checkAnd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
	    	    
	    new Label(parent, SWT.NULL);
	    //row 2 ends
	    
	    
	    //row 3 starts
	    new Label(parent, SWT.NULL);
	    
	    final Text conditionTxt2 = new Text(parent, SWT.BORDER);
	    conditionTxt2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    conditionTxt2.setEnabled(false);
	    
	    final Button checkNot2 = new Button(parent, SWT.CHECK);
	    checkNot2.setText("NOT");
	    checkNot2.setEnabled(false);
	    //row 3 ends
	    
	    
	    //row 4 starts
	    new Label(parent, SWT.NULL);
	    new Label(parent, SWT.NULL);
	    new Label(parent, SWT.NULL);
	    //row 4 ends
	    
	    
	    //row 5 starts
	    Label label2 = new Label(parent, SWT.NONE);
	    label2.setText("THEN");
	    label2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    final Text resultTxt = new Text(parent, SWT.BORDER);
	    resultTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    new Label(parent, SWT.NULL);
	    //row 5 ends
	    

	    //row 6 starts 
	    new Label(parent, SWT.NULL);
	    
	    Button btnCancel = new Button(parent, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    final Button btnOk = new Button(parent, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setEnabled(false);
	    btnOk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    //row 6 ends
	    
	    if(rule!=null)
	    {
	    	conditionTxt1.setText(rule.getConditionValue().get(0).getReference());
	    	checkNot1.setSelection(rule.getConditionValue().get(0).isNegated());
	    	if(rule.getConditionValue().size()>1)
	    	{
	    		checkAnd.setSelection(true);
	    		conditionTxt2.setText(rule.getConditionValue().get(1).getReference());
	    		checkNot2.setSelection(rule.getConditionValue().get(1).isNegated());
	    	}
	    	resultTxt.setText(rule.getResult());
	    }
	    
	    // events
	    checkAnd.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				if(checkAnd.getSelection())
				{
					conditionTxt2.setEnabled(true);
					checkNot2.setEnabled(true);
				}else
				{
					conditionTxt2.setEnabled(false);
					checkNot2.setEnabled(false);
				}
			}
	    	
	    });
	    
	    btnOk.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				
				rule = new ReferenceRule();
				List<RuleCondition> conds = new ArrayList<RuleCondition>();
				RuleCondition cond1 = new RuleCondition();
				cond1.setNegation(checkNot1.getSelection());
				cond1.setReference(conditionTxt1.getText());
				conds.add(cond1);
				if(checkAnd.getSelection())
				{
					RuleCondition cond2 = new RuleCondition();
					cond2.setNegation(checkNot2.getSelection());
					cond2.setReference(conditionTxt2.getText());
					conds.add(cond2);
				}
				rule.setConditionValues(conds);
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
	    //events end
	    
//	    //shell.setSize(400, 250);
//	    Monitor primary = display.getPrimaryMonitor();
//	    Rectangle bounds = primary.getBounds();
//	    Rectangle rect = shell.getBounds();
//	    int x = bounds.x + (bounds.width - rect.width) / 2;
//	    int y = bounds.y + (bounds.height - rect.height) / 2;
//	    shell.setLocation(x, y);
		
	}
}