package unCore;

import java.util.ArrayDeque;
import java.util.ArrayList;


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.*;
import java.util.stream.*;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import unCore.TreeNodeFunctions.TreeBuilder;

/*
 * TODO: contiguous subtree storage
 * 	- simplifies many operations, faster subtree queries
 */

public interface TreeNodeObject<N extends TreeNodeObject<N> & Iterable<N>> {
	// SUGGESTED FIELDS
	// SingularTreeData<N> core;
	// D data;
	// public int index;
	// public int parentIndex = -1; // index in tree.nodes
	// public int firstChild = -1;
	// public int childCt = 0;
	// public int depth = 0;

	// ABSRACT METHODS /////////////////////////////////////

	public int getIndex();

	public void setIndex(int index);

	public int getParentIndex();

	public void setParentIndex(int index);

	public int getFirstChildIndex();

	public void setFirstChildIndex(int index);

	public int getDepth();

	public void setDepth(int depth);

	public int getChildCount();

	public void setChildCount(int count);

	public N getInstance();

	public N defaultConstructor();

	public SingularTreeData<N> getCoreFn();

	public void setCore(SingularTreeData<N> input);

	default void initNodeFields() {
		setIndex(0);
		setParentIndex(-1);
		setFirstChildIndex(-1);
		setDepth(0);
		setChildCount(0);
	}

	// OPTIONAL METHODS /////////////////////////////////////

	/*
	 * - when node is copied in superclass, only essential fields are copied - use
	 * this method to transfer any additional fields to a newly copied node - how to
	 * handle when using abstract / multiple different subclasses? bounded generic?
	 */
	public default void transferSubclassFieldsTo(N newNode) {
	}

	/*
	 * - set to false if you don't want subclass to allow children - for use cases
	 * with a group subclass and various type subclasses
	 */
	public default boolean allowChildren() {
		return true;
	}
	
	/*
	 * this is called in the parent node after a child is added
	 * override in subclass for custom behavior
	 * things like adding a child's value to parent's total
	 */
	public default void addedChildMod() {
		// override to modify newly node after addChild() operation
	}
	
	/*
	 * specify additional text and line breaks for print operations
	 * example: if not root or leaf, return "\n"
	 * this will add an empty line between node text outputs
	 */
	public default String getPrintInfoLineBreak() {
		return "";
	}
//	
//	/*
//	 * use method for specifying what data to import from a json file
//	 * works with the build from json method, so json should follow same conventions
//	 * for trees of multiple subtypes, each subtype can have different method
//	 */
//	
//	public default void modifyFromJSON( JSONObject input ) {
//		
//	}
	
	


	

	public default N initCore() {
		setCore(new SingularTreeData<>());
		getCore().nodeList.add(getInstance());
		return getInstance();
	}
	
	public default SingularTreeData<N> getCore(){
		if( getCoreFn() == null ) initCore();
		return getCoreFn();
	}
	
	

	// ITERATABLE ////////////////////////////////////////////////////////////////

//  public default Iterator<N> iterator() {
//    return nodeIterator();
//  }

//  // CHECK FNS
//  // ///////////////////////////////////////////////////////////////////////////

	public default boolean hasParent() {
		return getParentIndex() > -1;
	}

	public default boolean hasChildren() {
		return getFirstChildIndex() > -1;
	}

	public default boolean isLeaf() {
		return getFirstChildIndex() == -1;
	}

	public default boolean isRoot() {
		return getParentIndex() == -1;
	}

	// equals() use default object equals method

	public default <E extends TreeNodeObject<E> & Iterable<E>> boolean equalLocation(E nodeIn) {
		return nodeIn.getLocationCodeFromRoot().equals(getLocationCodeFromRoot());
	}

	// returns true if node has leafs as direct children;
	public default boolean containsLeafs() {
		if (!hasChildren())
			return false;
		for (N node : getChildren())
			if (node.isLeaf())
				return true;
		return false;
	}

	public default boolean containsOnlyLeafs() {
		if (!hasChildren())
			return false;
		for (int i = 0; i < getChildCount(); i++)
			if (!get(i).isLeaf())
				return false;
		return true;
	}
	
	

	// GETTERS ////////////////////////////////////////////////////////////////////

	public default N get(int indexIn) {
		if (getChildCount() < 1)
			throw new UnsupportedOperationException(getInstance() + "cannot get child of a node with 0 children");
		return getCore().nodeList.get(getFirstChildIndex() + indexIn);
	}
	
	/*
	 * gets the size of entire tree
	 */

	public default int getTotalSize() {
		return getCore() != null ? getCore().nodeList.size() : 1;
	}
	
	/*
	 * gets the size of subtree
	 */
	
//	public default int getSize() {
//		int size = 1; // count self
//	    for (int i = 0; i < getChildCount(); i++) {
//	      size += get(i).getSubtreeSize();
//	    }
//	    return size;
//	}

//	public default int getSubtreeSize() {
//		int out = 0;
//		for (N node : getInstance())
//			out++;
//		return out;
//	}
	
