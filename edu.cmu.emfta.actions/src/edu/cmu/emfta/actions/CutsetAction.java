package edu.cmu.emfta.actions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;
import org.eclipse.sirius.diagram.DSemanticDiagram;

import edu.cmu.emfta.Event;

public class CutsetAction extends AbstractExternalJavaAction {
	private List<Event> cutset;

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {

		System.out.println("[CutSetAction] calling execute");
		for (EObject eo : selections) {
			System.out.println("[CutSetAction] eobject = " + eo);
			if (eo instanceof DSemanticDiagram) {
				System.out.println("[CutSetAction] is diagram");

				DSemanticDiagram dd = (DSemanticDiagram) eo;
				EObject target = dd.getTarget();

				if (target instanceof edu.cmu.emfta.Tree) {
					generateCutSet((edu.cmu.emfta.Tree) target);
				}

				if (target instanceof edu.cmu.emfta.FTAModel) {
					generateCutSet(((edu.cmu.emfta.FTAModel) target).getRoot());
				}
			} else {
				System.out.println("[CutSetAction] is NOT diagram");

			}
		}
	}

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {

		/**
		 * For now, we return true all the time. Might need to optimize
		 * it to make it more user-friendly.
		 */
		System.out.println("[CutSetAction] calling canExecute");
		for (EObject eo : selections) {
			System.out.println("[CutSetAction] eobject = " + eo);

		}
		return true;
	}

	public void generateCutSet(edu.cmu.emfta.Tree tree) {
		CutSet cs = new CutSet(tree);
		cs.process();
		System.out.println(cs);
	}

}
