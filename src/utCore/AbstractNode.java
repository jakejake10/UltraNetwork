package utCore;

import java.util.List;
//import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;

public abstract class AbstractNode<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N> {
	T myTree;
	public int parentLoc = -1;
	public int myLoc = -1;
	public int firstChild = -1;
	public int size = 0;
	// int dataLoc = -1;
	public int depth = -1;
	
	
	// CONSTRUCTORS ////////////////////////////////////////////////////////

//	public AbstractNode(AbstractTree system) {
//		this.system = system;
//	}

	public AbstractNode(T system, String... mode) {
		this.myTree = system;
		if( mode.length == 0 ) return;	// not needed?
		switch (mode[0]) {
		case "root":
			if (system.nodes.size() > 0)
				throw new IllegalStateException("node list must be empty before adding root node");
			myLoc = 0;
			depth = 0;
//			system.nodes.add(this);
			addToTree();
			break;
		}
	}
	
	// ABSTRACT FNS /////////////////////////////////////////////////////////////////
	
	abstract N defaultConstructor( T system, String... mode );
	abstract void addToTree();
	abstract N getInstance();
	
//	public <E> Node( E val, List<E> vals , UltraTree system, String... mode ){	// for subclasses working with parallel data lists
//		this( system, mode.length > 0 ? mode[0] : "");
//		vals.add(val);
//	}
	
	
	// CHILDREN ///////////////////////////////////////////////////
	
	// add at index, new int parameter
	public void addChild() {
		addChild( defaultConstructor( myTree ) );
	}

	public <E> void addChild(E input, List<E> inputList) {
		addChild();
		// lastChild().dataLoc = inputList.size();
		inputList.add(myLoc, input);
	}

	public void addChild( N child) {
		int childIndex = !hasChildren() ? myTree.nodes.size() : firstChild + size; // myLoc + 1 + size;
		if (!hasChildren())
			firstChild = childIndex;
		child.myLoc = childIndex;
		child.parentLoc = myLoc;
		child.depth = depth + 1;
		updateNodes(childIndex, 1);
		myTree.nodes.add(childIndex, child); // add to index after loc, and after any pre existing children
		size++;
	}
	

	public void updateNodes(int index, int amount) {
		if (index == myTree.nodes.size())
			return;
		for (int i = 0; i < myTree.nodes.size(); i++) {
			N curNode = myTree.nodes.get(i);
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

	
	public N get(int index) {
		return myTree.nodes.get(firstChild + index);
	}
	
	public N getParent() {
		return myTree.nodes.get(parentLoc);
	}
	
	public AbstractNode<T,N> getRoot() {
		return hasParent() ? getParent() : this;
	}
	
	public N lastChild() {
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

	public void applyFn(Consumer<N> fn ) {
		fn.accept( getInstance() );
	}

	public <E, R> E applyFn(BiFunction<N, R, E> fn) {
		return fn.apply( getInstance(), null );
	}

	public <E, R> E applyFn(R input, BiFunction<N, R, E> fn) {
		return fn.apply( getInstance(), input );
	}

	// ITERATOR ///////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	public Iterator<N> iterator() {
		return new NodeChildIterator();
	}

	public class NodeChildIterator implements Iterator<N> {
		int index = 0;

		// constructor
		public NodeChildIterator() {
		} // initialize cursor here if needed

		// Checks if the next element exists
		public boolean hasNext() {
			return index < size;
		}

		// moves the cursor/iterator to next element
		public N next() {
			N out = get(index);
			index++;
			return out;
		}

		// Used to remove an element. Implement only if needed
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
