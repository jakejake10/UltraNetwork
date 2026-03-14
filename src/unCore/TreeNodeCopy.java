package unCore;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TreeNodeCopy<N extends TreeNodeObject<N> & Iterable<N>>  {
	N getInstance();
	N get(int childIndex);
//	int getChildCount();
//	N defaultConstructor();
	boolean hasChildren();
	List<N> getChildren();
	N getLastChild();
//	int getDepth();
//	int getIndex();
	int getIndexInParent();
//	int getParentIndex();
//	int getFirstChildIndex();
	N getParent();
	boolean hasParent();
	SingularTreeData<N> getCore();
	void transferSubclassFieldsTo(N node);
//	void addChildForce();
//	<T> void traverseOperation(T data, BiConsumer<T, N> nodeFn);
	
	
	
	public default N copyNode(boolean transferNodeFields) {
		N out = getInstance().defaultConstructor();
		if (transferNodeFields)
			transferNodeFieldsTo(out);
		transferSubclassFieldsTo(out);
		return out;
	}
	
	/*
	 * - uses traverse operation to iterate with dft and copy a subtree - for each
	 * node in existing tree - gets the location code from root of that nodes parent
	 * - with dfs, the parent of the existing node should already exist in copied
	 * version - the parent location code is used to find the matching node from the
	 * copied tree root reference, and a child is added to it
	 */

	public default N copyNodeSubtree() {
		getInstance().markLeafCacheDirty();
		N out = copyNode(false);
		copyNodeSubtreeRecursive( getInstance(), out, n -> n.copyNode(false) );
		return out;
	}
	
	

	static <N extends TreeNodeObject<N> & Iterable<N>, E extends TreeNodeObject<E> & Iterable<E>> void copyNodeSubtreeRecursive(
			N from, E to, Function<N, E> convertFn) {
		for (int i = 0; i < from.getChildCount(); i++) {
			N fromChild = from.get(i);
			E toChild = convertFn.apply(fromChild.copyNode(false)); // false, not true
			to.addChildForce(toChild);
			copyNodeSubtreeRecursive(fromChild, toChild, convertFn);
		}
	}
	

	/*
	 * - generates a copy of the tree structure of the output type of the convertFn
	 * - uses copynode with nodefields transferred so convertfn has access to
	 * original fields
	 */

	public default <E extends TreeNodeObject<E> & Iterable<E>> E convertNodeSubtree(Function<N, E> convertFn) {
	    E out = convertFn.apply(copyNode(false));
	    copyNodeSubtreeRecursive(getInstance(), out, convertFn);
	    return out;
	}
	
	/*
	 * copies a new node structure from current subtree with no additional data
	 */
	public default <E extends TreeNodeObject<E> & Iterable<E>> E copyNodeSubtreeStructure( Supplier<E> newNodeFn ) {
		return convertNodeSubtree( n -> newNodeFn.get() );
	}
	
	
	/**
	 * changes a variable reference to a new node in the tree cannot shift focus
	 * between trees
	 * 
	 * @param node
	 */

	public default void shiftFocus(N node) {
		getInstance().setCore(node.getCore()); // check if core is from another tree and only replace if then? atomic int in
									// coredata?
		node.transferNodeFieldsTo(getInstance()); // node fields now identical to input node
		node.transferSubclassFieldsTo(getInstance()); // subclass fields now identical
		getCore().nodeList.set(getInstance().getIndex(), getInstance()); // save current node over original
	}

	

	public default void transferNodeFieldsTo(N input) {
		input.setIndex(getInstance().getIndex());
		input.setParentIndex(getInstance().getParentIndex());
		input.setFirstChildIndex(getInstance().getFirstChildIndex());
		input.setChildCount(getInstance().getChildCount());
		input.setDepth(getInstance().getDepth());
	}

	
	
	
	
	
}