	public default int getSubtreeSize() {
	    int size = 1;
	    for (int i = 0; i < getChildCount(); i++) {
	        size += get(i).getSubtreeSize();
	    }
	    return size;
	}

//	public default N findFirst(Predicate<N> matchFn) {
//		for (N node : getInstance())
//			if (matchFn.test(node))
//				return node;
//		return null;
//	}
	
	public default N findFirst(Predicate<N> matchFn) {
	    if (matchFn.test(getInstance()))
	        return getInstance();
	    for (int i = 0; i < getChildCount(); i++) {
	        N found = get(i).findFirst(matchFn);
	        if (found != null)
	            return found;
	    }
	    return null;
	}

	public default List<N> findAll(Predicate<N> matchFn) {
		List<N> out = new ArrayList<>();
		findAllRecursive(matchFn,out);
		return out;
	}
	
	default void findAllRecursive(Predicate<N> matchFn, List<N> out ){
		for (int i = 0; i < getChildCount(); i++ ) {
			if (matchFn.test(get(i))) 
				out.add(get(i));
		}
	}

	public default N getParent() {
		return getCore().nodeList.get(getParentIndex());
	}

	public default int getIndexInParent() {
		if (!hasParent())
			return -1;
		else
			return getIndex() - getParent().getFirstChildIndex();
	}

//  public default int calcIndexInParent() { // used for new node creation and sort operations
//    if (!hasParent()) return -1;
//    return getIndex() - getParent().getFirstChildIndex();
//  }

	public default N getRoot() {
//		return hasParent() ? getParent().getRoot() : getInstance();
		return getCore().nodeList.get(0);
	}

	public default N getRandomNode() {
		return getCore().nodeList.get((int) Math.floor((Math.random() * getCore().nodeList.size())));
	}
	
	/*
	 * for a performance boost use indexed iteration instead:
	 * for (int i = 0; i < getChildCount(); i++) {
		    N child = get(i);
		    ...
		}
	 */
	public default List<N> getChildren() {
		return IntStream.range(0, getChildCount()).mapToObj(i -> get(i)).collect(Collectors.toList());
	}
	
	/*
	 * performance boost over for( N child : getChildren() )
	 * new list does not need to be created
	 */
	public default void forEachChild(Consumer<N> fn) {
	    for (int i = 0; i < getChildCount(); i++) {
	        fn.accept(get(i));
	    }
	}
	/*
	 * performance boost over for( N leaf : getLeafs() )
	 * new list does not need to be created
	 * faster than cached leaf list in benchmark test
	 */
	public default void forEachLeafRecursive(Consumer<N> fn) {
	    if (getChildCount() == 0) {
	        fn.accept(getInstance());
	        return;
	    }
	    for (int i = 0; i < getChildCount(); i++) {
	        get(i).forEachLeafRecursive(fn);
	    }
	}
	
	
	
//	public default N getLastChild() {
//		return get(getChildCount() - 1);
//	}
	
	public default N getLastChild() {
	    return getChildCount() == 0 ? null : get(getChildCount() - 1);
	}

	public default List<N> getLeafs() {
		return getLeafsRecursive(new ArrayList<N>());
	}
	
	default List<N> getLeafsRecursive(List<N> data) {
		if (!hasChildren())
			data.add(getInstance());
		for (N node : getChildren())
			node.getLeafsRecursive(data); // can't use iterable if used in iterable class?
		return data;
	}
	
	public default N getRandomLeaf() {
		List<N> leafs = getLeafs();
		return leafs.get( (int)Math.floor( Math.random() * leafs.size() ) );
	}

	
	
//	default int getMaxDepth() {
//		if (hasChildren())
//			return getChildren().stream().mapToInt(c -> c.getMaxDepth()).max().orElse(-1);
//		else
//			return getDepth();
//	}

	/*
	 * gets max depth of a subtree, relative to node calling it
	 */
	default int getMaxDepth() {
		return getTotalTreeMaxDepth() - getDepth();
	}
	
	/*
	 * gets max depth of a tree relative to root node
	 */
	default int getTotalTreeMaxDepth() {

	    int max = getDepth();

	    for (int i = 0; i < getChildCount(); i++) {
	        int d = get(i).getTotalTreeMaxDepth();
	        if (d > max) max = d;
	    }

	    return max;
	}
	
	
	
	public default List<N> getNodesAtDepth( int depth ) {
		return findAll( n -> n.getDepth() == depth );
	}

//	public default int getLeafCount() {
//		return getLeafs().size();
//	}
	
