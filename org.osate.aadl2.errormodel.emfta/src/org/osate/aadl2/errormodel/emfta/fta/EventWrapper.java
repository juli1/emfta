package org.osate.aadl2.errormodel.emfta.fta;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.GateType;

public class EventWrapper {

	public static edu.cmu.emfta.Event toEmftaEvent(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.Event emftaEvent;
		emftaEvent = EmftaFactory.eINSTANCE.createEvent();
		emftaEvent.setName(event.getName());
		emftaEvent.setDescription(event.getDescription());
		emftaEvent.setProbability(event.getProbability());

		return emftaEvent;
	}

	public static edu.cmu.emfta.Gate toEmftaGate(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.Gate emftaGate;
		emftaGate = EmftaFactory.eINSTANCE.createGate();

		if (event.getType() == org.osate.aadl2.errormodel.analysis.fta.EventType.AND) {
			emftaGate.setType(GateType.AND);
		}

		if (event.getType() == org.osate.aadl2.errormodel.analysis.fta.EventType.OR) {
			emftaGate.setType(GateType.OR);
		}

		for (org.osate.aadl2.errormodel.analysis.fta.Event e : event.getSubEvents()) {
			if ((e.getEventType() == org.osate.aadl2.errormodel.analysis.fta.EventType.EVENT)
					|| (e.getEventType() == org.osate.aadl2.errormodel.analysis.fta.EventType.NORMAL)) {
				emftaGate.getEvents().add(toEmftaEvent(e));
			}

			if ((e.getEventType() == org.osate.aadl2.errormodel.analysis.fta.EventType.AND)
					|| (e.getEventType() == org.osate.aadl2.errormodel.analysis.fta.EventType.OR)) {
				emftaGate.getGates().add(toEmftaGate(e));
			}
		}
		return emftaGate;
	}

	public static edu.cmu.emfta.Tree toEmftaTree(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.Tree emftaTree;
		emftaTree = EmftaFactory.eINSTANCE.createTree();
		emftaTree.setName(event.getName());
		emftaTree.setDescription(event.getDescription());

		emftaTree.setGate(toEmftaGate(event.getSubEvents().get(0)));
		return emftaTree;
	}

	public static edu.cmu.emfta.FTAModel toEmftaModel(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.FTAModel emftaModel;
		emftaModel = EmftaFactory.eINSTANCE.createFTAModel();
		emftaModel.setName(event.getName());
		emftaModel.setDescription(event.getDescription());
		emftaModel.setRoot(toEmftaTree(event));

		return emftaModel;
	}

}
