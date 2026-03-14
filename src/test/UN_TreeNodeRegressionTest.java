package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import unCore.*;
import utTypes.*;
//import utTypes.*;

import java.util.List;
import java.util.function.Function;

import processing.core.PApplet;

import processing.core.PApplet;
import java.util.*;
import java.util.function.Predicate;

public class UN_TreeNodeRegressionTest extends PApplet {

	DataNode<String> root;

	public static void main(String... args) {
		UN_TreeNodeRegressionTest pt = new UN_TreeNodeRegressionTest();
		PApplet.runSketch(new String[] { "UN_TreeNodeRegressionTest" }, pt);
	}

	@Override
	public void settings() {
		size(700, 300);
	}

	@Override
	public void setup() {
		surface.setTitle("TreeNodeObject Regression Test");

		println("=== BUILD INITIAL TREE ===");
		root = buildInitialTree();
		printTreeSummary(root);

		runAllTests();

		noLoop();
	}

	@Override
	public void draw() {
		background(25);
		fill(255);
		text("See console for TreeNodeObject regression test results.", 20, 30);
	}

	// =========================================================
	// TEST RUNNER
	// =========================================================

	void runAllTests() {
		println("\n==================================================");
		println("RUNNING TESTS");
		println("==================================================");

		testBasicCountsAndStructure();
		testTraversalConsistency();
		testFindFirst();
		testBFSTraversal();
		testAddChild();
		testRemoveLeaf();
		testRemoveSubtree();
		testLeafCountConsistency();
		testCachedVsRecursiveLeafTraversal();
		testParentDepthIndexConsistency();
		
		testBFSAfterEdits();

		println("\n==================================================");
		println("ALL TESTS COMPLETE");
		println("==================================================");
	}

	// =========================================================
	// TREE BUILD
	// =========================================================

	DataNode<String> buildInitialTree() {
		// root
		DataNode<String> r = new DataNode<>("root");

		// level 1
		DataNode<String> a = new DataNode<>("A");
		DataNode<String> b = new DataNode<>("B");
		DataNode<String> c = new DataNode<>("C");
		r.addChild(a);
		r.addChild(b);
		r.addChild(c);

		// level 2
		a.addChild(new DataNode<>("A1"));
		a.addChild(new DataNode<>("A2"));

		DataNode<String> b1 = new DataNode<>("B1");
		DataNode<String> b2 = new DataNode<>("B2");
		b.addChild(b1);
		b.addChild(b2);

		c.addChild(new DataNode<>("C1"));

		// level 3
		b1.addChild(new DataNode<>("B1a"));
		b1.addChild(new DataNode<>("B1b"));

		b2.addChild(new DataNode<>("B2a"));

		return r;
	}

	// =========================================================
	// TESTS
	// =========================================================

	void testBasicCountsAndStructure() {
		println("\n--- testBasicCountsAndStructure ---");

		assertTrue(root != null, "root exists");
		assertEquals(root.getChildCount(), 3, "root has 3 direct children");

		int totalNodes = countNodesRecursive(root);
		int leafCountRecursive = countLeafsRecursive(root);
		int leafCountMethod = root.getLeafCount();

		println("total nodes recursive = " + totalNodes);
		println("leaf count recursive = " + leafCountRecursive);
		println("leaf count method    = " + leafCountMethod);

		assertEquals(totalNodes, 12, "initial total node count");
		assertEquals(leafCountMethod, leafCountRecursive, "initial leaf count matches recursive");
	}

	void testTraversalConsistency() {
		println("\n--- testTraversalConsistency ---");

		ArrayList<String> recursiveOrder = new ArrayList<>();
		forEachNodeRecursive(root, n -> recursiveOrder.add(n.toString()));

		ArrayList<String> iterableOrder = new ArrayList<>();
		for (DataNode<String> n : root) {
			iterableOrder.add(n.toString());
		}

		println("recursive order size = " + recursiveOrder.size());
		println("iterable order size  = " + iterableOrder.size());

		assertEquals(recursiveOrder.size(), iterableOrder.size(), "iterable traversal size matches recursive");

		// Optional: if your iterator is intended to be DFS in same order, keep this
		// enabled.
		// Otherwise comment this out.
		assertListEquals(recursiveOrder, iterableOrder, "iterable traversal order matches recursive DFS");
	}

