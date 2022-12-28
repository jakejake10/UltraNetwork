package utCore;

import java.util.List;
//import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;

public class Node implements Iterable<Node> {
	UltraTree system;
	public int parentLoc = -1;
	public int myLoc = -1;
	public int firstChild = -1;
	public int size = 0;
	// int dataLoc = -1;
	public int depth = -1;
	
	
	// CONSTRUCTORS ////////////////////////////////////////////////////////

	public Node(UltraTree system) {
		this.system = system;
	}

	public Node(UltraTree system, String mode) {
		this(system);
		switch (mode) {
		case "root":
			if (system.nodes.size() > 0)
				throw new IllegalStateException("node list must be empty before adding root node");
			myLoc = 0;
			depth = 0;
			system.nodes.add(this);
			break;
		}
	}
	
//	public <E> Node( E val, List<E> vals , UltraTree system, String... mode ){	// for subclasses working with parallel data lists
//		this( system, mode.length > 0 ? mode[0] : "");
//		vals.add(val);
//	}
	
	
	// CHILDREN ///////////////////////////////////////////////////
	
	// add at index, new int parameter
	public void addChild() {
		addChild(new Node(system));
	}

	public <E> void addChild(E input, List<E> inputList) {
		addChild();
		// lastChild().dataLoc = inputList.size();
		inputList.add(myLoc, input);
	}

	public void addChild(Node child) {
		int childIndex = !hasChildren() ? system.nodes.size() : firstChild + size; // myLoc + 1 + size;
		if (!hasChildren())
			firstChild = childIndex;
		child.myLoc = childIndex;
		child.parentLoc = myLoc;
		child.depth = depth + 1;
		updateNodes(childIndex, 1);
		system.nodes.add(childIndex, child); // add to index after loc, and after any pre existing children
		size++;
	}
	

	public void updateNodes(int index, int amount) {
		if (index == system.nodes.size())
			return;
		for (int i = 0; i < system.nodes.size(); i++) {
			Node curNode = system.nodes.get(i);
			if (curNode.myLoc >= index)
				curNode.myLoc += amount;
			if (curNode.firstChild >= index)
				curNode.firstChild += amount;
		}
	}
	
	// CHECK FNS ///////////////////////////////////////////////////////////////////////////

	public boolean hasParent() {
		return parentLoc > -1;
	}
	public boolean hasChildren() {
		return firstChild > -1;
	}
	
	public boolean isLeaf() {
		return firstChild == -1;
	}

	public boolean isRoot() {
		return parentLoc == -1;
	}

	
	
	// GET FNS ////////////////////////////////////////////////////////////////

	
	public Node get(int index) {
		return system.nodes.get(firstChild + index);
	}
	
	public Node getParent() {
		return system.nodes.get(parentLoc);
	}
	
	public Node getRoot() {
		return hasParent() ? getParent() : this;
	}
	
	public Node lastChild() {
		return get(size - 1);
	}

	public <E> E getVal(List<E> inputList) {
		try {
			return inputList.get(myLoc);
		} catch (Exception e) {
			return null;
		}
	}
	
	public <E> void setVal( E val, List<E> inputList ){
		if( myLoc != inputList.size() ) inputList.set(myLoc, val);
		else  inputList.add(val);
	}

	public int childCount() {
		return size;
	}

	public String toString() {
		return "parentLoc = " + parentLoc + ", myLoc = " + myLoc + ", size = " + size + ", depth = " + depth;
	}

	public void applyFn(Consumer<Node> fn) {
		fn.accept(this);
	}

	public <E, R> E applyFn(BiFunction<Node, R, E> fn) {
		return fn.apply(this, null);
	}

	public <E, R> E applyFn(R input, BiFunction<Node, R, E> fn) {
		return fn.apply(this, input);
	}

	// ITERATOR ///////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	public Iterator<Node> iterator() {
		return new NodeChildIterator();
	}

	public class NodeChildIterator implements Iterator<Node> {
		int index = 0;

		// constructor
		public NodeChildIterator() {
		} // initialize cursor here if needed

		// Checks if the next element exists
		public boolean hasNext() {
			return index < size;
		}

		// moves the cursor/iterator to next element
		public Node next() {
			Node out = get(index);
			index++;
			return out;
		}

		// Used to remove an element. Implement only if needed
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
