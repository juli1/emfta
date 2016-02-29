package org.osate.aadl2.errormodel.emfta.fta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.util.OsateDebug;
import org.osate.xtext.aadl2.errormodel.errorModel.AndExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.CompositeState;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorTransition;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorEvent;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorFlow;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPath;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorSource;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorTypes;
import org.osate.xtext.aadl2.errormodel.errorModel.FeatureorPPReference;
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.SubcomponentElement;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeSet;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeToken;
import org.osate.xtext.aadl2.errormodel.util.AnalysisModel;
import org.osate.xtext.aadl2.errormodel.util.EM2TypeSetUtil;
import org.osate.xtext.aadl2.errormodel.util.EMV2Properties;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.errormodel.util.PropagationPathEnd;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.Event;
import edu.cmu.emfta.EventType;
import edu.cmu.emfta.Gate;
import edu.cmu.emfta.GateType;
import edu.cmu.emfta.impl.EmftaFactoryImpl;

public class EmftaWrapper {
	private edu.cmu.emfta.FTAModel emftaModel;
	private AnalysisModel currentAnalysisModel;
	private ComponentInstance rootComponent;
	private ErrorBehaviorState rootComponentState;
	private ErrorTypes rootComponentTypes;
	

	public Map<String, edu.cmu.emfta.Event> cache;

	private Event createEvent ()
	{
		Event newEvent = EmftaFactory.eINSTANCE.createEvent();
		emftaModel.getEvents().add(newEvent);
		return newEvent;
	}
	
	
	// private void save (org.osate.aadl2.errormodel.analysis.fta.Event generic,
	// edu.cmu.emfta.Event emfta)
	// {
	// if (cache.containsKey(getIdentifierString(generic)))
	// {
	// return;
	// }
	//
	// emftaModel.getEvents().add(emfta);
	// cache.put(getIdentifierString(generic), emfta);
	// }
	//
	// private edu.cmu.emfta.Event get
	// (org.osate.aadl2.errormodel.analysis.fta.Event event)
	// {
	// if (! cache.containsKey(getIdentifierString(event)))
	// {
	// return null;
	// }
	// return cache.get(getIdentifierString(event));
	// }

	public EmftaWrapper(ComponentInstance root, ErrorBehaviorState errorState, ErrorTypes errorTypes) {
		emftaModel = null;
		cache = new HashMap<String, edu.cmu.emfta.Event>();
		rootComponentTypes = errorTypes;
		rootComponentState = errorState;
		rootComponent = root;
		currentAnalysisModel = new AnalysisModel(rootComponent);
	}

	public edu.cmu.emfta.FTAModel getEmftaModel() {
		if (emftaModel == null) {
			edu.cmu.emfta.Event emftaRootEvent;

			emftaModel = EmftaFactory.eINSTANCE.createFTAModel();
			emftaModel.setName(rootComponent.getName());
			emftaModel.setDescription("Top Level Failure");
			emftaRootEvent = processCompositeErrorBehavior(rootComponent, rootComponentState, rootComponentTypes);
			emftaModel.getEvents().add(emftaRootEvent);
			emftaModel.setRoot(emftaRootEvent);
		}

		return emftaModel;
	}