	public default int getLeafCount() {
	    SingularTreeData<N> core = getCore();

	    // detached / fallback case
	    if (core == null) {
	        final int[] count = {0};
	        forEachLeafRecursive(n -> count[0]++);
	        return count[0];
	    }

	    // root gets the fast cached path
	    if (isRoot()) {
	        ensureLeafCache();
	        return core.leafIndexes.size();
	    }

	    // subtree uses recursive traversal for correctness
	    final int[] count = {0};
	    forEachLeafRecursive(n -> count[0]++);
	    return count[0];
	}

	/*
	 * - string of digits indicating sequence of child indexes to get from root to
	 * current node
	 */
	public default String getLocationCodeFromRoot() {
		String out = "";
		N curNode = getInstance();
		while (curNode.hasParent()) {
			out = String.valueOf(curNode.getIndexInParent()) + out;
			curNode = curNode.getParent();
		}
		return out;
	}

	/*
	 * - if input contains node in subtree, just get the path to root and clip at
	 * input depth
	 */
	public default String getLocationCodeFromNode(N input) {
		return getLocationCodeFromRoot().substring(input.getDepth());
	}

	public default N getFromLocationCode(String locationCode) {
		N curNode = getInstance();
		for (int i = 0; i < locationCode.length(); i++) {
			int childIndex = Character.getNumericValue(locationCode.charAt(i));
			curNode = curNode.get(childIndex);
		}
		return curNode;
	}

//	public static <E extends TreeNodeObject<E> & Iterable<E>> E getLowestCommonAncestor(E node1, E node2) {
//		List<Integer> dataList = TreeNodeFunctions.initDataList(node1, 0);
//		E curNode1 = node1;
//		E curNode2 = node2;
//		while (curNode1.hasParent() || curNode2.hasParent()) {
//			if (dataList.get(curNode1.getIndex()) != 0)
//				return curNode1;
//			dataList.set(curNode1.getIndex(), 1);
//			if (dataList.get(curNode2.getIndex()) != 0)
//				return curNode2;
//			dataList.set(curNode2.getIndex(), 2);
//			if (curNode1.hasParent())
//				curNode1 = curNode1.getParent();
//			if (curNode2.hasParent())
//				curNode2 = curNode2.getParent();
//		}
//		return node1.getRoot();
//	}
	
	/*
	 * gets lowest node that contains two input nodes
	 */
			
	public static <E extends TreeNodeObject<E> & Iterable<E>> E getLowestCommonAncestor(E node1, E node2) {
		if (!node1.getRoot().equals(node2.getRoot()))
			throw new UnsupportedOperationException("nodes do not belong to same root");
		List<E> n1Path = new ArrayList<>();
		E curNode = node1;
		while (curNode.hasParent()) {
			curNode = curNode.getParent();
			n1Path.add(curNode);
		}
		curNode = node2;
		while (curNode.hasParent()) {
			curNode = curNode.getParent();
			for (E dn : n1Path)
				if (dn.equals(curNode))
					return curNode;
		}
		System.out.println("lca not found");
		return null;
	}
	
	
	/*
	 * gets direct child of an ancestor that contains node calling the method
	 */

	public default N getAncestorChild(N ancestor) {
		N curNode = getInstance();
		while (curNode.hasParent()) {
			if (curNode.getParent().equals(ancestor))
				return curNode;
			else
				curNode = curNode.getParent();
		}
		System.out.println("ancestor child not found");
		return null;
	}
	
	public default N getAncestorAtDepth( int depth ) {
		if( getDepth() < depth ) throw new UnsupportedOperationException("ancestor depth cannot be larger than node depth");
		N out = getInstance();
		while(out.hasParent()) {
			if( out.getDepth() == depth ) return out;
			else {
				if( !out.hasParent() ) return null;
				out = out.getParent();
			}
		}
		return null;
	}

	// STRUCTURE MODIFICATION OPERATIONS
	// //////////////////////////////////////////////

	/**
	 * node adding update override the addChild method in subclass for added
	 * functionality call super.addChild()
	 */
	public default void addChild(int... indexIn) {
		addChildTreeDataFn(defaultConstructor(), indexIn.length > 0 ? indexIn[0] : -1 );
	}

	public default void addChild(N childIn, int... indexIn) {
		addChildTreeDataFn(childIn, indexIn.length > 0 ? indexIn[0] : -1 );
	}
	
	public default void addChildForce(N childIn, int... indexIn) {
		addChildTreeDataFn(childIn, indexIn.length > 0 ? indexIn[0] : -1, true );
	}

	public default void addChild(Consumer<N> childModFn, int... indexIn) {
		addChildTreeDataFn(defaultConstructor(), indexIn.length > 0 ? indexIn[0] : -1 );
		N addedChild = indexIn.length == 0 ? getLastChild() : get(indexIn[0]);
		childModFn.accept(addedChild);
	}
	
	public default void addChildren(List<N> childData) {
		for (N child: childData) addChild( child );
	}

