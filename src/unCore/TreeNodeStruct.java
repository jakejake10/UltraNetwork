package unCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import unCore.NodeObj.CoreData;

public abstract class TreeNodeStruct<N extends TreeNodeStruct<N, D>, D> implements NodeObj<N, D>, Iterable<N> {
	//COMMON
	NodeObj.CoreData<N,D> core;
	D data;
	//OTHER
	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int size = 0;
	public int dataLoc = -1; // not incorporated into node shifting fn
	public int depth = 0;

	// constructors required in subclass
	public TreeNodeStruct( int...init ){	// root constructor
		if( init == null || init.length == 0 ) {
			core = makeCore();	// constructor() is root constructor
			core.attach( getInstance() );
			generateData();
		}
		// else constructor(0) means it is a null node
	}
	
	public TreeNodeStruct( D input ){	// root constructor
		if( nodeList() == null ) core = makeCore();	// constructor() is root constructor
		core.attach( getInstance() );
		setData( input );
	}
	
	public TreeNodeStruct( N input ){	// root constructor
		core.attach( input );
		generateData();
	}
	
	// NodeObj INTERFACE /////////////////////////////////
	/**
	 * methods for final class:
	 * defaultConstructor()
	 * getInstance()
	 */
	
	public int index() {
		return index;
	}
	public void setIndex( int index ) {
		this.index = index;
	}

	public int size() {
		int out = 0;
		for (N n : getInstance())
			out++;
		return out;
	}


	public Function<N,List<N>> dfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getChildren();
			Collections.reverse(nc);
			return nc;
		};
	}

	public Function<N,List<N>> bfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getChildren();
			return nc;
		};
	}

	//COMMON /////////////////
	
	public CoreData<N,D> getCore(){
		return core;
	}
	public void setCore( CoreData<N,D> input ){
		this.core = input;
	}
	public CoreData<N,D> makeCore() {
		return new CoreData<>( (n,i) -> n.firstChild + i );
	}
	public D getData() {
		return data;
	}
	public void setData( D data ) {
		this.data = data;
	}
	
//	@Override
//	public N defaultConstructor() {
//		return new TreeNodeStruct<>( 0 );
//	}
//
//	@Override
//	public N getInstance() {
//		return this;
//	}
//
//	public int size() {
//		int out = 0;
//		for (N n : this)
//			out++;
//		return out;
//	}
//
//	public Function<N, Collection<N>> dfsNodeGatherFn() {
//		return n -> {
//			List<N> nc = n.getChildren();
//			Collections.reverse(nc);
//			return nc;
//		};
//	}
//
//	public Function<N, Collection<N>> bfsNodeGatherFn() {
//		return n -> {
//			List<N> nc = n.getChildren();
//			return nc;
//		};
//	}
//	
	

	// CHECK FNS
	// ///////////////////////////////////////////////////////////////////////////

	public boolean hasParent() {
		return parentIndex > -1;
	}

	public boolean hasChildren() {
		return firstChild > -1;
	}

	public boolean isLeaf() {
		return firstChild == -1;
	}

	public boolean isRoot() {
		return parentIndex == -1;
	}

	// returns true if node has leafs as direct children;
	public boolean containsLeafs() {
		for (N node : getChildren())
			if (node.isLeaf())
				return true;
		return false;
	}

	// GET FNS ////////////////////////////////////////////////////////////////

