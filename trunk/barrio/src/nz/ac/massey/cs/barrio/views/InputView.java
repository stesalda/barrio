package nz.ac.massey.cs.barrio.views;

import nz.ac.massey.cs.barrio.gui.InputUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;



public class InputView extends ViewPart {

	private SashForm sashForm;
	private InputUI inputWidget;
	
	/**
	 * The constructor.
	 */
	public InputView() {
		super();
	}

	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		sashForm.setLayoutData(gridData);
		
		inputWidget = new InputUI(sashForm, SWT.NONE);
	}


	public void setFocus() {
		sashForm.setFocus();
	}
}