	/*
	 * use case for this?
	 */
	public default <E> void addChildren(List<E> childData, BiConsumer<N,E> childModFn) {
		for (E data : childData)
			addChild(n -> childModFn.accept(n, data));
	}

	/**
	 * core of the addChild() method first checks getCore(), instead of init each
	 * new root, just check once first child needs to be added need to deal with
	 * adding a "child" that is itself a tree with children, done? sets the core,
	 * adds new child to nodeList() if childnode has chldren, they are pulled out
	 * and reconnected through recursive addchild operation
	 */

	default void addChildTreeDataFn(N child, int indexIn, boolean...forceAdd) {
		if ( (!allowChildren() && forceAdd.length ==0 ) || ( !allowChildren() && !forceAdd[0] ) )
			throw new UnsupportedOperationException("this class does not allow child nodes to be added");
		if (getCore() == null)
			initCore();
		List<N> foundChildren = null;
		if (child.hasChildren())
			foundChildren = child.getChildren(); // need to pull these out and reconnect

		int curIndexInParent = indexIn < 0 || !hasChildren() || indexIn > getChildCount() ? getChildCount()
				: indexIn;
		if (!hasChildren())
			setFirstChildIndex(getTotalSize());
		child.setChildCount(0); // if child has children, it's size needs to be reset to 0, so adding back
								// children shows proper size
		child.setFirstChildIndex(-1); // if child has children, first child needs to be reset to -1 for future add ops
										// to work properly
		child.setIndex(getFirstChildIndex() + curIndexInParent);
		child.setParentIndex(getIndex());
		child.setDepth(getDepth() + 1);
		shiftNodesRight(child.getIndex(), curIndexInParent);
		getCore().nodeList.add(child.getIndex(), child); // add to index after loc, and after any pre existing children
		child.setCore(getCore());

		setChildCount(getChildCount() + 1);
		addedChildMod(); // comes from subclass
		if (foundChildren != null)
			for (N node : foundChildren)
				child.addChildForce(node);
		markLeafCacheDirty();
	}

// STRUCTURE MODIFICATION UTILITY FNS ///////////////////////////////////////////////////////

	default void shiftNodesRight(int chIndex, int iip) { // addition
		if (chIndex == getTotalSize())
			return;
		int lastChangedParent = -2;
		for (int i = 0; i < getTotalSize(); i++) {
			N curNode = getCore().nodeList.get(i);
			if (curNode.getIndex() >= chIndex) {
				if (curNode.getIndexInParent() == 0) {
					if ((curNode.getIndex() > chIndex || iip != 0)
							&& curNode.getParent().getIndex() != lastChangedParent) {
						curNode.getParent().setFirstChildIndex(curNode.getParent().getFirstChildIndex() + 1);
						lastChangedParent = curNode.getParent().getIndex();
					}
				}
				curNode.setIndex(curNode.getIndex() + 1);
			}
			if (curNode.getParentIndex() >= chIndex)
				curNode.setParentIndex(curNode.getParentIndex() + 1);
		}

	}

	default void shiftNodesLeft(int index) { // removal
		if (index == getTotalSize())
			return;
		for (int i = 0; i < getTotalSize(); i++) {
			N curNode = getCore().nodeList.get(i);
			if (curNode.getIndex() >= index)
				curNode.setIndex(curNode.getIndex() - 1);
			if (curNode.getFirstChildIndex() > index && curNode.getFirstChildIndex() != -1)
				curNode.setFirstChildIndex(curNode.getFirstChildIndex() - 1);
			if (curNode.getParentIndex() >= index)
				curNode.setParentIndex(curNode.getParentIndex() - 1);
		}
	}

	/**
	 * removes a child node works recursively to remove any children of child from
	 * tree
	 * 
	 * @param index doesn't return node because all children are removed with
	 *              operation
	 */

	public default void remove() {
		if( isRoot() ) throw new UnsupportedOperationException("Cannot call remove() on root node" );
		bottomUpOperation(null, (e, n) -> {
			if (n.hasChildren())
				for (N child : n.getChildren()) {
					n.getCore().nodeList.remove(child.getIndex());
					n.shiftNodesLeft(child.getIndex());
				}
			n.setChildCount(0);
			n.setFirstChildIndex(-1);
		});
		getCore().nodeList.remove(getIndex()); // added
		shiftNodesLeft(getIndex());
		getParent().setChildCount(getParent().getChildCount() - 1);
		if (getParent().getChildCount() == 0)
			getParent().setFirstChildIndex(-1);
		// subClassRemoveChildUpdate();
		markLeafCacheDirty();
	}

	public default void removeChild(int index) {
		get(index).remove();
	}

	public default <E> void removeChild(int index, List<E> treeData) {
		if (get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < get(index).getChildCount(); i++)
				get(index).removeChild(i, treeData);
		int childIndex = get(index).getIndex();
		setChildCount(getChildCount() - 1); // childCt--;
		if (getChildCount() == 0)
			setFirstChildIndex(-1);
		getCore().nodeList.remove(childIndex);
		shiftNodesLeft(childIndex);
		treeData.remove(childIndex);
	}

