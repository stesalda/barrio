package nz.ac.massey.cs.barrio.actions;

import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.OutputUI;
import nz.ac.massey.cs.barrio.preferences.PreferenceConstants;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class SingletonAction implements IViewActionDelegate {

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
	}

	@Override
	public void run(IAction action) 
	{
		OutputUI output = new GuiGetter().getOutputUI();
		output.setShowSingletons(!action.isChecked());
		if(action.isChecked()) hideSingletons(output.getMatrixCluster());
		else showSingletons(output.getMatrixCluster());

	}
	
	private void hideSingletons(Tree tree)
	{
		for(TreeColumn column:tree.getColumns())
		{
			if(column.getText().endsWith("- (1)")) column.setWidth(0);
			else column.pack();
		}
	}
	
	private void showSingletons(Tree tree)
	{
		for(TreeColumn column:tree.getColumns()) column.pack();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}