//	public TreeNode<D> get(int childIndex) {
//		if (firstChild == -1)
//			throw new NoSuchElementException("Node (root) -> " + this.locationCode() + ": has no children");
//		return nodeList.get(firstChild + childIndex);
//	}

	public N parent() {
		return nodeList().get(parentIndex);
	}

	public int indexInParent() {
		if (!hasParent())
			return -1;
		else
			return index - parent().firstChild;
//		return indexInParent;
	}

	public int calcIndexInParent() { // used for new node creation and sort operations
		if (!hasParent())
			return -1;
		return index - parent().firstChild;
	}

	public N getRoot() {
		return hasParent() ? parent().getRoot() : getInstance();
	}

	public N lastChild() {
		return get(size - 1);
	}

	public List<N> getChildren() {
//		List<N> out = new ArrayList<>();
//		for (int i = 0; i < size; i++)
//			out.add(get(i)); // can't use iterable if used in iterable class?
		return IntStream.range(firstChild, firstChild+size)
				.mapToObj(i -> nodeList().get(i) )
				.collect(Collectors.toList());
	}

	public List<N> getLeafs() {
		return getLeafs(new ArrayList<N>());
	}

	public List<N> getLeafs(List<N> data) {
		if (!hasChildren())
			data.add(getInstance());
		for (N node : getChildren())
			node.getLeafs(data); // can't use iterable if used in iterable class?
		return data;
	}

	public List<N> getFilteredLeafs(Predicate<N> selectionFn) {
		List<N> out = new ArrayList<>();
		for (N node : getLeafs())
			if (selectionFn.test(node))
				out.add(node);
		return out;
	}
	
	/*
	 * runs selector fn at each level to get next child until leaf is reached
	 */
	public N getLeafIndexSelectorFn( Function<N,Integer> childSelectionFn ) {
		if( hasChildren() ) return get( childSelectionFn.apply(getInstance()) ).getLeafIndexSelectorFn( childSelectionFn );
		else return getInstance();
	}
	
	public N getLeafSelectorFn( Predicate<N> childSelectionFn ) {	// returns first match
		if( hasChildren() ) {
			for( N node : getChildren() ) {
				if( childSelectionFn.test(node) )
					return node.getLeafSelectorFn( childSelectionFn );
			}
			return null;
		}
		else return getInstance();
	}
	
	 public List<N> getLeafsSelectorFn( Predicate<N> childSelectionFn ) {
		 List<N> out = new ArrayList<N>();
		 getLeafsSelectorFnRecursive( out, childSelectionFn );
		 return out;
	 }
	 
	 void getLeafsSelectorFnRecursive( List<N> foundLeafs, Predicate<N> childSelectionFn ) {
		if( hasChildren() ) {
			for( N node : getChildren() )
				if( childSelectionFn.test(node) )
					node.getLeafsSelectorFnRecursive( foundLeafs, childSelectionFn );
		}
		else foundLeafs.add( getInstance() );
	}

	public int getMaxDepth() {
		if (hasChildren()) {
			int maxChildDepth = depth;
			for (N node : getChildren()) {
				int curDepth = node.getMaxDepth();
				if (curDepth > maxChildDepth)
					maxChildDepth = curDepth;
			}
			return maxChildDepth;
		}

		return depth;
	}

	public List<N> getDepth(int targetLevel) {
		return getDepth(new ArrayList<N>(), targetLevel);
	}

	public List<N> getDepth(List<N> data, int targetLevel) {
		if (depth == targetLevel)
			data.add(getInstance());
		if (depth < targetLevel)
			for (N node : getChildren())
				node.getDepth(data, targetLevel); // can't use iterable if used in iterable class?
		return data;
	}

	public <E> List<E> getChildData(Function<N, E> fn) {
		List<E> out = new ArrayList<>();
		for (N child : getChildren())
			out.add(fn.apply(child));
		return out;
	}

	public int getChildCount() {
		return size;
	}

	public int leafSize() {
//		int out = 0;
//		while( leafs().iterator().hasNext() ) out++ ;
//		return out;
		return getLeafs().size();
	}

	public String locationCode() {
		return locationCodeFn("");
	}

	String locationCodeFn(String code) {
		if (hasParent())
			return parent().locationCodeFn(Integer.toString(indexInParent()) + code);
		else
			return code;
	}

	// CHILD OPERATIONS //////////////////////////////////////////////

	public void addChild( D data, int...index ) {
		int newIndex = index.length > 0 ? index[0] : size;
		addChild( index );
		get(newIndex).setData( data );
	}
	public void addChild(int... index) {
		if (index.length > 0)
			addChild(defaultConstructor( 0 ), index[0]);
		else
			addChild(defaultConstructor( 0 ));
	}

	void addChild(N child) {
		if (!hasChildren())
			firstChild = totalSize();
		child.index = firstChild + size;
		child.parentIndex = this.index;
		child.depth = depth + 1;
		shiftNodesRight(child.index, child.depth);
		nodeList().add(child.index, child); // add to index after loc, and after any pre existing children
		child.core = core;
		child.generateData();
		size++;
	}

	void addChild(N child, int index) {
		if (index > size || !hasChildren()) {
			addChild(child);
			return;
		}
		child.index = firstChild + index;
		child.parentIndex = this.index;
		child.depth = depth + 1;
		shiftNodesRight(child.index, child.depth);
		nodeList().add(child.index, child); // add to index after loc, and after any pre existing children
		child.core = core;
		generateData();
		size++;
	}
	

