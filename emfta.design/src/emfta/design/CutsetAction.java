package emfta.design;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.action.AbstractExternalJavaAction;

public class CutsetAction extends AbstractExternalJavaAction {

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		System.out.println("Execute my action");

	}

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		return true;
	}

}