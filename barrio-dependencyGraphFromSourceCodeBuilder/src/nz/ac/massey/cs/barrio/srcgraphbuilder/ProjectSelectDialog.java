package nz.ac.massey.cs.barrio.srcgraphbuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ProjectSelectDialog extends Dialog{
	
	private List<IJavaProject> selectedProjects;
	private IJavaProject[] existingProjects;
	private Display display;
	
	public ProjectSelectDialog(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.display = parent.getDisplay();
		this.selectedProjects = new ArrayList<IJavaProject>();
		this.existingProjects = null;
	}
	
	public List<IJavaProject> open()
	{
		Shell shell = new Shell(getParent(), getStyle());
		shell.setLayout(new FillLayout());
	    shell.setText("test");
	    createContents(shell);
	    shell.pack();
	    shell.open();
	    Display display = getParent().getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    
		return selectedProjects;
	}
	
	
	private void createContents(final Shell shell) {
	    shell.setLayout(new FillLayout());
	    shell.setText("Select projects");
	    
	    Composite parent = new Composite(shell, SWT.NONE);
	    parent.setLayout(new GridLayout(1, true));
	    

	    GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
	    tableData.heightHint = 180;
	    
	    Composite compositeTable = new Composite(parent, SWT.NONE);
	    compositeTable.setLayout(new FillLayout());
	    compositeTable.setLayoutData(tableData);	  
	    
	    final Table table = new Table(compositeTable, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    populateTable(table);
	    
	    GridData controlsData = new GridData(GridData.FILL_HORIZONTAL);
	    controlsData.heightHint = 30;
	    
	    Composite controls = new Composite(parent, SWT.NONE);
	    controls.setLayout(new GridLayout(3, true));
	    controls.setLayoutData(controlsData);
	    
	    GridData btnData = new GridData(GridData.FILL_HORIZONTAL);
	    btnData.heightHint = 20;
	    
	    Button btnCancel = new Button(controls, SWT.PUSH);
	    btnCancel.setText("Cancel");
	    btnCancel.setLayoutData(btnData);
	    btnCancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				selectedProjects.clear();
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
				if(existingProjects != null)
					for(IJavaProject project:existingProjects)
					{
						String projectName = project.getElementName();
						for(TableItem item:table.getItems())
							if(item.getText().equals(projectName) && item.getChecked())
								selectedProjects.add((IJavaProject) project);
					}
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
	
	

	private void populateTable(Table table) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJavaModel jmodel = JavaCore.create(workspace.getRoot());
		
		try {
			IJavaProject[] jprojects = jmodel.getJavaProjects();
			this.existingProjects = jprojects;
			
			for(IJavaProject project:jprojects)
			{
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(project.getElementName());
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
