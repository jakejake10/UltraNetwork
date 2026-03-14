package unCore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface TreeNodeQueries<N extends TreeNodeObject<N> & Iterable<N>> {
	/*
	 * gets the size of entire tree
	 */

	N getInstance();

//	int getChildCount();

	List<N> getChildren();

	N get(int index);

	SingularTreeData<N> getCore();

//	int getDepth();

//	void forEachLeafRecursive(Consumer<N> fn);
//
//	void forEachChild(Consumer<N> fn);

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
			getInstance().forEachLeafRecursive(n -> count[0]++);
			return count[0];
		}

		// root gets the fast cached path
		if (isRoot()) {
			getInstance().ensureLeafCache();
			return core.leafIndexes.size();
		}

		// subtree uses recursive traversal for correctness
		final int[] count = { 0 };
		getInstance().forEachLeafRecursive(n -> count[0]++);
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

}
