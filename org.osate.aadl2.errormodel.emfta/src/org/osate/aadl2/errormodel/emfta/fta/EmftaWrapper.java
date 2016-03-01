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
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.SubcomponentElement;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeSet;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeToken;
import org.osate.xtext.aadl2.errormodel.util.AnalysisModel;
import org.osate.xtext.aadl2.errormodel.util.EM2TypeSetUtil;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.errormodel.util.PropagationPathEnd;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.Event;
import edu.cmu.emfta.EventType;
import edu.cmu.emfta.Gate;
import edu.cmu.emfta.GateType;

public class EmftaWrapper {
	private edu.cmu.emfta.FTAModel emftaModel;
	private AnalysisModel currentAnalysisModel;
	private ComponentInstance rootComponent;
	private ErrorBehaviorState rootComponentState;
	private ErrorTypes rootComponentTypes;
	private int eventIdentifier;

	public Map<String, edu.cmu.emfta.Event> cache;
	
	private String buildIdentifier (ComponentInstance component, NamedElement namedElement, TypeSet typeSet)
	{
		String identifier = eventIdentifier + "-" + buildName (component,namedElement, typeSet);
		identifier = identifier.replaceAll("\\{", "").replaceAll("\\}", "").toLowerCase();
		return identifier;
	}
	
	private String buildName (ComponentInstance component, NamedElement namedElement, TypeSet typeSet)
	{
		String identifier;
		
		identifier = component.getName();
		identifier += "-";
		
		if (namedElement == null)
		{
			identifier += "unidentified";
			
		}
		else
		{
			if (namedElement instanceof ErrorPropagation)
			{
				ErrorPropagation ep = (ErrorPropagation) namedElement;
				identifier += ep.getName();
			}
			else if (namedElement instanceof ErrorEvent)
			{
				ErrorEvent ev = (ErrorEvent) namedElement;
				identifier += ev.getName();
			}
			else if (namedElement instanceof ErrorBehaviorState)
			{
				ErrorBehaviorState ebs = (ErrorBehaviorState) namedElement;
				identifier += ebs.getName();
			}
			else if (namedElement instanceof ErrorSource)
			{
				ErrorSource es = (ErrorSource) namedElement;
				identifier += es.getName();
			}
			else
			{
				identifier += "unknown";
			}
		}
		
		identifier += "-";
		
		if (typeSet == null)
		{
			identifier += "notypes";
		}
		else
		{
			identifier += EMV2Util.getPrintName(typeSet);
		}
		
		return identifier;
	}
	
