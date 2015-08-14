package edu.cmu.emfta.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class EmftaWizardNewfile extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private EmftaWizardNewfilePage mainPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New EMFTA File");
	}

	public void addPages() {
		mainPage = new EmftaWizardNewfilePage(workbench, selection);
		addPage(mainPage);
	}

	public boolean canFinish() {
		return mainPage.canFinish();
	}

	@Override
	public boolean performFinish() {

		return mainPage.finish();
	}

}