	void testFindFirst() {
		println("\n--- testFindFirst ---");

		DataNode<String> foundA2 = root.findFirst(n -> "A2".equals(extractValue(n)));
		assertTrue(foundA2 != null, "findFirst locates A2");

		DataNode<String> foundB1b = root.findFirst(n -> "B1b".equals(extractValue(n)));
		assertTrue(foundB1b != null, "findFirst locates B1b");

		DataNode<String> missing = root.findFirst(n -> "ZZZ".equals(extractValue(n)));
		assertTrue(missing == null, "findFirst returns null for missing node");
	}

	void testAddChild() {
		println("\n--- testAddChild ---");

		DataNode<String> c = root.findFirst(n -> "C".equals(extractValue(n)));
		assertTrue(c != null, "found C before add");

		int beforeTotal = countNodesRecursive(root);
		int beforeLeafs = root.getLeafCount();

		c.addChild(new DataNode<>("C2"));

		int afterTotal = countNodesRecursive(root);
		int afterLeafs = root.getLeafCount();

		println("before total = " + beforeTotal + " | after total = " + afterTotal);
		println("before leafs = " + beforeLeafs + " | after leafs = " + afterLeafs);

		assertEquals(afterTotal, beforeTotal + 1, "adding one child increases total count by 1");
		assertTrue(root.findFirst(n -> "C2".equals(extractValue(n))) != null, "newly added C2 is findable");
		assertEquals(afterLeafs, countLeafsRecursive(root), "leaf count still matches recursive after add");
	}

	void testRemoveLeaf() {
		println("\n--- testRemoveLeaf ---");

		DataNode<String> a2 = root.findFirst(n -> "A2".equals(extractValue(n)));
		assertTrue(a2 != null, "found A2 before remove");

		int beforeTotal = countNodesRecursive(root);
		int beforeLeafs = root.getLeafCount();

		a2.remove();

		int afterTotal = countNodesRecursive(root);
		int afterLeafs = root.getLeafCount();

		assertEquals(afterTotal, beforeTotal - 1, "removing leaf decreases total count by 1");
		assertTrue(root.findFirst(n -> "A2".equals(extractValue(n))) == null, "A2 no longer exists");
		assertEquals(afterLeafs, countLeafsRecursive(root), "leaf count still matches recursive after leaf remove");

		println("before total = " + beforeTotal + " | after total = " + afterTotal);
		println("before leafs = " + beforeLeafs + " | after leafs = " + afterLeafs);
	}

	void testRemoveSubtree() {
		println("\n--- testRemoveSubtree ---");

		DataNode<String> b1 = root.findFirst(n -> "B1".equals(extractValue(n)));
		assertTrue(b1 != null, "found B1 before subtree remove");

		int subtreeNodeCt = countNodesRecursive(b1);
		int beforeTotal = countNodesRecursive(root);

		b1.remove();

		int afterTotal = countNodesRecursive(root);

		assertEquals(afterTotal, beforeTotal - subtreeNodeCt, "removing subtree decreases total by subtree size");
		assertTrue(root.findFirst(n -> "B1".equals(extractValue(n))) == null, "B1 removed");
		assertTrue(root.findFirst(n -> "B1a".equals(extractValue(n))) == null, "B1a removed");
		assertTrue(root.findFirst(n -> "B1b".equals(extractValue(n))) == null, "B1b removed");
		assertEquals(root.getLeafCount(), countLeafsRecursive(root),
				"leaf count still matches recursive after subtree remove");
	}

	void testLeafCountConsistency() {
		println("\n--- testLeafCountConsistency ---");

		int recursive = countLeafsRecursive(root);
		int method = root.getLeafCount();

		assertEquals(method, recursive, "root.getLeafCount matches recursive after edits");

		DataNode<String> b = root.findFirst(n -> "B".equals(extractValue(n)));
		assertTrue(b != null, "found B for subtree leaf count");

		int bRecursive = countLeafsRecursive(b);
		int bMethod = b.getLeafCount();

		println("B recursive leaf count = " + bRecursive);
		println("B method leaf count    = " + bMethod);

		assertEquals(bMethod, bRecursive, "subtree getLeafCount matches recursive");
	}

	void testCachedVsRecursiveLeafTraversal() {
		println("\n--- testCachedVsRecursiveLeafTraversal ---");

		ArrayList<String> recursiveLeafs = new ArrayList<>();
		forEachLeafRecursive(root, n -> recursiveLeafs.add(extractValue(n)));

		ArrayList<String> cachedLeafs = new ArrayList<>();
		root.forEachLeafCached(n -> cachedLeafs.add(extractValue(n)));

		Collections.sort(recursiveLeafs);
		Collections.sort(cachedLeafs);

		println("recursive leafs = " + recursiveLeafs);
		println("cached leafs    = " + cachedLeafs);

		assertListEquals(recursiveLeafs, cachedLeafs, "cached leaf traversal matches recursive leaf traversal");
	}

