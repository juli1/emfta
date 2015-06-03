package edu.cmu.emfta.actions;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.emfta.Event;
import edu.cmu.emfta.Gate;
import edu.cmu.emfta.Tree;

public class CutSet {
	private List<List<Event>> cutset;

	private Tree tree;

	public CutSet(Tree ftaTree) {
		System.out.println("[CutSet] constructor");
		cutset = new ArrayList<List<Event>>();
		tree = ftaTree;
	}

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

	public void process() {
		System.out.println("[CutSet] processing");
		List<Event> current;
		current = new ArrayList<Event>();

		processGate(tree.getGate(), current);
	}

	public List<Event> copyList(List<Event> source) {
		List<Event> newList;
		newList = new ArrayList<Event>();

		for (Event e : source) {
			newList.add(e);
		}
		return newList;
	}

	public void processGate(Gate gate, List<Event> current) {

		System.out.println("[CutSetAction] calling processGate");
		System.out.println("[CutSetAction] gate = " + gate);

		switch (gate.getType()) {
		case AND: {

			for (Gate g : gate.getGates()) {
				processGate(g, current);
			}
			for (Event e : gate.getEvents()) {
				processEvent(e, current);
				;
			}
			break;
		}

		case OR: {
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

	private void processEvent(Event event, List<Event> current) {

		System.out.println("[CutSetAction] calling processEvent");
		System.out.println("[CutSetAction] event = " + event.getName());
		current.add(event);

	}

}