	private Event getFromCache (ComponentInstance component, NamedElement namedElement, TypeSet typeSet)
	{
		if (cache.containsKey(buildName(component, namedElement, typeSet)))
		{
			return cache.get(buildName(component, namedElement, typeSet));
		}
		return null;
	}
	
	
	private Event createEvent (ComponentInstance component, NamedElement namedElement, TypeSet typeSet, boolean putInCache)
	{
		Event newEvent = EmftaFactory.eINSTANCE.createEvent();
		String identifier = buildIdentifier(component, namedElement, typeSet);
		emftaModel.getEvents().add(newEvent);
		newEvent.setName(identifier);
		newEvent.setGate(null);
		eventIdentifier = eventIdentifier + 1;
		
		if (putInCache)
		{
			cache.put(buildName(component, namedElement, typeSet), newEvent);
		}
		
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
		eventIdentifier = 0;
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
							
							Event newEvent;
							
							newEvent = getFromCache(componentSource, errorSource, ef.getTypeTokenConstraint());
							if (newEvent == null)
							{
								newEvent = this.createEvent(componentSource, errorSource, ef.getTypeTokenConstraint(), false);
								newEvent.setType(EventType.EXTERNAL);
								Utils.fillProperties(newEvent, componentSource, errorSource, ef.getTypeTokenConstraint());
							}

							subEvents.add(newEvent);
						}
					}
				}
			}
		}

		/**
		 * Then, we build the final tree.
		 */
		if (subEvents.size() == 0)
		{
			result = getFromCache(component, errorPropagation, null);
			if (result == null)
			{
				result = this.createEvent(component, errorPropagation, null, false);
				
				String desc = "Events from component " + component.getName() + " on "
						+ EMV2Util.getPrintName(errorPropagation);
				if (typeToken != null) {
					desc += " with types " + EMV2Util.getPrintName(typeToken);
				}
				desc += " (no error source found)";
	
				result.setDescription(desc);
				result.setType(EventType.BASIC);
			}
			return result;
		}
		
		if (subEvents.size() == 1) {
			return subEvents.get(0);
		}
		
		if (subEvents.size() > 1)
		{
	
			result = getFromCache(component, errorPropagation, null);
			if (result == null)
			{
				result = this.createEvent(component, errorPropagation, null, false);

				
				result.setType(EventType.BASIC);
	
				String desc = "Events from component " + component.getName() + " on "
						+ EMV2Util.getPrintName(errorPropagation);
				if (typeToken != null) {
					desc += " with types " + EMV2Util.getPrintName(typeToken);
				}
				result.setDescription(desc);
	
	
				Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
				emftaGate.setType(GateType.OR);
	
				for (Event se : subEvents)
				{
					emftaGate.getEvents().add(se);
				}
				result.setGate(emftaGate);
			}
			return result;
		
		}

		return null;
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

		// OsateDebug.osateDebug("[FTAUtils] condition=" + condition);

		/**
		 * We have an AND expression, so, we create an EVENT to AND' sub events.
		 */
		if (condition instanceof AndExpression) {
			AndExpression expression;
			Event emftaEvent = EmftaFactory.eINSTANCE.createEvent();
			emftaModel.getEvents().add(emftaEvent);
			
			
			emftaEvent.setType(EventType.BASIC);
			Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.AND);
			emftaEvent.setGate(emftaGate);

			emftaEvent.setDescription("Occurrence of all the following events");

			expression = (AndExpression) condition;
			List<Event> subEvents = new ArrayList<Event>();
			
			for (ConditionExpression ce : expression.getOperands()) {
				subEvents.add(processCondition(component,ce));
			}
			
			for (Event e : subEvents)
			{
				emftaGate.getEvents().add(e);
			}
			return emftaEvent;
		}



		if (condition instanceof OrExpression) {
			OrExpression expression;
			Event emftaEvent = EmftaFactory.eINSTANCE.createEvent();
			emftaModel.getEvents().add(emftaEvent);
			
			emftaEvent.setType(EventType.BASIC);

			Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
			emftaGate.setType(GateType.OR);
			emftaEvent.setGate(emftaGate);


			emftaEvent.setDescription("Occurrence of at least one the following events");

			expression = (OrExpression) condition;
			for (ConditionExpression ce : expression.getOperands()) {
				emftaGate.getEvents().add( processCondition(component, ce));
			}
			return emftaEvent;
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
					Event emftaEvent;
					
					errorEvent = (ErrorEvent) conditionElement.getIncoming();
					
					emftaEvent = getFromCache(component, errorEvent, errorEvent.getTypeSet());
					
					if (emftaEvent == null)
					{
						emftaEvent = this.createEvent(component, errorEvent, errorEvent.getTypeSet(), false);
						emftaEvent.setType(EventType.BASIC);

						errorEvent = (ErrorEvent) conditionElement.getIncoming();
						emftaEvent.setType(EventType.BASIC);

						Utils.fillProperties(emftaEvent, component, errorEvent, errorEvent.getTypeSet());
					}
					
					return emftaEvent;
				}

				/**
				 * Here, we have an error propagation. This is notified with the
				 * in propagation within a composite error model.
				 */
				if (conditionElement.getIncoming() instanceof ErrorPropagation) {
					ErrorPropagation errorPropagation;


					errorPropagation = (ErrorPropagation) conditionElement.getIncoming();


					List<Event> contributors = new ArrayList<Event>();
					for (TypeToken tt : conditionElement.getConstraint().getTypeTokens()) {
						contributors.add(
								getAllEventsFromPropagationSource(component, errorPropagation, tt, new Stack<Event>()));
					}

					if (contributors.size() == 0) {
						Event emftaEvent;
						emftaEvent = getFromCache(component, errorPropagation, errorPropagation.getTypeSet());
						
						if (emftaEvent == null)
						{
							emftaEvent = this.createEvent(component, errorPropagation, errorPropagation.getTypeSet(), false);
							emftaEvent.setType(EventType.EXTERNAL);
							Utils.fillProperties(emftaEvent, component, errorPropagation, errorPropagation.getTypeSet());

						}
						return emftaEvent;
					}

					if (contributors.size() == 1) {
						return contributors.get(0);
					}

					if (contributors.size() > 1) {
						
						Event emftaEvent;
						emftaEvent = getFromCache(component, errorPropagation, errorPropagation.getTypeSet());
						
						if (emftaEvent == null)
						{
							emftaEvent = this.createEvent(component, errorPropagation, errorPropagation.getTypeSet(), false);
							emftaEvent.setType(EventType.BASIC);
							Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
							emftaEvent.setGate(emftaGate);
	
							emftaGate.setType(GateType.OR);
	
							for (Event contributor : contributors)
							{
								emftaGate.getEvents().add(contributor);
							}
						}
						return emftaEvent;
					}


					return null;
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

					return  processErrorState(referencedInstance, EMV2Util.getState(conditionElement),
							referencedErrorType);
				}
			}

			return null;
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
		public Event processComponentErrorBehavior(ComponentInstance component, ErrorBehaviorState state,
				ErrorTypes type) {
			/**
			 * Depending on the condition, it returns either a single element, an
			 * AND or an OR.
			 */
			List<Event> subEvents;

			subEvents = new ArrayList<Event>();

			for (ErrorBehaviorTransition transition : EMV2Util.getAllErrorBehaviorTransitions(component)) {
				if (transition.getTarget() == state) {
					subEvents.add(processCondition(component, transition.getCondition()));
				}
			}

			if (subEvents.size() == 1)
			{
				return subEvents.get(0);
			}
			if (subEvents.size() > 1)
			{
				
				Event combined;
				combined = getFromCache(component, state, state.getTypeSet());
				
				if (combined == null)
				{
					combined = this.createEvent(component, state, state.getTypeSet(), false);
					combined.setType(EventType.BASIC);
					Utils.fillProperties(combined, component, state, null);
					Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
					emftaGate.setType(GateType.OR);

					combined.setGate(emftaGate);

					for (Event se : subEvents)
					{
						emftaGate.getEvents().add(se);
					}
				}
			
				return combined;
			}

			return null;
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
			if (subEvents.size() > 1)
			{
				Event combined = this.createEvent(component, state, state.getTypeSet(), false);
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
			return null;

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

			Event compositeBehaviorEvent;
			Event componentBehaviorEvent;


			List<Event> subEvents = new ArrayList<Event>();

			componentBehaviorEvent = processComponentErrorBehavior(component, state, type);
			if (componentBehaviorEvent != null)
			{
				subEvents.add(componentBehaviorEvent);
			}

			compositeBehaviorEvent = processCompositeErrorBehavior(component, state, type);
			if (compositeBehaviorEvent != null)
			{
				subEvents.add(compositeBehaviorEvent);
			}

			if (subEvents.size() == 0)
			{
				Event errorStateEvent = this.createEvent(component, state, state.getTypeSet(), false);
				errorStateEvent.setType(EventType.BASIC);
				Utils.fillProperties(errorStateEvent, component, state, state.getTypeSet());
				errorStateEvent.setGate(null);
				return errorStateEvent;
			}

			if(subEvents.size() == 1)
			{
				return subEvents.get(0);
			}

			if (subEvents.size() > 0) {
				Event errorStateEvent = this.createEvent(component, state, state.getTypeSet(), false);
				errorStateEvent.setType(EventType.BASIC);
				Utils.fillProperties(errorStateEvent, component, state, state.getTypeSet());
				Gate emftaGate = EmftaFactory.eINSTANCE.createGate();
				emftaGate.setType(GateType.OR);
				errorStateEvent.setGate(emftaGate);

				for (Event e : subEvents) {
					emftaGate.getEvents().add(e);
				}
				return errorStateEvent;
			}

			return null;

		}

	}
