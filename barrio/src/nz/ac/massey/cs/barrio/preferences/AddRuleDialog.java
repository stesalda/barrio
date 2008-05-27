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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddRuleDialog extends Dialog{
	
	private Text conditionTxt1;
	private Button checkNot1;
	private Button checkAnd;
	private Text conditionTxt2;
	private Button checkNot2;
	private Text resultTxt;
	private Button btnCancel;
	private Button btnOk;
	
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
	    parent.setLayout(new GridLayout(3, true));
	    
	    //row 1 of elements starts
	    final Label label1 = new Label(parent, SWT.NONE);
	    label1.setText("IF references");
	    label1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    conditionTxt1 = new Text(parent, SWT.BORDER);
	    conditionTxt1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    checkNot1 = new Button(parent, SWT.CHECK);
	    checkNot1.setText("NOT");
	    
	    //row 1 ends
	    
	    
	    //row 2 starts
	    new Label(parent, SWT.NULL);
	    
	    checkAnd = new Button(parent, SWT.CHECK);
	    checkAnd.setText("AND");
	    checkAnd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
	    	    
	    new Label(parent, SWT.NULL);
	    //row 2 ends
	    
	    
	    //row 3 starts
	    new Label(parent, SWT.NULL);
	    
	    conditionTxt2 = new Text(parent, SWT.BORDER);
	    conditionTxt2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    conditionTxt2.setEnabled(false);
	    
	    checkNot2 = new Button(parent, SWT.CHECK);
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
	    
	    resultTxt = new Text(parent, SWT.BORDER);
	    resultTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    new Label(parent, SWT.NULL);
	    //row 5 ends
	    

	    //row 6 starts 
	    new Label(parent, SWT.NULL);
	    
	    btnCancel = new Button(parent, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    btnOk = new Button(parent, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    //row 6 ends
	    
	    if(rule!=null)
	    {
	    	conditionTxt1.setText(rule.getCondition1().getReference());
	    	checkNot1.setSelection(rule.getCondition1().isNegated());
	    	if(rule.getCondition2()!=null)
	    	{
	    		checkAnd.setSelection(true);
	    		conditionTxt2.setEnabled(true);
	    		checkNot2.setEnabled(true);
	    		conditionTxt2.setText(rule.getCondition2().getReference());
	    		checkNot2.setSelection(rule.getCondition2().isNegated());
	    	}
	    	resultTxt.setText(rule.getResult());
	    }
	    
	    setButtonOk();
	}
	
	
	private void setEvents(final Shell shell)
	{
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
				setButtonOk();
			}
	    	
	    });
	    
	    btnOk.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				
				rule = new ReferenceRule();
				RuleCondition cond1 = new RuleCondition();
				cond1.setNegated(checkNot1.getSelection());
				cond1.setReference(conditionTxt1.getText());
				
				RuleCondition cond2 = null;
				if(checkAnd.getSelection())
				{
					cond2 = new RuleCondition();
					cond2.setNegated(checkNot2.getSelection());
					cond2.setReference(conditionTxt2.getText());
				}
				rule.setCondition1(cond1);
				rule.setCondition2(cond2);
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
	    
	    conditionTxt1.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				setButtonOk();
			}	    	
	    });
	    
	    conditionTxt2.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				setButtonOk();
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
		if(checkAnd.getSelection())
		{
			if(conditionTxt1.getText().length()>0 && 
				conditionTxt2.getText().length()>0 &&
				resultTxt.getText().length()>0) result = true;
		}else
		{
			if(conditionTxt1.getText().length()>0 && 
				resultTxt.getText().length()>0) result = true;
		}
		
		return result;
	}
}