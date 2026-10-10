package unCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.*;

public interface TreeNodeQueries<N extends TreeNodeObject<N> & Iterable<N>> {
	/*
	 * gets the size of entire tree
	 */

	N getInstance();


	List<N> getChildren();

	N get(int index);

	SingularTreeData<N> getCore();



	boolean isRoot();

	boolean isLeaf();

	boolean hasChildren();

//    void ensureLeafCache();
	boolean hasParent();

	N getParent();

	int getIndexInParent();
	
	
	
	

	public default int getTotalSize() {
		return getCore() != null ? getCore().nodeList.size() : 1;
	}

	/*
	 * gets the size of subtree
	 */

	public default int getSubtreeSize() {
		int size = 1;
		for (int i = 0; i < getInstance().getChildCount(); i++) {
			size += get(i).getSubtreeSize();
		}
		return size;
	}

	/*
	 * gets max depth of a subtree, relative to node calling it
	 */
	default int getMaxDepth() {
		return getTotalTreeMaxDepth() - getInstance().getDepth();
	}

	/*
	 * gets max depth of a tree relative to root node
	 */
	default int getTotalTreeMaxDepth() {

		int max = getInstance().getDepth();

		for (int i = 0; i < getInstance().getChildCount(); i++) {
			int d = get(i).getTotalTreeMaxDepth();
			if (d > max)
				max = d;
		}

		return max;
	}

	public default List<N> getNodesAtDepth(int depth) {
		return findAll(n -> n.getDepth() == depth);
	}

	public default List<N> getLeafs() {
		return getLeafsRecursive(new ArrayList<N>());
	}

	default List<N> getLeafsRecursive(List<N> data) {
		if (!hasChildren()) {
			data.add(getInstance());
		} else {
			getInstance().forEachChild(n -> n.getLeafsRecursive(data));
		}
		return data;
	}

	public default N getRandomLeaf() {
		List<N> leafs = getLeafs();
		return leafs.get((int) Math.floor(Math.random() * leafs.size()));
	}

	public default int getLeafCount() {
		SingularTreeData<N> core = getCore();

		// detached / fallback case
		if (core == null) {
			final int[] count = { 0 };
			getInstance().forEachLeaf(n -> count[0]++);
			return count[0];
		}

		// root gets the fast cached path
		if (isRoot()) {
			getInstance().ensureLeafCache();
			return core.leafIndexes.size();
		}

		// subtree uses recursive traversal for correctness
		final int[] count = { 0 };
		getInstance().forEachLeaf(n -> count[0]++);
		return count[0];
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
		for (int i = 0; i < getInstance().getChildCount(); i++)
			if (!get(i).isLeaf())
				return false;
		return true;
	}

	public default N findFirst(Predicate<N> matchFn) {
		if (matchFn.test(getInstance()))
			return getInstance();
		for (int i = 0; i < getInstance().getChildCount(); i++) {
			N found = get(i).findFirst(matchFn);
			if (found != null)
				return found;
		}
		return null;
	}

	public default List<N> findAll(Predicate<N> matchFn) {
		List<N> out = new ArrayList<>();
		findAllRecursive(matchFn, out);
		return out;
	}

	default void findAllRecursive(Predicate<N> matchFn, List<N> out) {
		if (matchFn.test(getInstance()))
			out.add(getInstance());
		for (int i = 0; i < getInstance().getChildCount(); i++)
			get(i).findAllRecursive(matchFn, out);
	}

	public default N getRandomNode() {
		return getCore().nodeList.get((int) Math.floor((Math.random() * getCore().nodeList.size())));
	}

	/*
	 * returns list of child indexes from root to this node
	 */
	public default List<Integer> getLocationCodeFromRoot() {
		List<Integer> out = new ArrayList<>();
		N curNode = getInstance();
		while (curNode.hasParent()) {
			out.add(0, curNode.getIndexInParent()); // prepend
			curNode = curNode.getParent();
		}
		return out;
	}

