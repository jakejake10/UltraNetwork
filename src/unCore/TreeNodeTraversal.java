package unCore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface TreeNodeTraversal<N extends TreeNodeObject<N> & Iterable<N>> {

	N getInstance();

	N get(int childIndex);

//	int getChildCount();
	SingularTreeData<N> getCore();

//	int getDepth();
	boolean hasChildren();

	boolean hasParent();

	N getParent();

	int getIndexInParent();

	boolean isRoot();
//	int getTotalSize();

	/*
	 * performance boost over for( N child : getChildren() ) new list does not need
	 * to be created
	 */
	public default void forEachChild(Consumer<N> fn) {
		for (int i = 0; i < getInstance().getChildCount(); i++) {
			fn.accept(get(i));
		}
	}

	/*
	 * performance boost over for( N leaf : getLeafs() ) new list does not need to
	 * be created faster than cached leaf list in benchmark test
	 */
	public default void forEachLeafRecursive(Consumer<N> fn) {
		if (getInstance().getChildCount() == 0) {
			fn.accept(getInstance());
			return;
		}
		for (int i = 0; i < getInstance().getChildCount(); i++) {
			get(i).forEachLeafRecursive(fn);
		}
	}

	/*
	 * if root, uses cached list if not, does regular recursive operation
	 */
	public default void forEachLeafCached(Consumer<N> fn) {
		SingularTreeData<N> core = getCore();

		if (core == null) {
			forEachLeafRecursive(fn);
			return;
		}

		if (isRoot()) {
			ensureLeafCache();
			for (int i = 0; i < core.leafIndexes.size(); i++) {
				fn.accept(core.nodeList.get(core.leafIndexes.get(i)));
			}
			return;
		}

		// subtree fallback
		forEachLeafRecursive(fn);
	}

	// ITERATOR //////////////////////////////////////

	public default Iterator<N> nodeIterator() {
		return new DFSTraversal<N>(getInstance()); // iterator traversal naturally adds in reverse;
	}

	public default Iterable<N> dft() {
		return makeIterable(new DFSTraversal<N>(getInstance()));
	}

	public default Iterable<N> bft() {
		return makeIterable(new BFSTraversal<N>(getInstance()));
	}

	// can make static
	public default <E> Iterable<E> makeIterable(Iterator<E> type) {
		return new Iterable<E>() {
			public Iterator<E> iterator() {
				return type;
			}
		};
	}

//   cant use size() in traversal, because size() fn uses traversal
	public class BFSTraversal<T extends TreeNodeObject<T> & Iterable<T>> implements Iterator<T> {
		public Queue<T> traversal = new ArrayDeque<>();
		boolean[] traversed;

		public BFSTraversal(T startNode) {
			traversal.add(startNode);
			traversed = new boolean[startNode.getTotalSize()];
			traversed[startNode.getIndex()] = true;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();

			T n = traversal.remove();

			n.forEachChild(nc -> {
				if (!traversed[nc.getIndex()]) {
					traversed[nc.getIndex()] = true;
					traversal.add(nc);
				}
			});

			return n;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * traverse a list of objects using node structure
	 */
	public class DFSTraversal<T extends TreeNodeObject<T> & Iterable<T>> implements Iterator<T> {
		T nextNode;
		int rootDepth;
		boolean down = true;

		public DFSTraversal(T startNode) {
			this.nextNode = startNode;
			rootDepth = startNode.getDepth();
		}

		public boolean hasNext() {
			return nextNode != null;
		}

		public T next() {
			T curNode = nextNode;
			nextNode = getNextNodeDFSRecursiveFn(curNode, rootDepth);
			return curNode;
		}

		T getNextNodeDFSRecursiveFn(T curNode, int minDepth) {
			if (down && curNode.hasChildren())
				return curNode.get(0);
			else {
				if (!curNode.hasParent() || curNode.getDepth() <= minDepth)
					return null;
				if (curNode.getParent().getChildCount() - 1 > curNode.getIndexInParent()) {
					down = true;
					return curNode.getParent().get(curNode.getIndexInParent() + 1);
				} else {
					down = false;
					return getNextNodeDFSRecursiveFn(curNode.getParent(), minDepth);
				}
			}
		}

		public void remove() {
			// throws UnsupportedOperationException.
		}
	}

	/*
	
	*/

	public default N getNextNodeDFS(int startDepth) {
		return getNextNodeDFSRecursiveFn(true, startDepth); // depth of original node needed for each iteration
	}

	default N getNextNodeDFSRecursiveFn(boolean down, int startDepth) {
		if (down && hasChildren())
			return get(0);
		else {
			if (!hasParent() || getParent().getDepth() < startDepth)
				return null; // changed to < from <=
			else if (getParent().getChildCount() - 1 > getIndexInParent()) {
				down = true;
				return getParent().get(getIndexInParent() + 1);
			} else
				return getParent().getNextNodeDFSRecursiveFn(false, startDepth);
		}
	}

	public default N getNextNodeBFS(int startDepth) {
		N root = getInstance().getAncestorAtDepth(startDepth);
		if (root == null)
			return null;

		Queue<N> queue = new LinkedList<>();
		queue.add(root);

		while (!queue.isEmpty()) {
			N current = queue.poll();

			// Check if we’ve found the current node
			if (current == this) {
				// Return next in queue (i.e. next in BFS order)
				return queue.isEmpty() ? null : queue.peek();
			}

			// Enqueue children of the current node
			for (int i = 0; i < current.getChildCount(); i++) {
				queue.add(current.get(i));
			}
		}

		return null; // this node was not found in BFS traversal
	}

	// NODE OPERATIONS //////////////////////////////////////////////////////

	/*
	 * - recursive function for building, copying and converting trees to different
	 * types - iterates from the node that calls the operation - E = input data
	 * object, nodeFn<data, current iterated node N > - can build tree by adding
	 * children to current node calling fn, but will not work if adding children
	 * earlier in tree
	 */
//		public default <E> void traverseOperation(E data, BiConsumer<E, N> nodeFn) {
//			traverseOperationFn(data, nodeFn, getInstance().getDepth());
//		}
//
//		default <E> void traverseOperationFn(E data, BiConsumer<E, N> nodeFn, int startDepth) {
//			nodeFn.accept(data, getInstance());
//			N nextNode = getNextNodeDFS(startDepth);
//			if (nextNode == null)
//				return; // reached last node
//			nextNode.traverseOperationFn(data, nodeFn, startDepth);
//		}
//		
//		default void nodeOperation( Consumer<N> fn ) {
//			traverseOperation( null, (e,n) -> fn.accept(n) );
//		}

	// rename the existing one - keeps backward compat for anything using the data
	// passing pattern
	public default <E> void forEachNode(E data, BiConsumer<E, N> nodeFn) {
		forEachNodeFn(data, nodeFn, getInstance().getDepth());
	}

	// new clean overload - covers 90% of use cases
	public default void forEachNode(Consumer<N> fn) {
		forEachNodeFn(null, (e, n) -> fn.accept(n), getInstance().getDepth());
	}

	// rename traverseOperationFn -> forEachNodeFn (internal, not public API)
	default <E> void forEachNodeFn(E data, BiConsumer<E, N> nodeFn, int startDepth) {
		nodeFn.accept(data, getInstance());
		N nextNode = getNextNodeDFS(startDepth);
		if (nextNode == null)
			return;
		nextNode.forEachNodeFn(data, nodeFn, startDepth);
	}

//	public default <E> void bottomUpOperation(E data, BiConsumer<E, N> nodeFn) { // recurses operation to children, then
//																					// runs fn
//		if (hasChildren()) {
//			this.forEachChild(nc -> nc.bottomUpOperation(data, nodeFn)); // used for ar calc);
//		}
//		nodeFn.accept(data, getInstance()); // after applying operation recursively to children, function then accepts
//	}
	
	// rename, add Consumer overload
	public default <E> void forEachNodeBottomUp(E data, BiConsumer<E, N> nodeFn) {
	    if (hasChildren()) {
	        forEachChild(nc -> nc.forEachNodeBottomUp(data, nodeFn));
	    }
	    nodeFn.accept(data, getInstance());
	}

	public default void forEachNodeBottomUp(Consumer<N> fn) {
	    forEachNodeBottomUp(null, (e, n) -> fn.accept(n));
	}

	// CACHING ////////////////////////////////////////////////////////////////

	public default void ensureLeafCache() {
		SingularTreeData<N> core = getCore();
		if (core == null)
			return;
		if (core.leafIndexes == null || core.leafCacheDirty)
			rebuildLeafCache();
	}

	public default void markLeafCacheDirty() {
		SingularTreeData<N> core = getCore();
		if (core != null)
			core.leafCacheDirty = true;
	}

	public default void rebuildLeafCache() {
		SingularTreeData<N> core = getCore();
		if (core == null)
			return;

		if (core.leafIndexes == null)
			core.leafIndexes = new ArrayList<>();
		else
			core.leafIndexes.clear();

		for (int i = 0; i < core.nodeList.size(); i++) {
			N node = core.nodeList.get(i);
			if (node.getChildCount() == 0) {
				core.leafIndexes.add(i);
			}
		}

		core.leafCacheDirty = false;
	}

}
