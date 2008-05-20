package nz.ac.massey.cs.barrio.preferences;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class AddRuleDialog extends Dialog{
	
	private Display display;
	private ReferenceRule rule;

	private String referee;
	private String result;
	
	public AddRuleDialog(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.display = parent.getDisplay();

	    rule = null;
	    referee = null;
	    result = null;
	}
	
	public ReferenceRule open()
	{
		Shell shell = new Shell(getParent(), getStyle());
		shell.setLayout(new FillLayout());
	    createContents(shell);
	    shell.setMinimumSize(300, 100);
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
	    parent.setLayout(new GridLayout(3, false));
	    
	    final Label label1 = new Label(parent, SWT.NONE);
	    label1.setText("IF references");
	    
	    final Text refereeElementTxt = new Text(parent, SWT.BORDER);
	    if(referee!=null) refereeElementTxt.setText(referee);
	    
	    final Button checkNot = new Button(parent, SWT.CHECK);
	    checkNot.setText("NOT");
	    
	    Label label2 = new Label(parent, SWT.NONE);
	    label2.setText("THEN");
	    
	    final Text resultTxt = new Text(parent, SWT.BORDER);
	    if(result!=null) resultTxt.setText(result);
	    
	    new Label(parent, SWT.NULL);
	    
	    GridData controlsData = new GridData(GridData.FILL_HORIZONTAL);
	    
	    Composite controls = new Composite(parent, SWT.NONE);
	    controls.setLayout(new GridLayout(3, true));
	    controls.setLayoutData(controlsData);
	    
	    GridData btnData = new GridData(GridData.FILL_HORIZONTAL);
	    
	    Button btnCancel = new Button(controls, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(btnData);
	    btnCancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
	    	
	    });
	    Composite blank = new Composite(controls, SWT.NONE);
	    
	    blank.setLayoutData(btnData);
	    Button btnOk = new Button(controls, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setLayoutData(btnData);
	    
	    
	    btnOk.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				
				if(refereeElementTxt.getText().length()<1 || resultTxt.getText().length()<1){
					shell.close();
					return;
				}				
				rule = new ReferenceRule();
				rule.setNegation(checkNot.getSelection());
				rule.setReference(refereeElementTxt.getText());
				rule.setResult(resultTxt.getText());
				
				shell.close();
			}
	    });
	    
	    shell.setSize(400, 250);
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
		
	}
	
	public void setText(String referee, String result)
	{
		this.referee = referee;
		this.result = result;
	}
}