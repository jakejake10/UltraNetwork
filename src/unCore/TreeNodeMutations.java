package unCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TreeNodeMutations<N extends TreeNodeObject<N> & Iterable<N>> {

	N getInstance();


	/**
	 * node adding update override the addChild method in subclass for added
	 * functionality call super.addChild()
	 */

	// instead of addChild() or addChild(int indexIn)
	public default void addBlankChild() {
	    addChildCore(getInstance().defaultConstructor(), -1);
	}

	public default void addBlankChild(int indexIn) {
	    addChildCore(getInstance().defaultConstructor(), indexIn);
	}

	public default void addChild(N childIn, int... indexIn) {
		addChildCore(childIn, indexIn.length > 0 ? indexIn[0] : -1);
	}

	public default void addChildForce(N childIn, int... indexIn) {
		addChildCore(childIn, indexIn.length > 0 ? indexIn[0] : -1, true);
	}

	public default void addChild(Consumer<N> childModFn, int... indexIn) {
	    int pos = indexIn.length > 0 ? indexIn[0] : -1;
	    addChildCore(getInstance().defaultConstructor(), pos);
	    // resolve actual insertion position to retrieve the correct child
	    int actualPos = (pos < 0 || pos >= getInstance().getChildCount() - 1)
	        ? getInstance().getChildCount() - 1
	        : pos;
	    childModFn.accept(getInstance().get(actualPos));
	}

	public default void addChildren(List<N> childData) {
		for (N child : childData)
			addChild(child);
	}

	/*
	 * Adds multiple children, applying a modifier function to each
	 * using corresponding data from the provided list.
	 */
	public default <E> void addChildren(List<E> childData, BiConsumer<N, E> childModFn) {
	    for (E data : childData)
	        addChild(n -> childModFn.accept(n, data));
	}

	/*
	 * Core insertion logic for all addChild variants.
	 * Handles core initialization, insertion position resolution,
	 * index shifting, and reconnection of any pre-existing children
	 * on the incoming node.
	 */
	
	default void addChildCore(N child, int indexIn, boolean... forceAdd) {
	    
	    // STEP 1 — guard and setup
	    checkAllowChildren(forceAdd);
	    ensureCoreInitialized();
	    
	    // STEP 2 — if child already has children, detach them for reconnection
	    List<N> foundChildren = child.hasChildren() ? child.getChildren() : null;
	    resetChildFields(child);
	    
	    // STEP 3 — calculate insertion position and set child fields
	    int insertPos = resolveInsertPosition(indexIn);
	    setChildFields(child, insertPos);
	    
	    // STEP 4 — insert into nodeList and shift indexes
	    shiftNodesRight(child.getIndex(), insertPos);
	    getInstance().getCore().nodeList.add(child.getIndex(), child);
	    child.setCore(getInstance().getCore());
	    
	    // STEP 5 — update parent state and reconnect detached children
	    getInstance().setChildCount(getInstance().getChildCount() + 1);
	    getInstance().addedChildMod();
	    if (foundChildren != null)
	        for (N node : foundChildren)
	            child.addChildForce(node);
	    getInstance().markLeafCacheDirty();
	}
	
	
	
	
	default void checkAllowChildren(boolean... forceAdd) {
	    if (!getInstance().allowChildren() && (forceAdd.length == 0 || !forceAdd[0]))
	        throw new UnsupportedOperationException("this class does not allow child nodes to be added");
	}

	default void ensureCoreInitialized() {
	    if (getInstance().getCore() == null) getInstance().initCore();
	}

	
	/*
	 * Resets structural fields on a child being added.
	 * Required when adding a node that was previously a root of its own tree —
	 * its childCount and firstChild need to be cleared so addChildForce
	 * can correctly reconnect its children at the new depth.
	 */
	
	default void resetChildFields(N child) {
	    child.setChildCount(0);
	    child.setFirstChildIndex(-1);
	}

	default int resolveInsertPosition(int indexIn) {
	    if (indexIn < 0 || !getInstance().hasChildren() || indexIn > getInstance().getChildCount())
	        return getInstance().getChildCount();
	    return indexIn;
	}

	default void setChildFields(N child, int insertPos) {
	    if (!getInstance().hasChildren())
	        getInstance().setFirstChildIndex(getInstance().getTotalSize());
	    child.setIndex(getInstance().getFirstChildIndex() + insertPos);
	    child.setParentIndex(getInstance().getIndex());
	    child.setDepth(getInstance().getDepth() + 1);
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
	
	/*
	 * Removes this node from its parent tree and returns it as a
	 * new standalone root with its own SingularTreeData.
	 * Unlike remove(), the node and its subtree are preserved.
	 */
	public default N detach() {
	    if (getInstance().isRoot())
	        throw new UnsupportedOperationException("Cannot detach root node");
	    N detached = getInstance().copyNodeSubtree();
	    remove();
	    return detached;
	}

	/*
	 * Removes this node from its current position and attaches it
	 * as a child of the given target node. Equivalent to detach + addChild.
	 */
	public default void moveTo(N newParent) {
	    N subtree = detach();
	    newParent.addChild(subtree);
	}

	/*
	 * Swaps the subclass data (type, fill, shape data, etc.) of two nodes
	 * without affecting either node's position or children in the tree.
	 */
	public default void swapNodeData(N other) {
	    N tempA = getInstance().copyNode(false); // save A's data
	    other.transferSubclassFieldsTo(getInstance()); // A gets B's data
	    tempA.transferSubclassFieldsTo(other);          // B gets A's data
	}

	/*
	 * Swaps two subtrees in the tree, each keeping their own children.
	 * Both nodes must belong to the same tree.
	 */
	public default void swapSubtree(N other) {
	    if (getInstance() == other) return;
	    if (!getInstance().getRoot().equals(other.getRoot()))
	        throw new UnsupportedOperationException("Cannot swap nodes from different trees");

	    int iipA   = getInstance().getIndexInParent();
	    int iipB   = other.getIndexInParent();
	    N parentA  = getInstance().getParent();
	    N parentB  = other.getParent();

	    N copyA = getInstance().copyNodeSubtree();
	    N copyB = other.copyNodeSubtree();

	    parentA.get(iipA).remove();
	    parentA.addChildForce(copyB, iipA);

	    parentB.get(iipB).remove();
	    parentB.addChildForce(copyA, iipB);
	}

	/*
	 * Removes this node from the tree and promotes its direct children
	 * to be children of this node's parent, preserving their subtrees.
	 * Inverse of insertParent().
	 */
	public default void mergeChildren() {
	    if (getInstance().isRoot())
	        throw new UnsupportedOperationException("Cannot mergeChildren on root");
	    if (!getInstance().hasChildren()) {
	        remove();
	        return;
	    }
	    N parent = getInstance().getParent();
	    int iip  = getInstance().getIndexInParent();

	    // copy children before removing since remove clears them
	    List<N> children = new ArrayList<>();
	    for (int i = 0; i < getInstance().getChildCount(); i++)
	        children.add(getInstance().get(i).copyNodeSubtree());

	    remove();

	    // insert children at the original position in reverse order
	    // so they end up in the correct sequence
	    for (int i = children.size() - 1; i >= 0; i--)
	        parent.addChildForce(children.get(i), iip);
	}

	/*
	 * Collapses the entire subtree so all leaf nodes become
	 * direct children of this node. All intermediate nodes are removed.
	 */
	public default void flatten() {
	    if (!getInstance().hasChildren()) return;
	    List<N> leaves = getInstance().getLeafs();
	    List<N> leafCopies = new ArrayList<>();
	    for (N leaf : leaves)
	        leafCopies.add(leaf.copyNode(false));
	    getInstance().removeChildren();
	    for (N leaf : leafCopies)
	        getInstance().addChild(leaf);
	}

	/*
	 * Reverses the entire subtree structure at every level —
	 * each node's children are reversed recursively.
	 */
	public default void mirrorSubtree() {
	    getInstance().forEachNode(n -> {
	        if (n.hasChildren()) n.reverseChildren();
	    });
	}

	/*
	 * Distributes a list of values to leaf nodes in DFS order.
	 * List size must match leaf count.
	 */
	// e.g. distribute a list of colors to leaf shapes
	public default <E> void distributeToLeaves(List<E> data, BiConsumer<N, E> setterFn) {
	    if (data.size() != getInstance().getLeafCount())
	        throw new UnsupportedOperationException(
	            "data list size " + data.size() +
	            " does not match leaf count " + getInstance().getLeafCount());
	    getInstance().forEachLeafWithIndex((n, i) -> setterFn.accept(n, data.get(i)));
	}


}