	public default void removeChildren() {
		for (int i = getChildCount() - 1; i > -1; i--)
			removeChild(i);
	}

	/*
	 * requires copyNode fns to be in place
	 * how it works:
	 * - called on a node, takes in new node and replaces it
	 * - modifies input node based on conditions:
	 * 	 - if the input node has parents, a subtree copy is made so the input node is now the root
	 * 	 - if node is root(no core) and input node has core
	 *     - input node's core is imported as current nodes core, all fc indexes now point to new nodelist
	 *   - if node is root and input node is another subclass
	 *     - throws error, original variable that is referencing root can never point to different subtype
	 *     - works at root level with same class nodes, because of the transferSubclassFieldsTo() fn
	 *     - original variable reference will not have any new fields of the copied different subclass
	 *   - if node is not root
	 *     - input node inserted from parent at same index of original removed node
	 *     - done using addChildForce(), incase of any subclasses that prevent child creation
	 */
	
	public default N replaceNodeSubtree(N inputNode) {
		markLeafCacheDirty();
		N insertedNode = null;
		if (inputNode.hasChildren()) insertedNode = inputNode.copyNodeSubtree();
		else                     insertedNode = inputNode.copyNode(true);	// no node field data
		if ( !isRoot() ) {	// just replace node with new node
			int iip = getIndexInParent();
			N parent = getParent();
			parent.get(iip).remove();
			parent.addChildForce(insertedNode, iip);
			return insertedNode;
		}
		// root is being replaced
		else return insertedNode;
		
	}
	
	/*
	 * replaces an individual node without affecting nodes downstream
	 * can swap out a root node, but need to save to a new variable
	 * returns new node replacement
	 */
	public default N replaceIndividualNode(N inputNode) {
		if( inputNode.hasChildren() ) throw new UnsupportedOperationException("replaceIndividualNode() input cannot have children" );
		N insertedNode = inputNode.copyNode(false);
		transferNodeFieldsTo( insertedNode );
		if( !isRoot() ) {
			transferNodeFieldsTo( inputNode );
			if( getCore() == null ) initCore();
			getCore().nodeList.set( getIndex(), insertedNode );
		}
		else {
			insertedNode.setCore( getCore() );
			insertedNode.getCore().nodeList.set(0, insertedNode);
		}
		return insertedNode;
	}
	
//	public default N replaceWith(N inputNode) {
//		if (!inputNode.isRoot())
//			inputNode = inputNode.copyNodeSubtree();
//		if ( isRoot() ) {
//			if( getCore() == null && inputNode.getCore() != null ) 
//				setCore(inputNode.getCore());
//			if( !getClass().getName().equals(inputNode.getClass().getName())) {
//				throw new UnsupportedOperationException("Cannot change variable reference of root node from one node subclass to another");
//			}
//			else {
//				inputNode.transferNodeFieldsTo(getInstance());
//				inputNode.transferSubclassFieldsTo(getInstance());
//			}
//			return getInstance();
//		}
//		int iip = getIndexInParent();
//		N parent = getParent();
//		parent.get(iip).remove();
//		parent.addChildForce(inputNode, iip);
//		return inputNode;
//	}

	
	public default N replaceWith( Function<N,N> replaceFn ) {
		return replaceNodeSubtree( replaceFn.apply(getInstance()) );
	}

	// TRAVERSAL FNS /////////////////////////////////////////////////////////

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
	        if (!hasNext()) throw new NoSuchElementException();

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
	    N root = getAncestorAtDepth(startDepth);
	    if (root == null) return null;

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
	
	
	
	
	
	
	
	// CACHING ////////////////////////////////////////////////////////////////
	
	public default void ensureLeafCache() {
	    SingularTreeData<N> core = getCore();
	    if (core == null) return;
	    if (core.leafIndexes == null || core.leafCacheDirty) rebuildLeafCache();
	}
	
	public default void markLeafCacheDirty() {
	    SingularTreeData<N> core = getCore();
	    if (core != null) core.leafCacheDirty = true;
	}
	
	public default void rebuildLeafCache() {
	    SingularTreeData<N> core = getCore();
	    if (core == null) return;

	    if (core.leafIndexes == null) core.leafIndexes = new ArrayList<>();
	    else core.leafIndexes.clear();

	    for (int i = 0; i < core.nodeList.size(); i++) {
	        N node = core.nodeList.get(i);
	        if (node.getChildCount() == 0) {
	            core.leafIndexes.add(i);
	        }
	    }

	    core.leafCacheDirty = false;
	}
	
	/*
	 * if root, uses cached list
	 * if not, does regular recursive operation
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
	
	

	// STRUCTURAL FNS /////////////////////////////////////////////////////////

	/**
	 * changes node to a child of a new blank node returns new parent if doing
	 * operation on root node variable, do TreeNode<?> root = root.insertParent()
	 * variables that call this will now point to new inserted parent
	 */

