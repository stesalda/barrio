package nz.ac.massey.cs.barrio.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import nz.ac.massey.cs.barrio.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class BarrioPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public BarrioPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.FOLDER_NAME, 
				"&Barrio directory", getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE, 
				"Graph clustering analysis:", 2,
				new String[][] {{"&Directed","directed"},{"&Undirected","undirected"}},
				getFieldEditorParent()));
	}
	
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}	
}