	/**
	 * For one incoming error propagation and one component, returns all the
	 * potential errors contributors.
	 * 
	 * @param component
	 *            - the component that has the incoming error propagation
	 * @param errorPropagation
	 *            - the error propagation
	 * @return - a list of event that has all the error contributors
	 */
	public Event getAllEventsFromPropagationSource(final ComponentInstance component,
			final ErrorPropagation errorPropagation, final TypeToken typeToken, final Stack<Event> history) {
		List<PropagationPathEnd> propagationSources;
		Event result;
		List<Event> subEvents;

		subEvents = new ArrayList<Event>();

		// if (errorPropagation.getKind() != null)
		// {
		// throw new UnsupportedOperationException("special kind");
		// }

		// if (EMV2Util.isProcessor(errorPropagation)) {
		// OsateDebug.osateDebug("OsateUtils", "processor");
		// }
		//
		// if (EMV2Util.isAccess(errorPropagation)) {
		// OsateDebug.osateDebug("OsateUtils", "access");
		// throw new UnsupportedOperationException("special kind");
		//
		// }
		//
		// if (EMV2Util.isBinding(errorPropagation)) {
		// OsateDebug.osateDebug("OsateUtils", "access");
		// throw new UnsupportedOperationException("special kind");
		// }

		// OsateDebug.osateDebug("FTAUtils", "propagation=" +
		// EMV2Util.getPrintName(errorPropagation));
		// OsateDebug.osateDebug("FTAUtils", "types=" +
		// EMV2Util.getPrintName(typeToken));

		propagationSources = currentAnalysisModel.getAllPropagationSourceEnds(component, errorPropagation);

		for (PropagationPathEnd ppe : propagationSources) {
			ComponentInstance componentSource = ppe.getComponentInstance();
			ErrorPropagation propagationSource = ppe.getErrorPropagation();
			ComponentInstance componentDestination = component;
			ErrorPropagation propagationDestination = errorPropagation;

			/**
			 * Compute the correct type to search for
			 */

			for (ErrorFlow ef : EMV2Util.getAllErrorFlows(componentSource)) {
				/**
				 * Let's walk through all error path and see which one to browse
				 */
				if (ef instanceof ErrorPath) {
					ErrorPath errorPath = (ErrorPath) ef;
					// OsateDebug.osateDebug("FTAUtils",
					// "==========================");
					// OsateDebug.osateDebug("FTAUtils",
					// "Analyzing propagation: " +
					// EMV2Util.getPrintName(propagationSource));
					// OsateDebug.osateDebug("FTAUtils", "Analyzing typetoken :
					// " + EMV2Util.getPrintName(typeToken));
					//
					// OsateDebug.osateDebug("FTAUtils", "Error Path: " +
					// errorPath.getName());
					// OsateDebug.osateDebug("FTAUtils", "source=" +
					// EMV2Util.getPrintName(errorPath.getIncoming()));
					// OsateDebug.osateDebug("FTAUtils", "dest =" +
					// EMV2Util.getPrintName(errorPath.getOutgoing()));
					// OsateDebug.osateDebug("FTAUtils",
					// "constraint type=" +
					// EMV2Util.getPrintName(errorPath.getTypeTokenConstraint()));
					// OsateDebug.osateDebug("FTAUtils",
					// "target token=" +
					// EMV2Util.getPrintName(errorPath.getTargetToken()));

					if (Utils.propagationEndsMatches(errorPath.getOutgoing(), propagationSource) == false) {
						OsateDebug.osateDebug("FTAUtils",
								"ends do not match on path" + errorPath.getName() + "source="
										+ EMV2Util.getPropagationName(propagationSource) + ";types2="
										+ EMV2Util.getPropagationName(propagationDestination));
						continue;
					}

					/**
					 * If in the path the
					 */
					if (errorPath.getTargetToken() != null) {
						if (!EM2TypeSetUtil.contains(errorPath.getTargetToken(), typeToken)) {
							OsateDebug.osateDebug("FTAUtils",
									"types do not match on path " + errorPath.getName() + ";types1="
											+ EMV2Util.getPrintName(errorPath.getTargetToken()) + ";types2="
											+ EMV2Util.getPrintName(typeToken));
							continue;
						}

						if (errorPath.getTypeTokenConstraint() == null) {
							subEvents.add(getAllEventsFromPropagationSource(componentSource, errorPath.getIncoming(),
									null, history));
						} else {
							for (TypeToken tt : errorPath.getTypeTokenConstraint().getTypeTokens()) {
								subEvents.add(getAllEventsFromPropagationSource(componentSource,
										errorPath.getIncoming(), tt, history));
							}
						}
					} else {
						if (!EM2TypeSetUtil.contains(errorPath.getTypeTokenConstraint(), typeToken)) {
							OsateDebug.osateDebug("FTAUtils",
									"types do not match on path " + errorPath.getName() + ";types1="
											+ EMV2Util.getPrintName(errorPath.getTypeTokenConstraint()) + ";types2="
											+ EMV2Util.getPrintName(typeToken));
							continue;
						}
						if (errorPath.getTypeTokenConstraint() == null) {
							subEvents.add(getAllEventsFromPropagationSource(componentSource, errorPath.getIncoming(),
									null, history));
						} else {
							for (TypeToken tt : errorPath.getTypeTokenConstraint().getTypeTokens()) {
								subEvents.add(getAllEventsFromPropagationSource(componentSource,
										errorPath.getIncoming(), tt, history));
							}
						}

					}
				}

				/**
				 * If the error source is actually the source of the error
				 * propagation.
				 */
				if (ef instanceof ErrorSource) {
					ErrorSource errorSource = (ErrorSource) ef;

					if (Utils.propagationEndsMatches(propagationSource, errorSource.getOutgoing())) {
						if (EM2TypeSetUtil.contains(errorSource.getTypeTokenConstraint(), typeToken)) {
							Event newEvent = this.createEvent();
							newEvent.setType(EventType.EXTERNAL);
							Utils.fillProperties(newEvent, componentSource, errorSource, ef.getTypeTokenConstraint());
							subEvents.add(newEvent);
						}
					}
				}
			}
		}

		/**
		 * Then, we build the final tree.
		 */
		switch (subEvents.size()) {
		case 0: {
			result = this.createEvent();
			String desc = "Events from component " + component.getName() + " on "
					+ EMV2Util.getPrintName(errorPropagation);
			if (typeToken != null) {
				desc += " with types " + EMV2Util.getPrintName(typeToken);
			}
			desc += " (no error source found)";

			result.setDescription(desc);
			result.setType(EventType.BASIC);
			break;
		}
		case 1: {
			result = subEvents.get(0);
			break;
		}
		default: {
			Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.OR);
			
			for (Event se : subEvents)
			{
				emftaGate.getEvents().add(se);
			}
			
			result = this.createEvent();
			result.setType(EventType.BASIC);
			result.setGate(emftaGate);
			
			
			String desc = "Events from component " + component.getName() + " on "
					+ EMV2Util.getPrintName(errorPropagation);
			if (typeToken != null) {
				desc += " with types " + EMV2Util.getPrintName(typeToken);
			}
			result.setDescription(desc);

		}
		}

