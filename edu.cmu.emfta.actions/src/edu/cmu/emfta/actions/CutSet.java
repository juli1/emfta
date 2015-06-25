package edu.cmu.emfta.actions;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.emfta.Event;
import edu.cmu.emfta.Gate;
import edu.cmu.emfta.Tree;

/**
 * This class represents the cutset for an FTA
 * The Cutset gathers the list of events required
 * to trigger a fault.
 * 
 * @author julien
 *
 */
public class CutSet {
	private List<List<Event>> cutset;

	private Tree tree;

	/**
	 * Constructor - just pass the FTA as parameter
	 * @param ftaTree
	 */
	public CutSet(Tree ftaTree) {
		System.out.println("[CutSet] constructor");
		cutset = new ArrayList<List<Event>>();
		tree = ftaTree;
	}

	/**
	 * Return the list of cutset. A cutset is a list
	 * of events that will ultimately trigger an occurence
	 * of the main error (top of the FTA).
	 * @return A list of list of event.
	 */
	public List<List<Event>> getCutset() {
		return cutset;
	}

	/**
	 * Output a string of the FTA. Should be used
	 * for debug purpose. If one want to extract
	 * the events, use the getCutset() method.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append("Generated Cutset\n");

		i = 0;

		for (List<Event> events : cutset) {
			sb.append("Cutset " + i + ": ");
			for (Event event : events) {
				sb.append(event.getName().toString());
				sb.append(" ");
			}
			sb.append("\n");
			i++;
		}
		return sb.toString();
	}

	public String toCSV() {
		StringBuffer sb = new StringBuffer();
		int i;

		sb.append("Generated Cutset\n");

		i = 0;

		for (List<Event> events : cutset) {
			sb.append("#" + i + ",");
			for (Event event : events) {
				sb.append(event.getName().toString());
				sb.append(",");
			}
			sb.append("\n");
			i++;
		}
		return sb.toString();
	}

	/**
	 * main method that trigger the process
	 * of the FTA (generate everything)
	 */
	public void process() {
		System.out.println("[CutSet] processing");
		List<Event> current;
		current = new ArrayList<Event>();

		processGate(tree.getGate(), current);
	}

	/**
	 * Used to copy a list of events. Used when
	 * we hit an OR gate - the existing list is 
	 * copied for both branches of the gate
	 * and added to the list of cutset.
	 * @param source
	 * @return
	 */
	public List<Event> copyList(List<Event> source) {
		List<Event> newList;
		newList = new ArrayList<Event>();

		for (Event e : source) {
			newList.add(e);
		}
		return newList;
	}

	/**
	 * Process a gate object in the FTA. A gate is either
	 * an AND or an OR. When hitting an AND, it means that
	 * we mean all the gates/events attached to him to occur
	 * to trigger the event. When this is an OR, we then create
	 * separate cutset for each branch or the OR.
	 * 
	 * @param gate    - the actual gate to process
	 * @param current - the current list of cutset that was
	 *                  used when processing the parents of the gates.
	 */
	public void processGate(Gate gate, List<Event> current) {
		System.out.println("[CutSetAction] calling processGate");
		System.out.println("[CutSetAction] gate = " + gate);

		switch (gate.getType()) {
		case AND: {
			/**
			 * When we hit an AND, we just add
			 * the events/gates to the current
			 * cutset. No need to duplicate it.
			 */
			for (Gate g : gate.getGates()) {
				processGate(g, current);
			}
			for (Event e : gate.getEvents()) {
				processEvent(e, current);

			}
			break;
		}

		case OR: {
			/**
			 * When we hit an OR, we then copy the current list
			 * of the FTA and continue to process each branch.
			 * It then makes a separate cutset for each
			 * branch of the OR gate.
			 */
			List<Event> tmpList;

			for (Gate g : gate.getGates()) {
				tmpList = copyList(current);
				processGate(g, tmpList);
				cutset.add(tmpList);
			}
			for (Event e : gate.getEvents()) {
				tmpList = copyList(current);
				processEvent(e, tmpList);
				cutset.add(tmpList);
			}
			break;
		}

		default: {
			System.out.println("[CutSetAction] default choice not implemented");
			break;
		}
		}
	}

	/**
	 * Process an event from the FTA. Event are leaf nodes.
	 * @param event    - the event to process
	 * @param current  - the current cutset
	 */
	private void processEvent(Event event, List<Event> current) {

		System.out.println("[CutSetAction] calling processEvent");
		System.out.println("[CutSetAction] event = " + event.getName());
		current.add(event);

	}

}