	public default N insertParent() {
		markLeafCacheDirty();
		N newNode = defaultConstructor();
		newNode.addChild(copyNodeSubtree());
		if (hasParent())
			getParent().get(getIndexInParent()).replaceNodeSubtree(newNode);
		else
			shiftFocus(newNode.get(0));
		return getInstance();
	}

	/**
	 * changes a variable reference to a new node in the tree cannot shift focus
	 * between trees
	 * 
	 * @param node
	 */

	public default void shiftFocus(N node) {
		setCore(node.getCore()); // check if core is from another tree and only replace if then? atomic int in
									// coredata?
		node.transferNodeFieldsTo(getInstance()); // node fields now identical to input node
		node.transferSubclassFieldsTo(getInstance()); // subclass fields now identical
		getCore().nodeList.set(getIndex(), getInstance()); // save current node over original
	}

	public default N copyNode(boolean transferNodeFields) {
		N out = defaultConstructor();
		if (transferNodeFields)
			transferNodeFieldsTo(out);
		transferSubclassFieldsTo(out);
		return out;
	}

	public default void transferNodeFieldsTo(N input) {
		input.setIndex(getIndex());
		input.setParentIndex(getParentIndex());
		input.setFirstChildIndex(getFirstChildIndex());
		input.setChildCount(getChildCount());
		input.setDepth(getDepth());
	}

	/*
	 * - uses traverse operation to iterate with dft and copy a subtree - for each
	 * node in existing tree - gets the location code from root of that nodes parent
	 * - with dfs, the parent of the existing node should already exist in copied
	 * version - the parent location code is used to find the matching node from the
	 * copied tree root reference, and a child is added to it
	 */

	public default N copyNodeSubtree() {
		markLeafCacheDirty();
		N out = copyNode(false);
		copyNodeSubtreeRecursive( getInstance(), out );
		return out;
	}
	
	public static <E extends TreeNodeObject<E>&Iterable<E>> void copyNodeSubtreeRecursive( E from, E to ) {
		if ( from.hasChildren() ) {
			for( E c : from.getChildren() ) {
				to.addChildForce( c.copyNode(false));
				copyNodeSubtreeRecursive( c, to.getLastChild());
			}
		}
	}

	/*
	 * - generates a copy of the tree structure of the output type of the convertFn
	 * - uses copynode with nodefields transferred so convertfn has access to
	 * original fields
	 */

	public default <E extends TreeNodeObject<E> & Iterable<E>> E convertNodeSubtree(Function<N, E> convertFn) {
		E out = convertFn.apply(copyNode(false));
		traverseOperation(out, (copyRootNode, origCurNode) -> {
			if (origCurNode.equals(getInstance()))
				return; // dont do anything on initial node
			String target = origCurNode.getParent().getLocationCodeFromNode(getInstance());
			copyRootNode.getFromLocationCode(target).addChild(convertFn.apply(origCurNode.copyNode(true)));
		});
		return out;
	}
	
	/*
	 * copies a new node structure from current subtree with no additional data
	 */
	public default <E extends TreeNodeObject<E> & Iterable<E>> E copyNodeSubtreeStructure( Supplier<E> newNodeFn ) {
		return convertNodeSubtree( n -> newNodeFn.get() );
	}

	// NODE OPERATIONS //////////////////////////////////////////////////////

	/*
	 * - recursive function for building, copying and converting trees to different
	 * types - iterates from the node that calls the operation - E = input data
	 * object, nodeFn<data, current iterated node N > - can build tree by adding
	 * children to current node calling fn, but will not work if adding children
	 * earlier in tree
	 */
	public default <E> void traverseOperation(E data, BiConsumer<E, N> nodeFn) {
		traverseOperationFn(data, nodeFn, getDepth());
	}

	default <E> void traverseOperationFn(E data, BiConsumer<E, N> nodeFn, int startDepth) {
		nodeFn.accept(data, getInstance());
		N nextNode = getNextNodeDFS(startDepth);
		if (nextNode == null)
			return; // reached last node
		nextNode.traverseOperationFn(data, nodeFn, startDepth);
	}
	
	default void nodeOperation( Consumer<N> fn ) {
		traverseOperation( null, (e,n) -> fn.accept(n) );
	}
	
	

	public default <E> void bottomUpOperation(E data, BiConsumer<E, N> nodeFn) { // recurses operation to children, then
																					// runs fn
		if (hasChildren()) {
//			for (N child : getChildren())
//				child.bottomUpOperation(data, nodeFn); // used for ar calc
			this.forEachChild(nc -> nc.bottomUpOperation(data, nodeFn) ); // used for ar calc);
		}
		nodeFn.accept(data, getInstance()); // after applying operation recursively to children, function then accepts
	}
	
	
	// use forEachLeafRecursive(Consumer<N> fn) instead
//	public default void leafOperation( Consumer<N> fn ) {
//		if( hasChildren() ) for( N c : getChildren() ) c.leafOperation( fn );
//		else fn.accept( getInstance() );
//	}
//	

	
	// BUILD FNS ///////////////////////////////////////////////