		return result;
	}



	/**
	 * Process a condition, either from a component error behavior or a
	 * composite error behavior.
	 * 
	 * @param component
	 *            - the component that contains the condition
	 * @param condition
	 *            - the ConditionExpression to be analyzed
	 * @return a list of events related to the condition
	 */
	public Event processCondition(ComponentInstance component, ConditionExpression condition) {
		Event emftaEvent = this.createEvent();
		emftaEvent.setType(EventType.BASIC);
		// OsateDebug.osateDebug("[FTAUtils] condition=" + condition);

		/**
		 * We have an AND expression, so, we create an EVENT to AND' sub events.
		 */
		if (condition instanceof AndExpression) {
			AndExpression expression;
			Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.AND);
			emftaEvent.setGate(emftaGate);

			emftaEvent.setDescription("Occurrence of all the following events");

			expression = (AndExpression) condition;
			for (ConditionExpression ce : expression.getOperands()) {

				emftaGate.getEvents().add(processCondition(component, ce));
			}
		}



		if (condition instanceof OrExpression) {
			OrExpression expression;
			Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.OR);
			emftaEvent.setGate(emftaGate);


			emftaEvent.setDescription("Occurrence of all the following events");

			expression = (OrExpression) condition;
			for (ConditionExpression ce : expression.getOperands()) {
				emftaGate.getEvents().add( processCondition(component, ce));

			}
		}



		/**
		 * Here, we have a single condition element.
		 */
		if (condition instanceof ConditionElement) {
			ConditionElement conditionElement = (ConditionElement) condition;
			if (conditionElement.getIncoming() != null) {
				// OsateDebug.osateDebug("[FTAUtils] processCondition incoming="
				// + conditionElement.getIncoming());

				/**
				 * Here, we have an error event. Likely, this is something we
				 * can get when we are analyzing error component behavior.
				 */
				if (conditionElement.getIncoming() instanceof ErrorEvent) {
					ErrorEvent errorEvent;


					errorEvent = (ErrorEvent) conditionElement.getIncoming();
					emftaEvent.setType(EventType.BASIC);

					Utils.fillProperties(emftaEvent, component, errorEvent, errorEvent.getTypeSet());
				}

				/**
				 * Here, we have an error propagation. This is notified with the
				 * in propagation within a composite error model.
				 */
				if (conditionElement.getIncoming() instanceof ErrorPropagation) {
					ErrorPropagation errorPropagation;
					Event newEvent;
					errorPropagation = (ErrorPropagation) conditionElement.getIncoming();


					emftaEvent.setType(EventType.EXTERNAL);


					List<Event> contributors = new ArrayList<Event>();
					for (TypeToken tt : conditionElement.getConstraint().getTypeTokens()) {
						contributors.add(
								getAllEventsFromPropagationSource(component, errorPropagation, tt, new Stack<Event>()));
					}

					if (contributors.size() > 0) {
						Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
						emftaEvent.setGate(emftaGate);

						emftaGate.setType(GateType.OR);

						for (Event contributor : contributors)
						{
							emftaGate.getEvents().add(contributor);
						}
					}
				}
			}

				/**
				 * Here, we have a reference to a subcomponent and then, potentially
				 * one of its state. This is what we find in a composite error state
				 * machine.
				 */
				if (conditionElement.getQualifiedState() != null) {
					/**
					 * In the following, it seems that we reference another
					 * component. This is typically the case when the condition is
					 * within an composite error behavior.
					 *
					 * So, we find the referenced component in the component
					 * hierarchy and add all its contributors to the returned
					 * events.
					 */
					// OsateDebug.osateDebug("[FTAUtils] processCondition
					// subcomponents are present, size=" +
					// conditionElement.getSubcomponents().size());
					SubcomponentElement subcomponentElement = conditionElement.getQualifiedState().getSubcomponent();
					Subcomponent subcomponent = subcomponentElement.getSubcomponent();
					ComponentInstance referencedInstance;
					ErrorTypes referencedErrorType;
					referencedInstance = null;
					referencedErrorType = null;
					// OsateDebug.osateDebug("[FTAUtils] subcomponent=" +
					// subcomponent);

					for (ComponentInstance sub : component.getComponentInstances()) {
						// OsateDebug.osateDebug("[FTAUtils] sub=" +
						// sub.getSubcomponent());
						if (sub.getSubcomponent().getName().equalsIgnoreCase(subcomponent.getName())) {
							referencedInstance = sub;
						}
					}

					if ((conditionElement.getConstraint() != null)
							&& (conditionElement.getConstraint().getTypeTokens().size() > 0)) {
						referencedErrorType = conditionElement.getConstraint().getTypeTokens().get(0).getType().get(0);
					}

					// OsateDebug.osateDebug("[FTAUtils] referenced component
					// instance=" + referencedInstance);
					// OsateDebug.osateDebug("[FTAUtils] referenced type=" +
					// referencedErrorType);

					emftaEvent =  processErrorState(referencedInstance, EMV2Util.getState(conditionElement),
							referencedErrorType);
				}
			}

			return emftaEvent;
		}

		/**
		 * Process a component error behavior, analyze its transition and produces a
		 * list of all events that could then be added in a fault-tree.
		 * 
		 * @param component
		 *            - The component under analysis (the one that contains the
		 *            error behavior)
		 * @param state
		 *            - The target states of the transitions under analysis
		 * @param type
		 *            - The type associated with the target state
		 * @return - list of events that are related to the target state in this
		 *         component.
		 */
		public List<Event> processComponentErrorBehavior(ComponentInstance component, ErrorBehaviorState state,
				ErrorTypes type) {
			/**
			 * Depending on the condition, it returns either a single element, an
			 * AND or an OR.
			 */
			List<Event> returnedEvents;

			returnedEvents = new ArrayList<Event>();

			for (ErrorBehaviorTransition transition : EMV2Util.getAllErrorBehaviorTransitions(component)) {
				if (transition.getTarget() == state) {
					returnedEvents.add(processCondition(component, transition.getCondition()));
				}
			}

			return returnedEvents;
		}

		/**
		 * Process a composite error behavior for a component and try to get all
		 * related potential events to add in a FTA
		 * 
		 * @param component
		 *            - the component under analysis
		 * @param state
		 *            - the target state under analysis
		 * @param type
		 *            - the type associated to the target state (if any)
		 * @return - the list of all potential related FTA events
		 */
		public Event processCompositeErrorBehavior(ComponentInstance component, ErrorBehaviorState state,
				ErrorTypes type) {
			/**
			 * Depending on the condition, it returns either a single element, an
			 * AND or an OR.
			 */
			List<Event> subEvents;

			subEvents = new ArrayList<Event>();

			for (CompositeState cs : EMV2Util.getAllCompositeStates(component)) {
				if (cs.getState() == state) {
					subEvents.add(processCondition(component, cs.getCondition()));
				}
			}
			
			if (subEvents.size() == 1)
			{
				return subEvents.get(0);
			}
			else
			{
				Event combined = this.createEvent();
				combined.setType(EventType.BASIC);
				Utils.fillProperties(combined, component, state, null);
				Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
				emftaGate.setType(GateType.OR);
				
				combined.setGate(emftaGate);
				
				for (Event se : subEvents)
				{
					emftaGate.getEvents().add(se);
				}
				
				return combined;
			}
			
		}

		/**
		 * Process a particular error behavior state and try to get all potential
		 * error contributors, either from the component error behavior or the
		 * composite error behavior.
		 *
		 * @param component
		 *            - the component under analysis
		 * @param state
		 *            - the failure mode under analysis
		 * @param type
		 *            - the type related to the failure mode (null if not useful)
		 * @return - a node that represents either the single failure state or an
		 *         AND- or OR- nodes if several.
		 */
		public Event processErrorState(ComponentInstance component, ErrorBehaviorState state, ErrorTypes type) {
			Event errorStateEvent = this.createEvent();
			errorStateEvent.setType(EventType.BASIC);

			Utils.fillProperties(errorStateEvent, component, state, state.getTypeSet());

			List<Event> subEvents = new ArrayList<Event>();

			subEvents.addAll(processComponentErrorBehavior(component, state, type));
			subEvents.add(processCompositeErrorBehavior(component, state, type));

			if (subEvents.size() > 0) {
				Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
				emftaGate.setType(GateType.OR);
				errorStateEvent.setGate(emftaGate);

				for (Event e : subEvents) {
					emftaGate.getEvents().add(e);
				}
			}

			// /**
			// * If we have only one subevent, we directly attach it to the main
			// * event.
			// */
			// if (subEvents.size() == 1) {
			// return subEvents.get(0);
			//
			// // The following code is commented. When we have only one sub event,
			// // it does
			// // not seem to make sense to continue and try to process more
			// // events.
			//
			// // if (subEvents.get(0).getEventType() == EventType.NORMAL) {
			// // /**
			// // * If the subevent is also a normal event, we directly return
			// // * it and bypass the other one.
			// // */
			// // return subEvents.get(0);
			// // } else {
			// // /**
			// // * In that case, here, we have an event. We add it directly.
			// // */
			// // returnedEvent.addSubEvent(subEvents.get(0));
			// // }
			// }
			//
			// /**
			// * If we have several intermediate subevents, we consider that each
			// one
			// * is independent. So, we make an OR gate to connect all these events
			// * altogether.
			// */
			// if (subEvents.size() > 1) {
			// // OsateDebug.osateDebug("FTAUtils", "More than one event, needs to
			// // make an or");
			// Event intermediateEvent = new Event();
			// intermediateEvent.setEventType(EventType.OR);
			// intermediateEvent.setDescription("Composition of one of the following
			// events on " + component.getName());
			//
			// for (Event ev : subEvents) {
			// intermediateEvent.addSubEvent(ev);
			// }
			// returnedEvent.addSubEvent(intermediateEvent);
			// }

			return errorStateEvent;

		}

	}
