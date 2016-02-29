package org.osate.aadl2.errormodel.emfta.fta;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.GateType;

public class EventWrapper {

	public static edu.cmu.emfta.Event toEmftaEvent(org.osate.aadl2.errormodel.analysis.fta.Event event, edu.cmu.emfta.FTAModel emftaModel) {
		edu.cmu.emfta.Event emftaEvent;

		emftaEvent = null;

		emftaEvent = EmftaFactory.eINSTANCE.createEvent();
		emftaEvent.setName(event.getName());
		emftaEvent.setDescription(event.getDescription());
		emftaEvent.setProbability(event.getProbability());

		switch (event.getEventType()) {
		case NORMAL: {
			if (event.getSubEvents().size() == 1) {
				org.osate.aadl2.errormodel.analysis.fta.Event subEvent = event.getSubEvents().get(0);
				emftaEvent.setName(event.getName());
				emftaEvent.setType(edu.cmu.emfta.EventType.BASIC);
				emftaEvent.setDescription(event.getDescription());
//				edu.cmu.emfta.Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
				emftaEvent.setGate(null);

//				switch (subEvent.getType())
//				{
//					case OR:
//					{
//						if (subEvent.getSubEvents().size() == 1) {
//							return toEmftaEvent(subEvent.getSubEvents().get(0));
//						}
//						emftaGate.setType(GateType.OR);
//						break;
//					}
//
//					case AND:
//					{
//
//						emftaGate.setType(GateType.AND);
//						break;
//					}
//
//					default:
//					{
//						OsateDebug.osateDebug("unhandled type");
//						break;
//					}
//				}

				edu.cmu.emfta.Event gateEvent = toEmftaEvent(subEvent, emftaModel);
				emftaEvent.setGate(gateEvent.getGate());
				emftaModel.getEvents().add(emftaEvent);


			} else {
				throw new UnsupportedOperationException("EMFTA - not handled now");
			}

			break;
		}

		case EVENT: {
			emftaEvent.setType(edu.cmu.emfta.EventType.BASIC);
			emftaModel.getEvents().add(emftaEvent);

			break;
		}

		case OR: {
			edu.cmu.emfta.Gate emftaGate;
			emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.OR);
			emftaEvent.setGate(emftaGate);
			for (org.osate.aadl2.errormodel.analysis.fta.Event e : event.getSubEvents()) {
				edu.cmu.emfta.Event tmpEmftaEvent = toEmftaEvent(e, emftaModel);

				emftaGate.getEvents().add(tmpEmftaEvent);
				emftaModel.getEvents().add(tmpEmftaEvent);

			}
			break;
		}

		case AND: {
			edu.cmu.emfta.Gate emftaGate;
			emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.AND);
			emftaEvent.setGate(emftaGate);
			for (org.osate.aadl2.errormodel.analysis.fta.Event e : event.getSubEvents()) {
				edu.cmu.emfta.Event tmpEmftaEvent = toEmftaEvent(e, emftaModel);
				emftaModel.getEvents().add(tmpEmftaEvent);

				emftaGate.getEvents().add(tmpEmftaEvent);
			}
			break;
		}
		}

//		emftaModel.getEvents().add(emftaEvent);
		return emftaEvent;
	}

	public static edu.cmu.emfta.FTAModel toEmftaModel(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.FTAModel emftaModel;
		emftaModel = EmftaFactory.eINSTANCE.createFTAModel();
		emftaModel.setName(event.getName());
		emftaModel.setDescription(event.getDescription());
		emftaModel.setRoot(toEmftaEvent(event,emftaModel));

		return emftaModel;
	}

}
