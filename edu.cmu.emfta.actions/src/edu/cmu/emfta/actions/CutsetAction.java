package edu.cmu.emfta.actions;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;
import org.eclipse.sirius.diagram.DSemanticDiagram;

import edu.cmu.emfta.Event;
import edu.cmu.emfta.Gate;

public class CutsetAction extends AbstractExternalJavaAction {

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

		System.out.println("[CutSetAction] calling canExecute");
		for (EObject eo : selections) {
			System.out.println("[CutSetAction] eobject = " + eo);

		}
		return true;
	}

	public static void generateCutSet(edu.cmu.emfta.Tree tree) {
		System.out.println("[CutSetAction] calling generateCutSet");
		System.out.println("[CutSetAction] tree = " + tree);

		processGate(tree.getGate());
	}

	public static void processGate(edu.cmu.emfta.Gate gate) {
		System.out.println("[CutSetAction] calling processGate");
		System.out.println("[CutSetAction] gate = " + gate);

		for (Gate g : gate.getGates()) {
			processGate(g);
		}

		for (Event e : gate.getEvents()) {
			processEvent(e);
		}
	}

	public static void processEvent(edu.cmu.emfta.Event event) {
		System.out.println("[CutSetAction] calling processEvent");
		System.out.println("[CutSetAction] event = " + event.getName());
	}

}
