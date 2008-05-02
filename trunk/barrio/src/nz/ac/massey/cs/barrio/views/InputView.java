package nz.ac.massey.cs.barrio.views;

import nz.ac.massey.cs.barrio.gui.InputUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;



public class InputView extends ViewPart {

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
		GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		inputWidget = new InputUI(parent, SWT.NONE);
		inputWidget.setLayoutData(gridData);
	}


	public void setFocus() {
		inputWidget.setFocus();
	}
	
	public InputUI getInputUI()
	{
		return inputWidget;
	}
}
