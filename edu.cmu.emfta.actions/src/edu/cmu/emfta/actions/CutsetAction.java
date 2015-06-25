package edu.cmu.emfta.actions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeSpec;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DSemanticDiagramSpec;

import edu.cmu.emfta.Event;

public class CutsetAction extends AbstractExternalJavaAction {
	private List<Event> cutset;

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {

		System.out.println("[CutSetAction] calling execute");
		for (EObject eo : selections) {
			EObject target = null;

			System.out.println("[CutSetAction] eobject = " + eo);

			if (eo instanceof DSemanticDiagramSpec) {
				DSemanticDiagramSpec ds = (DSemanticDiagramSpec) eo;
				target = ds.getTarget();

				System.out.println("[CutSetAction] eobject class= " + eo.getClass());

				System.out.println("[CutSetAction] target = " + target);
			}

			if (eo instanceof DNodeSpec) {
				DNodeSpec ds = (DNodeSpec) eo;
				target = ds.getTarget();

				System.out.println("[CutSetAction] eobject class= " + eo.getClass());

				System.out.println("[CutSetAction] target = " + target);
			}

			if (target != null) {
				if (target instanceof edu.cmu.emfta.Tree) {
					generateCutSet((edu.cmu.emfta.Tree) target);
				}

				if (target instanceof edu.cmu.emfta.FTAModel) {
					generateCutSet(((edu.cmu.emfta.FTAModel) target).getRoot());
				}
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
			System.out.println("[CutSetAction] eobject class= " + eo.getClass());

			if (eo instanceof DSemanticDiagramSpec) {
				DSemanticDiagramSpec ds = (DSemanticDiagramSpec) eo;
				EObject target = ds.getTarget();

				System.out.println("[CutSetAction] eobject class= " + eo.getClass());

				System.out.println("[CutSetAction] target = " + target);
			}

			if (eo instanceof DNodeSpec) {
				DNodeSpec ds = (DNodeSpec) eo;
				EObject target = ds.getTarget();

				System.out.println("[CutSetAction] eobject class= " + eo.getClass());

				System.out.println("[CutSetAction] target = " + target);
			}

		}
		return true;
	}

	public void generateCutSet(edu.cmu.emfta.Tree tree) {
		CutSet cs = new CutSet(tree);
		cs.process();
		System.out.println(cs);
	}

}
