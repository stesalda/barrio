package nz.ac.massey.cs.barrio.actions;

import java.util.List;

import nz.ac.massey.cs.barrio.Activator;
import nz.ac.massey.cs.barrio.exporter.Exporter;
import nz.ac.massey.cs.barrio.exporter.KnownExporter;
import nz.ac.massey.cs.barrio.gui.GuiGetter;
import nz.ac.massey.cs.barrio.gui.InputUI;
import nz.ac.massey.cs.barrio.gui.OutputUI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
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
		
		FileDialog dialog = new FileDialog(input.getShell(), SWT.SAVE);
		dialog.setFilterNames(new String[]{"XML files"});
		dialog.setFilterExtensions(new String[]{"*.xml"});
		
		e.export(input.getFilteredGraph(), input.getClusteredGraph(), 
				input.getSeparationLevel(), input.getActiveFilters(), dialog.open());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}