/**
 * Copyright (c) 2015 Carnegie Mellon University.
 * All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS," WITH NO WARRANTIES WHATSOEVER.
 * CARNEGIE MELLON UNIVERSITY EXPRESSLY DISCLAIMS TO THE FULLEST 
 * EXTENT PERMITTEDBY LAW ALL EXPRESS, IMPLIED, AND STATUTORY 
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND 
 * NON-INFRINGEMENT OF PROPRIETARY RIGHTS.

 * This Program is distributed under a BSD license.  
 * Please see license.txt file or permission@sei.cmu.edu for more
 * information. 
 * 
 * DM-0003411
 */


package edu.cmu.emfta.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.cmu.emfta.actions.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private TabFolder folder;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("EMFTA Configuration Page");
		System.out.println("starts preferences page");

	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addTab("EMFTA");
//
//		addField(new NoCheckDefaultDirectoryFieldEditor(PreferenceConstants.GCC_PATH, "&Path to gcc bin/ directory:",
//				getFieldEditorParent()));
//
//		addField(new NoCheckDefaultDirectoryFieldEditor(PreferenceConstants.CHEDDAR_PATH,
//				"&Path to Cheddar bin/ directory:", getFieldEditorParent()));
//
//		addField(new NoCheckDefaultDirectoryFieldEditor(PreferenceConstants.MAST_PATH, "&Path to MAST bin/ directory:",
//				getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceConstants.REPORT_TYPE, "Report Type", 1, new String[][] {
				{ "&Single Page", PreferenceConstants.REPORT_TYPE_SINGLEPAGE },
				{ "M&ulti Pages", PreferenceConstants.REPORT_TYPE_MULTIPAGES } }, getFieldEditorParent()));
//		addField(new RadioGroupFieldEditor(PreferenceConstants.ANALYSIS_TYPE, "Analysis Type", 1, new String[][] {
//				{ "&Qualitative", PreferenceConstants.ANALYSIS_TYPE_QUALITATIVE },
//				{ "Quantitative", PreferenceConstants.ANALYSIS_TYPE_QUANTITATIVE } }, getFieldEditorParent()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		System.out.println("starts preferences page");

	}

	/**
	 * Adjust the layout of the field editors so that they are properly aligned.
	 */
	protected void adjustGridLayout() {
		if (folder != null) {
			TabItem[] items = folder.getItems();
			for (int j = 0; j < items.length; j++) {
				GridLayout layout = ((GridLayout) ((Composite) items[j].getControl()).getLayout());
				layout.numColumns = 2;
				layout.marginHeight = 5;
				layout.marginWidth = 5;
			}
		}

		// need to call super.adjustGridLayout() since fieldEditor.adjustForNumColumns() is protected
		super.adjustGridLayout();

		// reset the main container to a single column
		((GridLayout) super.getFieldEditorParent().getLayout()).numColumns = 1;
	}

	/**
	 * Returns a parent composite for a field editor.
	 * <p>
	 * This value must not be cached since a new parent may be created each time this method called. Thus this method
	 * must be called each time a field editor is constructed.
	 * </p>
	 * @return a parent
	 */
	protected Composite getFieldEditorParent() {
		if (folder == null || folder.getItemCount() == 0) {
			return super.getFieldEditorParent();
		}
		return (Composite) folder.getItem(folder.getItemCount() - 1).getControl();
	}

	/**
	 * Adds a tab to the page.
	 * @param text the tab label
	 */
	public void addTab(String text) {
		if (folder == null) {
			// initialize tab folder
			folder = new TabFolder(super.getFieldEditorParent(), SWT.NONE);
			folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(text);

		Composite currentTab = new Composite(folder, SWT.NULL);
		GridLayout layout = new GridLayout();
		currentTab.setLayout(layout);
		currentTab.setFont(super.getFieldEditorParent().getFont());
		currentTab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		item.setControl(currentTab);
	}

}