	public default TreeBuilder<N> buildTree() {
		return new TreeNodeFunctions.TreeBuilder<N>(getInstance());
	}

	// SORT METHODS ////////////////////////////////////////////
		// TODO: add custom sort behavior? rather than default sort of strings as alphabetical for example, instead by length

	public default <E> void sortChildren(Function<N, E> sortParam) {
		if (!hasChildren())
			return;
		Comparator<N> cFn = (e1, e2) -> 0;
		E type = sortParam.apply(getInstance());
		if (type instanceof Float || type instanceof Integer)
			cFn = (e1, e2) -> (int) ((Float) sortParam.apply(e1) - (Float) sortParam.apply(e2));
		else if (type instanceof String)
			cFn = (e1, e2) -> ((String) sortParam.apply(e1)).compareTo((String) sortParam.apply(e2));
		Collections.sort(getCore().nodeList.subList(getFirstChildIndex(), getFirstChildIndex() + getChildCount()), cFn);
		for (int i = 0; i < getChildCount(); i++) {
			get(i).setIndex(getFirstChildIndex() + i);
			for (N nChild : get(i).getChildren())
				nChild.setParentIndex(get(i).getIndex());
		}
		markLeafCacheDirty();
	}
	
	public default <E> void shuffleChildren() {
		if (!hasChildren())
			return;
		Collections.shuffle(getCore().nodeList.subList(getFirstChildIndex(), getFirstChildIndex() + getChildCount()));
		for (int i = 0; i < getChildCount(); i++) {
			get(i).setIndex(getFirstChildIndex() + i);
			for (N nChild : get(i).getChildren())
				nChild.setParentIndex(get(i).getIndex());
		}
		markLeafCacheDirty();
	}

	public default <E> void sortChildrenReverse(Function<N, E> sortParam) {
		if (!hasChildren())
			return;
		Comparator<N> cFn = (e1, e2) -> 0;
		E type = sortParam.apply(getInstance());
		if (type instanceof Float || type instanceof Integer)
			cFn = (e1, e2) -> (int) ((Float) sortParam.apply(e1) - (Float) sortParam.apply(e2));
		else if (type instanceof String)
			cFn = (e1, e2) -> ((String) sortParam.apply(e1)).compareTo((String) sortParam.apply(e2));
		Collections.sort(getCore().nodeList.subList(getFirstChildIndex(), getFirstChildIndex() + getChildCount()), cFn);
		Collections.reverse(getCore().nodeList.subList(getFirstChildIndex(), getFirstChildIndex() + getChildCount()));
		for (int i = 0; i < getChildCount(); i++) {
			get(i).setIndex(getFirstChildIndex() + i);
			for (N nChild : get(i).getChildren())
				nChild.setParentIndex(get(i).getIndex());
		}
		markLeafCacheDirty();
	}

	public default <E> void sortAll(Function<N, E> sortParam) {
		traverseOperation(sortParam, (f, n) -> {
			if (n.hasChildren())
				n.sortChildren(f);
		});
		markLeafCacheDirty();
	}

	public default <E> void sortAllReverse(Function<N, E> sortParam) {
		traverseOperation(sortParam, (f, n) -> {
			if (n.hasChildren())
				n.sortChildrenReverse(f);
		});
		markLeafCacheDirty();
	}
	
	public default <E> void shuffleAll() {
		traverseOperation(null, (f, n) -> {
			if (n.hasChildren())
				n.shuffleChildren();
		});
		markLeafCacheDirty();
	}
	
	
	// UNTESTED
	public default void reverseChildren() {
	    if (!hasChildren()) return;

	    // Reverse the contiguous slice containing this node's direct children
	    List<N> kids = getCore().nodeList.subList(
	        getFirstChildIndex(),
	        getFirstChildIndex() + getChildCount()
	    );
	    Collections.reverse(kids);

	    // Re-index children to match their new positions
	    for (int i = 0; i < getChildCount(); i++) {
	        N child = get(i); // now reflects reversed order
	        child.setIndex(getFirstChildIndex() + i);

	        // Ensure grandchildren still point at the child's (possibly changed) index
	        // (Usually already correct if everything is consistent, but this mirrors your other methods.)
	        for (N gc : child.getChildren()) {
	            gc.setParentIndex(child.getIndex());
	        }
	    }
	    markLeafCacheDirty();
	}

	
	

	// JSON FNS /////////////////////////////////////////////////
	
	public default void saveTreeJSON( String filename, PApplet pa ) {
		   pa.saveJSONObject( toJSON(), filename);
		}
	
