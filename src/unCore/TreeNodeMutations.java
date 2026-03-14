package unCore;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TreeNodeMutations<N extends TreeNodeObject<N> & Iterable<N>> {

	N getInstance();

//	N get(int childIndex);
//	int getChildCount();
//	N defaultConstructor();
//	boolean hasChildren();
//	List<N> getChildren();
//	int getFirstChildIndex();
//	N getLastChild();
//	int getIndex();
//	int getDepth();
//	int getParentIndex();
//	int getIndexInParent();
//	N getParent();
//	boolean hasParent();
//	boolean isRoot();
//	SingularTreeData<N> getCore();
//	boolean allowChildren();
//	void addedChildMod();
//	void setIndex();
//	void setParentIndex( int index );
//	void setFirstChildIndex( int index);
//	void setDepth( int depth );
//	void setChildCount( int size );
//	void setCore(SingularTreeData<N> core );
//	N initCore();
//	void shiftFocus(N input);

//	void transferNodeFieldsTo(N node);

//	<T> void bottomUpOperation(T data, BiConsumer<T, N> nodeFn);
//	<T> void traverseOperation(T data, BiConsumer<T, N> nodeFn);
//	N copyNodeSubtree();
//	<T> void forEachNode(T data, BiConsumer<T, N> nodeFn);
//
//	void forEachNode(Consumer<N> fn);
//
//	<T> void forEachNodeBottomUp(T data, BiConsumer<T, N> nodeFn);
//
//	void forEachNodeBottomUp(Consumer<N> nodeFn);

	/**
	 * node adding update override the addChild method in subclass for added
	 * functionality call super.addChild()
	 */
	public default void addChild(int... indexIn) {
		addChildTreeDataFn(getInstance().defaultConstructor(), indexIn.length > 0 ? indexIn[0] : -1);
	}

	public default void addChild(N childIn, int... indexIn) {
		addChildTreeDataFn(childIn, indexIn.length > 0 ? indexIn[0] : -1);
	}

	public default void addChildForce(N childIn, int... indexIn) {
		addChildTreeDataFn(childIn, indexIn.length > 0 ? indexIn[0] : -1, true);
	}

	public default void addChild(Consumer<N> childModFn, int... indexIn) {
		addChildTreeDataFn(getInstance().defaultConstructor(), indexIn.length > 0 ? indexIn[0] : -1);
		N addedChild = indexIn.length == 0 ? getInstance().getLastChild() : getInstance().get(indexIn[0]);
		childModFn.accept(addedChild);
	}

	public default void addChildren(List<N> childData) {
		for (N child : childData)
			addChild(child);
	}

	/*
	 * use case for this?
	 */
	public default <E> void addChildren(List<E> childData, BiConsumer<N, E> childModFn) {
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

	default void addChildTreeDataFn(N child, int indexIn, boolean... forceAdd) {
		if ((!getInstance().allowChildren() && forceAdd.length == 0)
				|| (!getInstance().allowChildren() && !forceAdd[0]))
			throw new UnsupportedOperationException("this class does not allow child nodes to be added");
		if (getInstance().getCore() == null)
			getInstance().initCore();
		List<N> foundChildren = null;
		if (child.hasChildren())
			foundChildren = child.getChildren(); // need to pull these out and reconnect

		int curIndexInParent = indexIn < 0 || !getInstance().hasChildren() || indexIn > getInstance().getChildCount()
				? getInstance().getChildCount()
				: indexIn;
		if (!getInstance().hasChildren())
			getInstance().setFirstChildIndex(getInstance().getTotalSize());
		child.setChildCount(0); // if child has children, it's size needs to be reset to 0, so adding back
								// children shows proper size
		child.setFirstChildIndex(-1); // if child has children, first child needs to be reset to -1 for future add ops
										// to work properly
		child.setIndex(getInstance().getFirstChildIndex() + curIndexInParent);
		child.setParentIndex(getInstance().getIndex());
		child.setDepth(getInstance().getDepth() + 1);
		shiftNodesRight(child.getIndex(), curIndexInParent);
		getInstance().getCore().nodeList.add(child.getIndex(), child); // add to index after loc, and after any pre
																		// existing children
		child.setCore(getInstance().getCore());

		getInstance().setChildCount(getInstance().getChildCount() + 1);
		getInstance().addedChildMod(); // comes from subclass
		if (foundChildren != null)
			for (N node : foundChildren)
				child.addChildForce(node);
		getInstance().markLeafCacheDirty();
	}

	default void shiftNodesRight(int chIndex, int iip) { // addition
		if (chIndex == getInstance().getTotalSize())
			return;
		int lastChangedParent = -2;
		for (int i = 0; i < getInstance().getTotalSize(); i++) {
			N curNode = getInstance().getCore().nodeList.get(i);
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
		if (index == getInstance().getTotalSize())
			return;
		for (int i = 0; i < getInstance().getTotalSize(); i++) {
			N curNode = getInstance().getCore().nodeList.get(i);
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
		if (getInstance().isRoot())
			throw new UnsupportedOperationException("Cannot call remove() on root node");
		getInstance().forEachNodeBottomUp(null, (e, n) -> {
			if (n.hasChildren())
				for (N child : n.getChildren()) {
					n.getCore().nodeList.remove(child.getIndex());
					n.shiftNodesLeft(child.getIndex());
				}
			n.setChildCount(0);
			n.setFirstChildIndex(-1);
		});
		getInstance().getCore().nodeList.remove(getInstance().getIndex()); // added
		shiftNodesLeft(getInstance().getIndex());
		getInstance().getParent().setChildCount(getInstance().getParent().getChildCount() - 1);
		if (getInstance().getParent().getChildCount() == 0)
			getInstance().getParent().setFirstChildIndex(-1);
		// subClassRemoveChildUpdate();
		getInstance().markLeafCacheDirty();
	}

	public default void removeChild(int index) {
		getInstance().get(index).remove();
	}

	public default <E> void removeChild(int index, List<E> treeData) {
		if (getInstance().get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < getInstance().get(index).getChildCount(); i++)
				getInstance().get(index).removeChild(i, treeData);
		int childIndex = getInstance().get(index).getIndex();
		getInstance().setChildCount(getInstance().getChildCount() - 1); // childCt--;
		if (getInstance().getChildCount() == 0)
			getInstance().setFirstChildIndex(-1);
		getInstance().getCore().nodeList.remove(childIndex);
		shiftNodesLeft(childIndex);
		treeData.remove(childIndex);
	}

	public default void removeChildren() {
		for (int i = getInstance().getChildCount() - 1; i > -1; i--)
			removeChild(i);
	}

	/*
	 * requires copyNode fns to be in place how it works: - called on a node, takes
	 * in new node and replaces it - modifies input node based on conditions: - if
	 * the input node has parents, a subtree copy is made so the input node is now
	 * the root - if node is root(no core) and input node has core - input node's
	 * core is imported as current nodes core, all fc indexes now point to new
	 * nodelist - if node is root and input node is another subclass - throws error,
	 * original variable that is referencing root can never point to different
	 * subtype - works at root level with same class nodes, because of the
	 * transferSubclassFieldsTo() fn - original variable reference will not have any
	 * new fields of the copied different subclass - if node is not root - input
	 * node inserted from parent at same index of original removed node - done using
	 * addChildForce(), incase of any subclasses that prevent child creation
	 */

	public default N replaceNodeSubtree(N inputNode) {
		getInstance().markLeafCacheDirty();
		N insertedNode = null;
		if (inputNode.hasChildren())
			insertedNode = inputNode.copyNodeSubtree();
		else
			insertedNode = inputNode.copyNode(true); // no node field data
		if (!getInstance().isRoot()) { // just replace node with new node
			int iip = getInstance().getIndexInParent();
			N parent = getInstance().getParent();
			parent.get(iip).remove();
			parent.addChildForce(insertedNode, iip);
			return insertedNode;
		}
		// root is being replaced
		else
			return insertedNode;

	}

	/*
	 * replaces an individual node without affecting nodes downstream can swap out a
	 * root node, but need to save to a new variable returns new node replacement
	 */
	public default N replaceIndividualNode(N inputNode) {
		if (inputNode.hasChildren())
			throw new UnsupportedOperationException("replaceIndividualNode() input cannot have children");
		N insertedNode = inputNode.copyNode(false);
		getInstance().transferNodeFieldsTo(insertedNode);
		if (!getInstance().isRoot()) {
			getInstance().transferNodeFieldsTo(inputNode);
			if (getInstance().getCore() == null)
				getInstance().initCore();
			getInstance().getCore().nodeList.set(getInstance().getIndex(), insertedNode);
		} else {
			insertedNode.setCore(getInstance().getCore());
			insertedNode.getCore().nodeList.set(0, insertedNode);
		}
		return insertedNode;
	}

	public default N replaceWith(Function<N, N> replaceFn) {
		return replaceNodeSubtree(replaceFn.apply(getInstance()));
	}

	/**
	 * changes node to a child of a new blank node returns new parent if doing
	 * operation on root node variable, do TreeNode<?> root = root.insertParent()
	 * variables that call this will now point to new inserted parent
	 */

	public default N insertParent() {
		getInstance().markLeafCacheDirty();
		N newNode = getInstance().defaultConstructor();
		newNode.addChild(getInstance().copyNodeSubtree());
		if (getInstance().hasParent())
			getInstance().getParent().get(getInstance().getIndexInParent()).replaceNodeSubtree(newNode);
		else
			getInstance().shiftFocus(newNode.get(0));
		return getInstance();
	}

	// SORT METHODS ////////////////////////////////////////////
	// TODO: add custom sort behavior? rather than default sort of strings as
	// alphabetical for example, instead by length
	
	
	

//	public default <E> void sortChildren(Function<N, E> sortParam) {
//		if (!getInstance().hasChildren())
//			return;
//		Comparator<N> cFn = (e1, e2) -> 0;
//		E type = sortParam.apply(getInstance());
//		if (type instanceof Float)
//			cFn = (e1, e2) -> Float.compare((Float) sortParam.apply(e1), (Float) sortParam.apply(e2));
//		else if (type instanceof Integer)
//			cFn = (e1, e2) -> Integer.compare((Integer) sortParam.apply(e1), (Integer) sortParam.apply(e2));
//		else if (type instanceof String)
//			cFn = (e1, e2) -> ((String) sortParam.apply(e1)).compareTo((String) sortParam.apply(e2));
//		Collections.sort(getInstance().getCore().nodeList.subList(getInstance().getFirstChildIndex(),
//				getInstance().getFirstChildIndex() + getInstance().getChildCount()), cFn);
//		for (int i = 0; i < getInstance().getChildCount(); i++) {
//			getInstance().get(i).setIndex(getInstance().getFirstChildIndex() + i);
//			for (N nChild : getInstance().get(i).getChildren())
//				nChild.setParentIndex(getInstance().get(i).getIndex());
//		}
//		getInstance().markLeafCacheDirty();
//	}
	
//	default void sortChildrenWithComparator(Comparator<N> cFn) {
//	    Collections.sort(
//	        getInstance().getCore().nodeList.subList(
//	            getInstance().getFirstChildIndex(),
//	            getInstance().getFirstChildIndex() + getInstance().getChildCount()
//	        ), cFn);
//	    for (int i = 0; i < getInstance().getChildCount(); i++) {
//	        getInstance().get(i).setIndex(getInstance().getFirstChildIndex() + i);
//	        for (N child : getInstance().get(i).getChildren())
//	            child.setParentIndex(getInstance().get(i).getIndex());
//	    }
//	    getInstance().markLeafCacheDirty();
//	}
	
	// comparator builder — handles Float, Integer, String; returns no-op for unknown types
	default <E> Comparator<N> buildComparator(Function<N, E> sortParam) {
	    E type = sortParam.apply(getInstance());
	    if (type instanceof Float)
	        return (a, b) -> Float.compare((Float) sortParam.apply(a), (Float) sortParam.apply(b));
	    if (type instanceof Integer)
	        return (a, b) -> Integer.compare((Integer) sortParam.apply(a), (Integer) sortParam.apply(b));
	    if (type instanceof String)
	        return (a, b) -> ((String) sortParam.apply(a)).compareTo((String) sortParam.apply(b));
	    return (a, b) -> 0;
	}

	// applies a comparator to the children slice and re-indexes
	default void sortChildrenWithComparator(Comparator<N> cFn) {
	    Collections.sort(
	        getInstance().getCore().nodeList.subList(
	            getInstance().getFirstChildIndex(),
	            getInstance().getFirstChildIndex() + getInstance().getChildCount()
	        ), cFn);
	    reindexChildren();
	}

	// re-indexes children and their direct children after any reorder operation
	default void reindexChildren() {
	    for (int i = 0; i < getInstance().getChildCount(); i++) {
	        N child = getInstance().get(i);
	        child.setIndex(getInstance().getFirstChildIndex() + i);
	        for (N grandchild : child.getChildren())
	            grandchild.setParentIndex(child.getIndex());
	    }
	    getInstance().markLeafCacheDirty();
	}

	// public API — now just a few lines each

	public default <E> void sortChildren(Function<N, E> sortParam) {
	    if (!getInstance().hasChildren()) return;
	    sortChildrenWithComparator(buildComparator(sortParam));
	}

	public default <E> void sortChildrenReverse(Function<N, E> sortParam) {
	    if (!getInstance().hasChildren()) return;
	    sortChildrenWithComparator(buildComparator(sortParam).reversed());
	}

	public default void shuffleChildren() {
	    if (!getInstance().hasChildren()) return;
	    Collections.shuffle(
	        getInstance().getCore().nodeList.subList(
	            getInstance().getFirstChildIndex(),
	            getInstance().getFirstChildIndex() + getInstance().getChildCount()
	        ));
	    reindexChildren();
	}

	public default void reverseChildren() {
	    if (!getInstance().hasChildren()) return;
	    Collections.reverse(
	        getInstance().getCore().nodeList.subList(
	            getInstance().getFirstChildIndex(),
	            getInstance().getFirstChildIndex() + getInstance().getChildCount()
	        ));
	    reindexChildren();
	}

//	public default <E> void shuffleChildren() {
//		if (!getInstance().hasChildren())
//			return;
//		Collections.shuffle(getInstance().getCore().nodeList.subList(getInstance().getFirstChildIndex(),
//				getInstance().getFirstChildIndex() + getInstance().getChildCount()));
//		for (int i = 0; i < getInstance().getChildCount(); i++) {
//			getInstance().get(i).setIndex(getInstance().getFirstChildIndex() + i);
//			for (N nChild : getInstance().get(i).getChildren())
//				nChild.setParentIndex(getInstance().get(i).getInstance().getIndex());
//		}
//		getInstance().markLeafCacheDirty();
//	}
//
//	public default <E> void sortChildrenReverse(Function<N, E> sortParam) {
//		if (!getInstance().hasChildren())
//			return;
//		Comparator<N> cFn = (e1, e2) -> 0;
//		E type = sortParam.apply(getInstance());
//		if (type instanceof Float)
//			cFn = (e1, e2) -> Float.compare((Float) sortParam.apply(e1), (Float) sortParam.apply(e2));
//		else if (type instanceof Integer)
//			cFn = (e1, e2) -> Integer.compare((Integer) sortParam.apply(e1), (Integer) sortParam.apply(e2));
//		else if (type instanceof String)
//			cFn = (e1, e2) -> ((String) sortParam.apply(e1)).compareTo((String) sortParam.apply(e2));
//		Collections.sort(getInstance().getCore().nodeList.subList(getInstance().getFirstChildIndex(),
//				getInstance().getFirstChildIndex() + getInstance().getChildCount()), cFn);
//		Collections.reverse(getInstance().getCore().nodeList.subList(getInstance().getFirstChildIndex(),
//				getInstance().getFirstChildIndex() + getInstance().getChildCount()));
//		for (int i = 0; i < getInstance().getChildCount(); i++) {
//			getInstance().get(i).setIndex(getInstance().getFirstChildIndex() + i);
//			for (N nChild : getInstance().get(i).getChildren())
//				nChild.setParentIndex(getInstance().get(i).getIndex());
//		}
//		getInstance().markLeafCacheDirty();
//	}
//
////		public default <E> void sortAll(Function<N, E> sortParam) {
////			traverseOperation(sortParam, (f, n) -> {
////				if (n.hasChildren())
////					n.sortChildren(f);
////			});
////			getInstance().markLeafCacheDirty();
////		}
//
//	public default <E> void sortAllReverse(Function<N, E> sortParam) {
//		traverseOperation(sortParam, (f, n) -> {
//			if (n.hasChildren())
//				n.sortChildrenReverse(f);
//		});
//		getInstance().markLeafCacheDirty();
//	}

//		public default <E> void shuffleAll() {
//			traverseOperation(null, (f, n) -> {
//				if (n.hasChildren())
//					n.shuffleChildren();
//			});
//			getInstance().markLeafCacheDirty();
//		}

	public default <E> void sortAll(Function<N, E> sortParam) {
		getInstance().forEachNode(n -> {
			if (n.hasChildren())
				n.sortChildren(sortParam);
		});
		getInstance().markLeafCacheDirty();
	}

	public default void shuffleAll() {
		getInstance().forEachNode(n -> {
			if (n.hasChildren())
				n.shuffleChildren();
		});
		getInstance().markLeafCacheDirty();
	}

//	// UNTESTED
//	public default void reverseChildren() {
//		if (!getInstance().hasChildren())
//			return;
//
//		// Reverse the contiguous slice containing this node's direct children
//		List<N> kids = getInstance().getCore().nodeList.subList(getInstance().getFirstChildIndex(),
//				getInstance().getFirstChildIndex() + getInstance().getChildCount());
//		Collections.reverse(kids);
//
//		// Re-index children to match their new positions
//		for (int i = 0; i < getInstance().getChildCount(); i++) {
//			N child = getInstance().get(i); // now reflects reversed order
//			child.setIndex(getInstance().getFirstChildIndex() + i);
//
//			// Ensure grandchildren still point at the child's (possibly changed) index
//			// (Usually already correct if everything is consistent, but this mirrors your
//			// other methods.)
//			for (N gc : child.getChildren()) {
//				gc.setParentIndex(child.getIndex());
//			}
//		}
//		getInstance().markLeafCacheDirty();
//	}

}
