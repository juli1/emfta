package org.osate.aadl2.errormodel.emfta.fta;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.GateType;

public class EmftaWrapper {
	org.osate.aadl2.errormodel.analysis.fta.Event rootEvent;
	edu.cmu.emfta.FTAModel emftaModel;
	
	public Map<String,edu.cmu.emfta.Event> cache;
	
	private String getIdentifierString (org.osate.aadl2.errormodel.analysis.fta.Event event)
	{
		return event.getDescription() + event.getType().toString();
	}
	
	private void save (org.osate.aadl2.errormodel.analysis.fta.Event generic, edu.cmu.emfta.Event emfta)
	{
		if (cache.containsKey(getIdentifierString(generic)))
		{
			return;
		}
		
		emftaModel.getEvents().add(emfta);
		cache.put(getIdentifierString(generic), emfta);
	}
	
	private edu.cmu.emfta.Event get (org.osate.aadl2.errormodel.analysis.fta.Event event)
	{
		return cache.get(getIdentifierString(event));
	}
	
	public EmftaWrapper (org.osate.aadl2.errormodel.analysis.fta.Event root)
	{
		rootEvent = root;
		emftaModel = null;
		cache = new HashMap<String, edu.cmu.emfta.Event>();
	}

	public edu.cmu.emfta.Event toEmftaEvent(org.osate.aadl2.errormodel.analysis.fta.Event event) {
		edu.cmu.emfta.Event emftaEvent;
		
		emftaEvent = get(event);
		
		if (emftaEvent != null)
		{
			return emftaEvent;
		}

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

				edu.cmu.emfta.Event gateEvent = toEmftaEvent(subEvent);
				emftaEvent.setGate(gateEvent.getGate());

				save (event, emftaEvent);
//				emftaModel.getEvents().add(emftaEvent);


			} else {
				throw new UnsupportedOperationException("EMFTA - not handled now");
			}

			break;
		}

		case EVENT: {
			emftaEvent.setType(edu.cmu.emfta.EventType.BASIC);
//			emftaModel.getEvents().add(emftaEvent);
			save (event, emftaEvent);

			break;
		}

		case OR: {
			edu.cmu.emfta.Gate emftaGate;
			emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.OR);
			emftaEvent.setGate(emftaGate);
			for (org.osate.aadl2.errormodel.analysis.fta.Event e : event.getSubEvents()) {
				edu.cmu.emfta.Event tmpEmftaEvent = toEmftaEvent(e);

				emftaGate.getEvents().add(tmpEmftaEvent);
//				emftaModel.getEvents().add(tmpEmftaEvent);
				save (e, tmpEmftaEvent);
				

			}
			break;
		}

		case AND: {
			edu.cmu.emfta.Gate emftaGate;
			emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.AND);
			emftaEvent.setGate(emftaGate);
			for (org.osate.aadl2.errormodel.analysis.fta.Event e : event.getSubEvents()) {
				edu.cmu.emfta.Event tmpEmftaEvent = toEmftaEvent(e);
				save (e, tmpEmftaEvent);

//				emftaModel.getEvents().add(tmpEmftaEvent);
				emftaGate.getEvents().add(tmpEmftaEvent);
			}
			break;
		}
		}

//		emftaModel.getEvents().add(emftaEvent);
		return emftaEvent;
	}

	public edu.cmu.emfta.FTAModel getEmftaModel() {
		if (emftaModel == null)
		{
			edu.cmu.emfta.Event emftaRootEvent;
			
			emftaModel = EmftaFactory.eINSTANCE.createFTAModel();
			emftaModel.setName(rootEvent.getName());
			emftaModel.setDescription(rootEvent.getDescription());
			emftaRootEvent = toEmftaEvent(rootEvent);
			emftaModel.getEvents().add(emftaRootEvent);
			emftaModel.setRoot(emftaRootEvent);
		}
		

		return emftaModel;
	}

}