	/*
	 * // returns the path from a given ancestor node down to this node - if input
	 * contains node in subtree, just get the path to root and clip at input depth
	 */

	public default List<Integer> getLocationCodeFromNode(N input) {
		return getLocationCodeFromRoot().subList(input.getDepth(), getLocationCodeFromRoot().size());
	}

	public default N getFromLocationCode(String locationCode) {
		N curNode = getInstance();
		for (int i = 0; i < locationCode.length(); i++) {
			int childIndex = Character.getNumericValue(locationCode.charAt(i));
			curNode = curNode.get(childIndex);
		}
		return curNode;
	}

	public default N getFromLocationCode(List<Integer> locationCode) {
		N curNode = getInstance();
		for (int childIndex : locationCode) {
			curNode = curNode.get(childIndex);
		}
		return curNode;
	}

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

	public default N getAncestorAtDepth(int depth) {
		if (getInstance().getDepth() < depth)
			throw new UnsupportedOperationException("ancestor depth cannot be larger than node depth");
		N out = getInstance();
		while (out.getDepth() > depth) {
			out = out.getParent();
		}
		return out;
	}
	
	
	/*
	 * Returns all siblings of this node — all children of the parent
	 * excluding this node. Returns empty list if node is root.
	 */
	public default List<N> getSiblings() {
	    if (!hasParent()) return new ArrayList<>();
	    List<N> siblings = new ArrayList<>();
	    for (int i = 0; i < getParent().getChildCount(); i++) {
	        N sibling = getParent().get(i);
	        if (sibling != getInstance()) siblings.add(sibling);
	    }
	    return siblings;
	}

	/*
	 * Walks up n levels and returns that ancestor.
	 * Returns root if n exceeds tree depth.
	 */
	public default N getNthAncestor(int n) {
	    N cur = getInstance();
	    for (int i = 0; i < n; i++) {
	        if (!cur.hasParent()) return cur;
	        cur = cur.getParent();
	    }
	    return cur;
	}

	/*
	 * Returns true if this node is an ancestor of the given node —
	 * i.e. the given node is somewhere in this node's subtree.
	 */
	public default boolean isAncestorOf(N node) {
	    N cur = node;
	    while (cur.hasParent()) {
	        cur = cur.getParent();
	        if (cur == getInstance()) return true;
	    }
	    return false;
	}

	/*
	 * Returns true if this node is a descendant of the given node —
	 * i.e. this node is somewhere in the given node's subtree.
	 */
	public default boolean isDescendantOf(N node) {
	    return node.isAncestorOf(getInstance());
	}

	/*
	 * Returns the path from root down to this node as an ordered list.
	 * First element is root, last element is this node.
	 */
	public default List<N> getPath() {
	    List<N> path = new ArrayList<>();
	    N cur = getInstance();
	    while (cur.hasParent()) {
	        path.add(0, cur);
	        cur = cur.getParent();
	    }
	    path.add(0, cur); // add root
	    return path;
	}

	/*
	 * Iterates all leaves with their sequential leaf index.
	 * Index is 0-based and reflects leaf order in DFS traversal.
	 */
	public default void forEachLeafWithIndex(BiConsumer<N, Integer> fn) {
	    final int[] index = { 0 };
	    getInstance().forEachLeaf(n -> {
	        fn.accept(n, index[0]);
	        index[0]++;
	    });
	}

	/*
	 * Returns the sequential index of this node among all leaves.
	 * Returns -1 if this node is not a leaf.
	 */
	public default int getLeafIndex() {
	    if (!isLeaf()) return -1;
	    final int[] index = { -1 };
	    final int[] count = { 0 };
	    getInstance().getRoot().forEachLeaf(n -> {
	        if (n == getInstance()) index[0] = count[0];
	        count[0]++;
	    });
	    return index[0];
	}