//	public void addChild(TreeNode<D> child, int... index) {
//		int childIndex = index.length == 0 || !hasChildren() || index[0] > size ? size : index[0];
//		int newIndex = !hasChildren() ? totalSize() : firstChild + childIndex; // myLoc + 1 + size;
//		if (!hasChildren())
//			firstChild = newIndex;
//		child.nodeList = nodeList;
//		child.index = newIndex;
//		child.parentIndex = this.index;
//		child.depth = depth + 1;
//		child.indexInParent = child.index - firstChild; // calcIndexInParent();
//		shiftNodesRight(newIndex, child.depth);
//		nodeList.add(newIndex, child); // add to index after loc, and after any pre existing children
//		size++;
//	}

	public void addChildren(List<N> children) {
		for (N child : children)
			addChild(child);
	}

	public void removeChild(int index) {
		if (get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < get(index).getChildCount(); i++)
				get(index).removeChild(i);
		int childIndex = get(index).index;
		size--;
		if (size == 0)
			firstChild = -1;
		nodeList().remove(childIndex);
		shiftNodesLeft(childIndex);
	}

	public <E> void removeChild(int index, List<E> treeData) {
		if (get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < get(index).getChildCount(); i++)
				get(index).removeChild(i, treeData);
		int childIndex = get(index).index;
		size--;
		if (size == 0)
			firstChild = -1;
		nodeList().remove(childIndex);
		shiftNodesLeft(childIndex);
		treeData.remove(childIndex);
	}

	public void clearChildren() {
		for (int i = 0; i < getChildCount(); i++)
			removeChild(i);
	}

	public void shiftNodesRight(int index, int depth) { // addition
		if (index == totalSize())
			return;
		for (int i = 0; i < totalSize(); i++) {
			N curNode = nodeList().get(i);
			if (curNode.index >= index)
				curNode.index += 1;
			if (curNode.firstChild >= index && curNode.depth >= depth) // curNode.firstChild != -1 )
				curNode.firstChild += 1;
			if (curNode.parentIndex >= index)
				curNode.parentIndex += 1;
		}
	}

	public void shiftNodesLeft(int index) { // removal
		if (index == totalSize())
			return;
		for (int i = 0; i < totalSize(); i++) {
			N curNode = nodeList().get(i);
			if (curNode.index >= index)
				curNode.index--;
			if (curNode.firstChild > index && curNode.firstChild != -1)
				curNode.firstChild--;
			if (curNode.parentIndex >= index)
				curNode.parentIndex--;
		}
	}

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String multiLineString(Function<N, ?> fn) {
		String out = "";
		for (N n : getInstance())
			out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}

	public void printOperation(Function<N, ?> fn) {
		for (N n : getInstance())
			System.out.println(createPrintFn(fn).apply(n).toString());
	}

	public Function<N, String> createPrintFn(Function<N, ?> addedString) {
		return n -> {
			Object val = addedString.apply(n);
			String space = "";
			for (int i = 0; i < n.depth; i++)
				space = "  " + space;
			return space + " - " + (val == null ? "(null) " : val.toString());
		};
	}

	public void printTree() {
		printOperation(n -> n);
	}

	public String toString() {
		return "node"; // multiLineString(n -> "node");
	}

	public <E> void printList(List<E> inputList) {
		System.out.println(multiLineString(
				n -> (n.getElem(inputList) != null ? "node: " + n.getElem(inputList) : "node: (null)")));
	}

}
