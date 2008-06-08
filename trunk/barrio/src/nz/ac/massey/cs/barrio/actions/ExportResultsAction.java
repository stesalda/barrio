package nz.ac.massey.cs.barrio.actions;

import java.util.List;

import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputUI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportResultsAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		List<Exporter> exporters = KnownExporter.all();
		Exporter e = exporters.get(0);
		
		GuiGetter gg = new GuiGetter();
		InputUI input = gg.getInputUI();
		OutputUI output = gg.getOutputUI();
//		e.export(input.getJob().getInitGraph(), input.getJob().getFinalGraph(), 
//				input.getJob().getSeparation(), input.getJob().getFilters(), 
//				input.getJob().getRemovedEdges(), 
//				output.getTreePwMC(), output.getTreeCwMP());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}