	/*
	 * Returns all nodes at a depth relative to this node.
	 * depth 0 returns this node, depth 1 returns direct children, etc.
	 */
	public default List<N> getNodesAtRelativeDepth(int relativeDepth) {
	    int targetDepth = getInstance().getDepth() + relativeDepth;
	    return findAll(n -> n.getDepth() == targetDepth);
	}

	/*
	 * Returns a Stream of all nodes in DFS order.
	 * Enables use of Java stream API: filter, map, collect, etc.
	 */
	public default Stream<N> stream() {
	    return StreamSupport.stream(
	        Spliterators.spliteratorUnknownSize(
	            getInstance().nodeIterator(),
	            Spliterator.ORDERED
	        ), false);
	}

	/*
	 * Finds the first N nodes matching the predicate and returns them.
	 * More efficient than findAll when you only need a limited number of results.
	 */
	public default List<N> findFirst(Predicate<N> matchFn, int maxResults) {
	    List<N> out = new ArrayList<>();
	    findFirstLimited(matchFn, maxResults, out);
	    return out;
	}

	default void findFirstLimited(Predicate<N> matchFn, int maxResults, List<N> out) {
	    if (out.size() >= maxResults) return;
	    if (matchFn.test(getInstance())) out.add(getInstance());
	    for (int i = 0; i < getInstance().getChildCount(); i++)
	        get(i).findFirstLimited(matchFn, maxResults, out);
	}

	/*
	 * Iterates all nodes at the given absolute depth without building a list.
	 */
	public default void forEachNodeAtDepth(int depth, Consumer<N> fn) {
	    if (getInstance().getDepth() == depth) {
	        fn.accept(getInstance());
	        return;
	    }
	    if (getInstance().getDepth() < depth)
	    	getInstance().forEachChild(n -> n.forEachNodeAtDepth(depth, fn));
	}

	/*
	 * Splits leaves into two lists based on a predicate.
	 * First list contains leaves where predicate is true,
	 * second list contains leaves where predicate is false.
	 */
	public default List<List<N>> partition(Predicate<N> matchFn) {
	    List<N> matched   = new ArrayList<>();
	    List<N> unmatched = new ArrayList<>();
	    getInstance().forEachLeaf(n -> {
	        if (matchFn.test(n)) matched.add(n);
	        else                 unmatched.add(n);
	    });
	    List<List<N>> out = new ArrayList<>();
	    out.add(matched);
	    out.add(unmatched);
	    return out;
	}

	/*
	 * Transforms all leaf nodes using the given function and returns
	 * the results as a list in DFS leaf order.
	 */
	public default <E> List<E> mapLeafs(Function<N, E> fn) {
	    List<E> out = new ArrayList<>();
	    getInstance().forEachLeaf(n -> out.add(fn.apply(n)));
	    return out;
	}

	/*
	 * Simple left-fold over all nodes in DFS order.
	 * Accumulates a result by applying fn to each node and the running total.
	 */
	public default <T> T reduce(T identity, BiFunction<T, N, T> fn) {
	    final Object[] acc = { identity };
	    getInstance().forEachNode(n -> acc[0] = fn.apply((T) acc[0], n));
	    return (T) acc[0];
	}

	/*
	 * Bottom-up reduce where each leaf is transformed by leafFn,
	 * and each branch node receives its own value and its children's
	 * reduced values via branchFn. Useful for computing aggregate
	 * values like bounding boxes or summing a finance tree.
	 */
	public default <T> T reduceBottomUp(Function<N, T> leafFn, BiFunction<N, List<T>, T> branchFn) {
	    if (isLeaf()) return leafFn.apply(getInstance());
	    List<T> childResults = new ArrayList<>();
	    for (int i = 0; i < getInstance().getChildCount(); i++)
	        childResults.add(get(i).reduceBottomUp(leafFn, branchFn));
	    return branchFn.apply(getInstance(), childResults);
	}

}
