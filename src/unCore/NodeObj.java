package unCore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.*;
import java.util.stream.*;

import pFns_general.PFns;
import ugCore.Grid;
import ugCore.Divider.Recursive;
import unCore.NodeFunctions.BFSTraversal;
import unCore.NodeFunctions.DFSTraversal;
import utTypes.BasicNode;

import java.util.Iterator;
import java.util.LinkedList;

public abstract interface NodeObj<N extends NodeObj<N, D> & Iterable<N>, D>   {
//	CoreData core;
//	public D data;
	

//	public int index;

//	public AbstractNode2( int...init ){	// root constructor
//		if( init != null && init.length > 0 ) return;
//		core = makeCore();
//	}
//	public AbstractNode2( N input ){	// root constructor
//		core.attach( input );
//	}
	
	
//	AbstractNode2( N input ){
//		input.core.attach( getInstance() );
//	}
	
//	default void initNode() { // called in constructors, index should be overridden in location placement
//		setIndex( totalSize() );
//		getCore().attach( getInstance() );
//		setData( getCore().dataGeneratorFn.apply( getInstance() ) );
//	}

	default void addNode() {	// basic add, not used where node placed at location
		N newNode = defaultConstructor();
		newNode.setIndex( totalSize() );
		getCore().attach( newNode );
		
	}

	// ABSRACT METHODS /////////////////////////////////////

	public abstract int index();
	public abstract void setIndex( int index );
	public abstract int size();
	
	public abstract N getInstance();

	public abstract N defaultConstructor( int...init );
	public abstract N defaultConstructor( N input );

	public abstract Function<N,List<N>> dfsNodeGatherFn();
	public abstract Function<N,List<N>> bfsNodeGatherFn();

	public abstract CoreData<N,D> makeCore();
	public abstract void setCore( CoreData<N,D> input );
	public abstract CoreData<N,D> getCore();
	
	public abstract D getData();
	public abstract void setData( D data );
	
	
//	public abstract Function<N, List<N>> nodeGatherFn();
	// abstract List<N> nodeList();
	// abstract void setNodeList( List<N> input );

//	public static N createRoot() {
//		throw new NoSuchElementException();
//		return null;
//	}
	
	// BASE METHODS ////////////////////////////////////////
	
	

	// GET METHODS /////////////////////////////////////////

	public default N get( int index ) {
		return nodeList().get( getCore().indexReturnFn.apply(getInstance(), index ) );
	}
	public default List<N> nodeList() {
		return getCore().nodeList;
	}
//
//	public default int index() {
//		return index;
//	}

	public default int totalSize() {
		return nodeList().size();
	}

	// DATA METHODS ////////////////////////////////////////

	

	// LIST METHODS ////////////////////////////////////////

	public default <E> List<E> makeList() {
		List<E> out = new ArrayList<>();
		return makeList(out);
	}

	public default <E> List<E> makeList(E fillValue) {
		List<E> out = new ArrayList<>();
		while (out.size() < size())
			out.add(fillValue);
		return out;
	}