	// overload to loadTree(filename, pa) in subclass with the String->N you are trying to create built in
	public static <E extends TreeNodeObject<E> & Iterable<E>> E loadTreeJSON(String filename, Function<String,E> fn, PApplet pa ) {
		  JSONObject o = pa.loadJSONObject(filename);
		  return fromJSON(o,fn);
		}
	

	// override in implementing class:
	default JSONObject dataToJSON(JSONObject json) { return json; }

	default void dataFromJSON(JSONObject data) {}
	
	// override if using a polymorphic tree with multiple types
	default String getJSONType() { return "default"; }

	default JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.setString("type", getJSONType());

		JSONObject data = dataToJSON( new JSONObject() );
		o.setJSONObject("data", data);

		JSONArray kids = new JSONArray();
		for (N c : getChildren()) {
			kids.append(c.toJSON());
		}
		o.setJSONArray("children", kids);

		return o;
	}

	static <E extends TreeNodeObject<E> & Iterable<E>> E fromJSON(JSONObject o, Function<String, E> factoryByType) {
		// require "type" in node
		String type = o.getString("type", null);
		if (type == null) throw new RuntimeException("Missing 'type' string at JSON node root.");
		
		JSONObject data = o.getJSONObject("data");
		if (data == null) data = new JSONObject();

		E n = factoryByType.apply(type);
		if (n == null) throw new RuntimeException("Factory returned null for type: " + type);

		n.dataFromJSON(data);

		JSONArray kids = o.getJSONArray("children");
		if (kids != null) {
			for (int i = 0; i < kids.size(); i++) {
				n.addChild(TreeNodeObject.fromJSON(kids.getJSONObject(i), factoryByType));
			}
		}
		return n;
	}

	  
	
	
	// DATA LIST FUNCTIONS //////////////////////////////////////////////////////////////

	public default <E> List<E> createDataList(E value) {
		return IntStream.range(0, getTotalSize()).mapToObj(i -> value).collect(Collectors.toList());
	}
	
	public default <E> List<E> createDataList(Function<N,E> fn ) {
		List<E> out = this.<E>createDataList((E) null);
		for( N node : getInstance() ) out.set( node.getIndex(), fn.apply(node));
		return out;
	}

	public default <D> void applyDataList(N node, List<D> dataList,
			BiConsumer<N, D> fn) {
		if (node.getTotalSize() != dataList.size())
			throw new UnsupportedOperationException("data list size not equal to node size");
		node.traverseOperation(dataList, (dList, n) -> fn.accept(n, dList.get(n.getIndex())));
	}

	public static <I, O> List<O> convertDataList(List<I> dataList, Function<I, O> convertFn) {
		return dataList.stream().map(i -> convertFn.apply(i)).collect(Collectors.toList());
	}
	
	public default <D> void dataListOperation( List<D> dataList, BiConsumer<N,D> fn ) {
		traverseOperation( dataList, (dList,n) -> {
			fn.accept(n,dList.get(n.getIndex()));
		} );
	}
	
	public default <E> void dataListLeafOperation( List<E> data, BiConsumer<N,E> fn ) {
		if( data.size() != getLeafCount() ) throw new UnsupportedOperationException("data list must be equal to leaf count" );
		List<N> leafs = getLeafs();
		for( int i = 0; i < leafs.size(); i++ ) fn.accept( leafs.get(i), data.get(i) );
	}
	
	public default <E> E getData( List<E> dataList ){
		return dataList.get( getIndex() );
	}
	public default <E> E setData( E data, List<E> dataList ){
		return dataList.set( getIndex(), data );
	}
	public default <E> E modifyData( Function<E,E> dataFn, List<E> dataList ){
		return dataList.set( getIndex(), dataFn.apply(dataList.get(getIndex())) );
	}

	// PRINT FNS ////////////////////////////////////////////////

	public default String nodeString() {
		return "node | index: " + getIndex() + " | depth: " + getDepth() + " | childCt " + getChildCount()
				+ " | firstChild: " + getFirstChildIndex() + " | indexInParent: " + getIndexInParent();
	}

	public default String getTreeStringPreface() {
		return new String(new char[getDepth() + 1]).replace("\0", "  ") + "- ";
	}

	public default String getTreeString(Function<N, Object> nodeToPrintInfo) {
		String out = "";
		for (N cur : getInstance()) {
			String lineBreak = cur.getPrintInfoLineBreak();
			Object printData = nodeToPrintInfo.apply(cur);
			out += lineBreak+cur.getTreeStringPreface() + (printData != null ? printData.toString() : "null") + "\n";
		}
		return out;
	}

	public default void printOperation() {
		printOperation(n -> n);
	}

	public default void printOperation(Function<N, Object> nodeToPrintInfo) {
		System.out.println(getTreeString(nodeToPrintInfo));
	}

}
