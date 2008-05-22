package nz.ac.massey.cs.barrio.gui;

import nz.ac.massey.cs.barrio.views.InputView;
import nz.ac.massey.cs.barrio.views.OutputView;

import org.eclipse.ui.PlatformUI;

public class GuiGetter {
	
	public InputUI getInputUI() 
	{
		String id = "nz.ac.massey.cs.barrio.views.InputView";
		InputView view = (InputView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(id);
//		System.out.println("[GuiGetter]: InputUI="+view.getTitle());
		return view.getInputUI();
	}
	
	public OutputUI getOutputUI()
	{
		String id = "nz.ac.massey.cs.barrio.views.OutputView";
		OutputView view = (OutputView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(id);
		return view.getOutputUI();
	}

}
