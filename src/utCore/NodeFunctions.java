package utCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * - node class which can access it's saved index - keeps old index after
 * ordering, allowing externalList to be ordered - after updating external
 * lists, internal index can be updated to match actual order - can you make a
 * bind() method to create a class with extra fields? - can't extend the
 * basicnode class, getInstance() still returns basicnode unless abstractnode
 * extended - probably easier just to use as is
 */

interface NodeFunctions<N extends NodeFunctions<N> & Iterable<N> & Comparable<N>> {

	// ABSTRACT METHODS /////////////////////
	public int index();

	public void updateIndex();

	public int size();

	public int treeSize();

	public N getInstance();

	public List<N> nodeList();

	public Function<N, List<N>> dfsNodeGatherFn();
	public Function<N, List<N>> bfsNodeGatherFn();

	// MAKELIST FNS ///////////////////////////////

	public default <E> List<E> makeList() {
		List<E> out = new ArrayList<>();
		return makeList(out);
	}
	
	public default <E> List<E> makeList( E fillValue ) {
		List<E> out = new ArrayList<>();
		while (out.size() < size())
			out.add(fillValue);
		return out;
	}

	public default <E> List<E> makeList(List<E> input) {
		if (input == null)
			input = new ArrayList<>();
		if (input.size() < size())
			while (input.size() < size())
				input.add(null);
		return input;
	}

	public default <E extends Number> float sum(Function<N, List<N>> nodeFn, List<E> data) {
		return (float) getElems(nodeFn, data).stream().mapToDouble(i -> i.doubleValue()).sum();
	}

	public default <E> void setElem(E elem, List<E> dataList) {
		checkDataList(this, dataList);
		dataList.set(index(), elem);
	}

	public default <E> E getElem(List<E> dataList) {
		checkDataList(this, dataList);
		return dataList.get(index());
	}

	public default <E> List<E> getElems(List<N> nodeList, List<E> inputList) {
		return nodeList.stream().map(n -> n.getElem(inputList)).collect(Collectors.toList());
	}

	public default <E> List<E> getElems(Function<N, List<N>> nodeFn, List<E> inputList) {
		return nodeFn.apply(getInstance()).stream().map(n -> n.getElem(inputList)).collect(Collectors.toList());
	}

	default <E> void sort(List<E> dataList) {
		// insert code from abstract node
	}

	default <E> void modifyList(BiFunction<N, E, E> modFn, List<E> dataList) {
		setElem(modFn.apply(getInstance(), getElem(dataList)), dataList);
	}

	default <E> List<E> makeList(BiFunction<N, E, E> modFn, List<E> dataList) {
		List<E> out = makeList();
		for (N node : getInstance())
			node.setElem(modFn.apply(getInstance(), node.getElem(dataList)), out);
		return out;
	}

	default <E> void modifyElems(BiFunction<N, E, E> modFn, List<E> dataList) {
		for (N node : (Iterable<N>) getInstance())
			node.modifyList((n, e) -> modFn.apply(n, e), dataList);
	}

	default <E> void modifyElems(Function<N, List<N>> nodeFn, Function<E, E> modFn, List<E> dataList) {
		for (N node : nodeFn.apply(getInstance()))
			node.modifyList((n, e) -> modFn.apply(e), dataList);
	}

	default <E> void modifyElems(Iterable<N> iterable, BiFunction<N, E, E> modFn, List<E> dataList) {
		for (N node : iterable)
			node.modifyList(modFn, dataList);
	}

	static <E> void checkDataList(NodeFunctions<?> node, List<E> dataList) {
		if (dataList == null)
			dataList = new ArrayList<>();
		if (dataList.size() < node.size())
			while (dataList.size() < node.size()) {
				dataList.add(null);
			}
		else if (dataList.size() > node.size()) {
			dataList = dataList.subList(0, node.size());
		}
	}

	// SORTING FNS /////////////////////////////////////////

	/**
	 * runs automatically after each update fn override in subclass
	 */
	public default void customTreeUpdateFn() {
	};

	public default void sort(List<N> nodeList, Comparator<N> comparator) {
		if (nodeList == null || nodeList.size() == 0)
			return;
		Collections.sort(nodeList, comparator);
		customTreeUpdateFn();
	}

	public default void sortRecursive(Function<N, List<N>> nodeFn, Comparator<N> comparator) {
		List<N> nodeList = nodeFn.apply(getInstance());
		if (nodeList == null || nodeList.size() == 0)
			return;
		Collections.sort(nodeList, comparator);
		customTreeUpdateFn();
		for (N node : nodeList)
			node.sortRecursive(nodeFn, comparator);
	}

	public default void shuffle(List<N> nodeList) {
		if (nodeList == null || nodeList.size() == 0)
			return;
		Collections.shuffle(nodeList);
		customTreeUpdateFn();
	}

	public default void shuffleRecursive(Function<N, List<N>> nodeFn) {
		List<N> nodeList = nodeFn.apply(getInstance());
		if (nodeList == null || nodeList.size() == 0)
			return;
		Collections.shuffle(nodeList);
		customTreeUpdateFn();
		for (N node : nodeList)
			node.shuffleRecursive(nodeFn);
	}

