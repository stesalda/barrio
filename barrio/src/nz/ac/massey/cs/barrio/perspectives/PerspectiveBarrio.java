/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package nz.ac.massey.cs.barrio.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;



/**
 *  This class is meant to serve as an example for how various contributions 
 *  are made to a perspective. Note that some of the extension point id's are
 *  referred to as API constants while others are hardcoded and may be subject 
 *  to change. 
 */
public class PerspectiveBarrio implements IPerspectiveFactory {

	private IPageLayout layout;

	public PerspectiveBarrio() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.layout = factory;
		addViews();
	}

	private void addViews() {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		layout.addStandaloneView("nz.ac.massey.cs.barrio.views.OutputView",  true, IPageLayout.RIGHT, 0.2f, editorArea);
		layout.addStandaloneView("nz.ac.massey.cs.barrio.views.InputView",  true, IPageLayout.TOP, 0.35f, editorArea);
	}
}