	void testParentDepthIndexConsistency() {
		println("\n--- testParentDepthIndexConsistency ---");

		final int[] count = { 0 };

		forEachNodeRecursive(root, n -> {
			count[0]++;

			// root checks
			if (n == root) {
				assertEquals(n.getParentIndex(), -1, "root parent index is -1");
				assertEquals(n.getDepth(), 0, "root depth is 0");
			}

			// non-root checks
			if (n != root) {
				assertTrue(n.getParentIndex() >= 0, "non-root parent index valid for " + extractValue(n));
				assertEquals(n.getDepth(), n.getParent().getDepth() + 1,
						"depth increments from parent for " + extractValue(n));
			}

			assertTrue(root.getCore().nodeList.get(n.getIndex()) == n,
				    "node reference matches nodeList slot for " + extractValue(n));
		});

		println("checked nodes = " + count[0]);
	}
	
	void testBFSTraversal() {
		println("\n--- testBFSTraversal ---");

		ArrayList<String> bfs = new ArrayList<>();

		Iterator<DataNode<String>> it = new TreeNodeObject.BFSTraversal<>(root);

		while (it.hasNext()) {
			bfs.add(it.next().getData());
		}

		println("BFS order = " + bfs);

		ArrayList<String> expected = new ArrayList<>(Arrays.asList(
			"root",
			"A","B","C",
			"A1","A2","B1","B2","C1",
			"B1a","B1b","B2a"
		));

		assertListEquals(bfs, expected, "BFS traversal order correct");
	}
	
	void testBFSAfterEdits() {
		println("\n--- testBFSAfterEdits ---");

		ArrayList<String> bfs = new ArrayList<>();

		Iterator<DataNode<String>> it = new TreeNodeObject.BFSTraversal<>(root);

		while (it.hasNext()) {
			bfs.add(it.next().getData());
		}

		println("BFS after edits = " + bfs);

		assertTrue(bfs.contains("C2"), "BFS contains newly added node C2");
		assertTrue(!bfs.contains("A2"), "BFS no longer contains removed node A2");
		assertTrue(!bfs.contains("B1"), "BFS no longer contains removed subtree");
	}

	// =========================================================
	// HELPERS
	// =========================================================

	void printTreeSummary(DataNode<String> r) {
		println("node count = " + countNodesRecursive(r));
		println("leaf count = " + countLeafsRecursive(r));

		ArrayList<String> names = new ArrayList<>();
		forEachNodeRecursive(r, n -> names.add(extractValue(n)));
		println("nodes = " + names);
	}

	int countNodesRecursive(DataNode<String> n) {
		final int[] count = { 0 };
		forEachNodeRecursive(n, x -> count[0]++);
		return count[0];
	}

	int countLeafsRecursive(DataNode<String> n) {
		final int[] count = { 0 };
		forEachLeafRecursive(n, x -> count[0]++);
		return count[0];
	}

	void forEachNodeRecursive(DataNode<String> n, java.util.function.Consumer<DataNode<String>> fn) {
		fn.accept(n);
		for (int i = 0; i < n.getChildCount(); i++) {
			forEachNodeRecursive(n.get(i), fn);
		}
	}

	void forEachLeafRecursive(DataNode<String> n, java.util.function.Consumer<DataNode<String>> fn) {
		if (n.getChildCount() == 0) {
			fn.accept(n);
			return;
		}
		for (int i = 0; i < n.getChildCount(); i++) {
			forEachLeafRecursive(n.get(i), fn);
		}
	}

	String extractValue(DataNode<String> n) {
		// Adjust this if your DataNode exposes its payload differently.
		// For example:
		// return n.data;
		 return n.getData();
		// return n.value;
//		return n.toString();
	}

	// =========================================================
	// ASSERT HELPERS
	// =========================================================

	void assertTrue(boolean cond, String label) {
		if (cond)
			println("[PASS] " + label);
		else
			println("[FAIL] " + label);
	}

	void assertEquals(int a, int b, String label) {
		if (a == b)
			println("[PASS] " + label + " -> " + a);
		else
			println("[FAIL] " + label + " -> got " + a + ", expected " + b);
	}

	void assertListEquals(List<String> a, List<String> b, String label) {
		if (a.equals(b))
			println("[PASS] " + label);
		else {
			println("[FAIL] " + label);
			println("  A = " + a);
			println("  B = " + b);
		}
	}
}