	public default void reverse(List<N> nodeList) {
		Collections.reverse(nodeList);
		customTreeUpdateFn();
	}

	public default <E> void sort(Function<N, List<N>> nodeFn, Comparator<N> comparator, List<E> data) {
		List<N> nodeListorig = nodeFn.apply(getInstance());
		List<N> nodeList = nodeList().subList(nodeListorig.get(0).index(),
				nodeListorig.get(nodeListorig.size() - 1).index());
		List<E> dataList = data.subList(nodeList.get(0).index(), nodeList.get(nodeList.size() - 1).index());
		Collections.sort(nodeList, comparator);
		final int firstInd = nodeList.get(0).index();
		Comparator<E> compare = new Comparator<E>() {
			public int compare(E d1, E d2) {
				// subtract nodeList.get(0).index()?
				return (nodeList.get(dataList.indexOf(d1)).index() - firstInd)
						- (nodeList.get(dataList.indexOf(d2)).index() - firstInd);
			}
		};
		dataList.sort(compare);
		customTreeUpdateFn();
	}

//	Comparator<Integer>  nodeOrder = new Comparator<Item>( List<Integer> data ) {
//	    public int compare(N node, List<Integer> data ) {
//	        return Integer.compare(node.indexInParent(), data::get );
//	    }
//	};

	// ITERATORS ////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/*
	 * iterator defaults as depth first traversal
	 */

	public default Iterator<N> nodeIterator() {
		// iterator traversal naturally adds in reverse;
		return new DFSTraversal<N>(getInstance(), dfsNodeGatherFn());
	}
	
	public default Iterable<N> dfs() {
		return makeIterable( new DFSTraversal<N>(getInstance(), dfsNodeGatherFn()) );
	}
	public default Iterable<N> bfs() {
		return makeIterable( new BFSTraversal<N>(getInstance(), bfsNodeGatherFn()) );
	}
	
	public static <E> Iterable<E> makeIterable(Iterator<E> type) {
		return new Iterable<E>() {
			public Iterator<E> iterator() {
				return type;
			}
		};
	}

	// cant use size() in traversal, because size() fn uses traversal
	public class BFSTraversal<T extends NodeFunctions<T> & Iterable<T> & Comparable<T>> implements Iterator<T> {
		public Queue<T> traversal = new LinkedList<>();
		boolean[] traversed;
		public Function<T, List<T>> nodeGatheringFn;

		public BFSTraversal(T startNode, Function<T, List<T>> nodeGatheringFn) {

			traversal.add(startNode);
			this.nodeGatheringFn = nodeGatheringFn;
			traversed = new boolean[startNode.treeSize()];
			for (int i = 0; i < traversed.length; i++)
				traversed[i] = false;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public T next() {
			T n = traversal.poll();

			List<T> foundNodes = nodeGatheringFn.apply(n);
			for (T node : foundNodes) {
				if (!traversed[node.index()]) {
					traversed[node.index()] = true;
					traversal.add(node);
				}
			}

			return n;
		}

		public void remove() {
			// Default throws UnsupportedOperationException.
		}
	}

	/**
	 * traverse a list of objects using node structure
	 */

//		public <E> Iterable<E> bfs( List<E> dataList ){
////			checkList( dataList );
//			Iterator<E> it = new Iterator<E>() {
//				public Queue<N> traversal = new LinkedList<>(Arrays.asList(getInstance()));
//
//				@Override
//				public boolean hasNext() {
//					return !traversal.isEmpty();
//				}
//
//				@Override
//				public E next() {
//					if (!hasNext())
//						throw new NoSuchElementException();
//					N n = traversal.poll();
//					if (n.hasChildren())
//						for (int i = n.getChildCount()-1; i >= 0; i--)
//							traversal.add(n.get(i)); // reverse this?
//					return n.getElem(dataList);
//				}
//
//				@Override
//				public void remove() {
//					throw new UnsupportedOperationException();
//				}
//			};
//		
//
//			return new Iterable<E>() {
//				public Iterator<E> iterator() {
//					return it;
//				}
//			};
//		}

	public class DFSTraversal<T extends NodeFunctions<T> & Iterable<T> & Comparable<T>> implements Iterator<T> {
		public Stack<T> traversal = new Stack<>();
//		public BiConsumer<N, V> traverseFn;
		public Function<T, List<T>> nodeGatheringFn;
		boolean[] traversed;

		public DFSTraversal(T startNode, Function<T, List<T>> nodeGatheringFn) {
			traversal.push(startNode.getInstance());
			traversed = new boolean[startNode.treeSize()];
			for (int i = 0; i < traversed.length; i++)
				traversed[i] = false;
			this.nodeGatheringFn = nodeGatheringFn;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public T next() {
			T n = traversal.pop();
			List<T> foundNodes = nodeGatheringFn.apply(n);
			for (T node : foundNodes) {
				if (!traversed[node.index()]) {
					traversed[node.index()] = true;
					traversal.add(node);
				}
			}
			return n;
		}

		public void remove() {
			// Default throws UnsupportedOperationException.
		}
	}

}
