package nz.ac.massey.cs.barrio.srcgraphbuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ProjectSelector {
	
	private static List<IJavaProject> projects;
	
	public ProjectSelector()
	{
		projects = new ArrayList<IJavaProject>();
	}

	public static List<IJavaProject> getProjects() {
		selectProjects();
		return projects;
	}
	
	private static void selectProjects()
	{
		buildProjectSelector();
	}
	
	public static void main(String[] args)
	{
		System.out.println(getProjects());
	}

	private static void buildProjectSelector() {
		final Display display = new Display();
	    Shell shell = new Shell(display, SWT.CLOSE);
	    shell.setText("Select projects");
	    shell.setLayout(new FillLayout());
	    
	    Composite parent = new Composite(shell, SWT.NONE);
	    parent.setLayout(new GridLayout(1, true));
	    

	    GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
	    tableData.heightHint = 180;
	    
	    Composite compositeTable = new Composite(parent, SWT.NONE);
	    compositeTable.setLayout(new FillLayout());
	    compositeTable.setLayoutData(tableData);	  
	    
	    Table table = new Table(compositeTable, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    populateTable(display, table);
	    
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
				projects = null;
				display.dispose();
			}
	    	
	    });
	    Composite blank = new Composite(controls, SWT.NONE);
	    
	    blank.setLayoutData(btnData);
	    Button btnOk = new Button(controls, SWT.PUSH);
	    btnOk.setText("Ok");
	    btnOk.setLayoutData(btnData);

	    shell.setSize(400, 250);
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
	    shell.open();
	    
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
		
	}

	private static void populateTable(Display display, Table table) {
		display.asyncExec(new Runnable(){

			public void run() {
				//IResource res = ResourcesPlugin.getWorkspace().getRoot();
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				System.out.println(workspace.getRoot().getProject());
			}
		});

		
	}

}