	public static <E extends NodeFunctions<?>, R> List<R> makeList(List<E> nodeList, R fillValue) {
		List<R> out = new ArrayList<>();
		while (out.size() < nodeList.size())
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

	public default <E> E getElem(List<E> data) {
		return data.get(index());
	}

	public default <E> void setElem(E value, List<E> dataList) {
		dataList.set(index(), value);
	}

	public default <E> List<E> getElems(List<N> nodeList, List<E> inputList) {
		return nodeList.stream().map(n -> n.getElem(inputList)).collect(Collectors.toList());
	}

	public default <E> List<E> getElems(Function<N, List<N>> nodeFn, List<E> inputList) {
		return nodeFn.apply(getInstance()).stream().map(n -> n.getElem(inputList)).collect(Collectors.toList());
	}

	public default <E> void sort(List<E> dataList) {
		// insert code from abstract node
	}

	public default <E> void modifyList(BiFunction<N, E, E> modFn, List<E> dataList) {
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

	public default Iterator<N> iterator() {
		return nodeIterator();
	}
	/*
	 * iterator s as depth first traversal
	 */

	public default Iterator<N> nodeIterator() {
		// iterator traversal naturally adds in reverse;
		return new DFSTraversal<N>(getInstance(), dfsNodeGatherFn());
	}

	public default Iterable<N> dfs() {
		return makeIterable(new DFSTraversal<N>(getInstance(), dfsNodeGatherFn()));
	}

	public default Iterable<N> bfs() {
		return makeIterable(new BFSTraversal<N>(getInstance(), bfsNodeGatherFn()));
	}

	public static <E> Iterable<E> makeIterable(Iterator<E> type) {
		return new Iterable<E>() {
			public Iterator<E> iterator() {
				return type;
			}
		};
	}

	// cant use size() in traversal, because size() fn uses traversal
	public class BFSTraversal<T extends NodeObj<T, ?>  & Iterable<T>> implements Iterator<T> {
		public Queue<T> traversal = new LinkedList<>();
		boolean[] traversed;
		public Function<T,List<T>> nodeGatheringFn;

		public BFSTraversal(T startNode, Function<T,List<T>> nodeGatheringFn) {

			traversal.add(startNode);
			this.nodeGatheringFn = nodeGatheringFn;
			traversed = new boolean[startNode.totalSize()];
			for (int i = 0; i < traversed.length; i++)
				traversed[i] = false;
			traversed[startNode.index()] = true;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public T next() {
			T n = traversal.poll();

			Collection<T> foundNodes = nodeGatheringFn.apply(n);
			for (T node : foundNodes) {
				if (!traversed[node.index()]) {
					traversed[node.index()] = true;
					traversal.add(node);
				}
			}

			return n;
		}

		public void remove() {
			// throws UnsupportedOperationException.
		}
	}

	/**
	 * traverse a list of objects using node structure
	 */

//		public <E> Iterable<E> bfs( List<E> dataList ){
//		}

	public class DFSTraversal<T extends NodeObj<T, ?>  & Iterable<T>> implements Iterator<T> {
		public Stack<T> traversal = new Stack<>();
		public Function<T,List<T>> nodeGatheringFn;
		boolean[] traversed;

		public DFSTraversal(T startNode, Function<T,List<T>> nodeGatheringFn) {
			traversal.push(startNode.getInstance());
			traversed = new boolean[startNode.totalSize()];
			for (int i = 0; i < traversed.length; i++)
				traversed[i] = false;
			traversed[startNode.index()] = true;
			this.nodeGatheringFn = nodeGatheringFn;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public T next() {
			T n = traversal.pop();
			Collection<T> foundNodes = nodeGatheringFn.apply(n);
			for (T node : foundNodes) {
				if (!traversed[node.index()]) {
					traversed[node.index()] = true;
					traversal.add(node);
				}
			}
			return n;
		}

		public void remove() {
			// throws UnsupportedOperationException.
		}
	}
	// CORE DATA FNS /////////////////////////////////
	
	public default void generateData() {
		setData( getCore().dataGeneratorFn.apply( getInstance() ) );
	}
	
	public default void setDataGenerator( Function<N,D> generatorFn ) {
//		setData( generatorFn.apply( getInstance() ) );
		getCore().setDataGenerator(generatorFn);
		generateData();
	}
	
	public default void updateGeneratedData() {
		for( N node : bfs() ) node.generateData();
	}
	
	public default void updateGeneratedData( Function<N,D> generatorFn ) {
		for( N node : bfs() ) node.generateData();
	}

	class CoreData<T extends NodeObj<T,D> & Iterable<T>,D> {
		
		List<T> nodeList;
		BiFunction<T,Integer,Integer> indexReturnFn;
		Function<T,T> nodeGeneratorFn;
		Function<T,D> dataGeneratorFn = n -> null;
		
		CoreData( BiFunction<T,Integer,Integer> inputFn ){
			nodeList = new ArrayList<>();
			this.indexReturnFn = inputFn;
		}
		
		public void attach( T newNode ) {
			nodeList.add(newNode);
			newNode.setCore( this );
		}
		
		
		public void setDataGenerator( Function<T,D> generatorFn ) {
			this.dataGeneratorFn = generatorFn;
		}
		
	}

}