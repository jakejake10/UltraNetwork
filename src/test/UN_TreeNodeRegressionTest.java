package test;

import processing.core.*;
import unCore.*;
import utTypes.*;

import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/*
 * Regression test for TreeNodeObject and sub-interfaces.
 *
 * Test groups:
 *   - Stateful tests (mutate shared root in sequence):
 *       testBasicCountsAndStructure, testTraversalConsistency, testFindFirst,
 *       testBFSTraversal, testAddChild, testRemoveLeaf, testRemoveSubtree,
 *       testLeafCountConsistency, testCachedVsRecursiveLeafTraversal,
 *       testParentDepthIndexConsistency, testBFSAfterEdits
 *
 *   - Independent tests (each builds its own fresh tree):
 *       testCopyNode, testCopyNodeSubtree, testConvertNodeSubtree,
 *       testSortChildren, testSortChildrenReverse,
 *       testReplaceNodeSubtree, testInsertParent,
 *       testFindAll, testLocationCode
 */
public class UN_TreeNodeRegressionTest extends PApplet {

	// shared root — intentionally mutated by stateful tests in sequence
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

		// --- stateful: run in order, each depends on prior state ---
		testBasicCountsAndStructure();
		testTraversalConsistency();
		testFindFirst();
		testBFSTraversal();
		testAddChild();           // adds C2
		testRemoveLeaf();         // removes A2
		testRemoveSubtree();      // removes B1, B1a, B1b
		testLeafCountConsistency();
		testCachedVsRecursiveLeafTraversal();
		testParentDepthIndexConsistency();
		testBFSAfterEdits();      // verifies state from add/remove tests

		// --- independent: each builds its own tree ---
		testCopyNode();
		testCopyNodeSubtree();
		testConvertNodeSubtree();
		testSortChildren();
		testSortChildrenReverse();
		testReplaceNodeSubtree();
		testInsertParent();
		testFindAll();
		testLocationCode();
		
		testForEachNodeBottomUp();
		testForEachNode();
		testReverseChildren();
		testShuffleChildren();
		testSortAll();

		println("\n==================================================");
		println("ALL TESTS COMPLETE");
		println("==================================================");
	}

	// =========================================================
	// TREE BUILD
	// =========================================================

	/*
	 * Builds this tree:
	 *
	 *           root
	 *         /  |  \
	 *        A   B   C
	 *       / \ / \   \
	 *      A1 A2 B1 B2  C1
	 *          / \  \
	 *        B1a B1b B2a
	 *
	 * 12 nodes total, 6 leaves: A1, A2, B1a, B1b, B2a, C1
	 */
	DataNode<String> buildInitialTree() {
		DataNode<String> r  = new DataNode<>("root");

		DataNode<String> a  = new DataNode<>("A");
		DataNode<String> b  = new DataNode<>("B");
		DataNode<String> c  = new DataNode<>("C");
		r.addChild(a);
		r.addChild(b);
		r.addChild(c);

		a.addChild(new DataNode<>("A1"));
		a.addChild(new DataNode<>("A2"));

		DataNode<String> b1 = new DataNode<>("B1");
		DataNode<String> b2 = new DataNode<>("B2");
		b.addChild(b1);
		b.addChild(b2);

		c.addChild(new DataNode<>("C1"));

		b1.addChild(new DataNode<>("B1a"));
		b1.addChild(new DataNode<>("B1b"));
		b2.addChild(new DataNode<>("B2a"));

		return r;
	}

	// =========================================================
	// STATEFUL TESTS
	// =========================================================

	void testBasicCountsAndStructure() {
		println("\n--- testBasicCountsAndStructure ---");

		assertTrue(root != null, "root exists");
		assertEquals(root.getChildCount(), 3, "root has 3 direct children");

		int totalNodes        = countNodesRecursive(root);
		int leafCountRecursive = countLeafsRecursive(root);
		int leafCountMethod    = root.getLeafCount();

		println("total nodes recursive = " + totalNodes);
		println("leaf count recursive  = " + leafCountRecursive);
		println("leaf count method     = " + leafCountMethod);

		assertEquals(totalNodes, 12, "initial total node count");
		assertEquals(leafCountMethod, leafCountRecursive, "initial leaf count matches recursive");
	}

	void testTraversalConsistency() {
		println("\n--- testTraversalConsistency ---");

		ArrayList<String> recursiveOrder = new ArrayList<>();
		forEachNodeRecursive(root, n -> recursiveOrder.add(n.toString()));

		ArrayList<String> iterableOrder = new ArrayList<>();
		for (DataNode<String> n : root)
			iterableOrder.add(n.toString());

		println("recursive order size = " + recursiveOrder.size());
		println("iterable order size  = " + iterableOrder.size());

		assertEquals(recursiveOrder.size(), iterableOrder.size(), "iterable traversal size matches recursive");
		assertListEquals(recursiveOrder, iterableOrder, "iterable traversal order matches recursive DFS");
	}

	void testFindFirst() {
		println("\n--- testFindFirst ---");

		assertTrue(root.findFirst(n -> "A2".equals(extractValue(n)))  != null, "findFirst locates A2");
		assertTrue(root.findFirst(n -> "B1b".equals(extractValue(n))) != null, "findFirst locates B1b");
		assertTrue(root.findFirst(n -> "ZZZ".equals(extractValue(n))) == null, "findFirst returns null for missing node");
	}

	void testBFSTraversal() {
		println("\n--- testBFSTraversal ---");

		ArrayList<String> bfs = collectBFS(root);
		println("BFS order = " + bfs);

		ArrayList<String> expected = new ArrayList<>(Arrays.asList(
			"root",
			"A", "B", "C",
			"A1", "A2", "B1", "B2", "C1",
			"B1a", "B1b", "B2a"
		));

		assertListEquals(bfs, expected, "BFS traversal order correct");
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

		println("before total = " + beforeTotal + " | after total = " + afterTotal);
		println("before leafs = " + beforeLeafs + " | after leafs = " + afterLeafs);

		assertEquals(afterTotal, beforeTotal - 1, "removing leaf decreases total count by 1");
		assertTrue(root.findFirst(n -> "A2".equals(extractValue(n))) == null, "A2 no longer exists");
		assertEquals(afterLeafs, countLeafsRecursive(root), "leaf count still matches recursive after leaf remove");
	}

	void testRemoveSubtree() {
		println("\n--- testRemoveSubtree ---");

		DataNode<String> b1 = root.findFirst(n -> "B1".equals(extractValue(n)));
		assertTrue(b1 != null, "found B1 before subtree remove");

		int subtreeNodeCt = countNodesRecursive(b1);
		int beforeTotal   = countNodesRecursive(root);

		b1.remove();

		int afterTotal = countNodesRecursive(root);

		assertEquals(afterTotal, beforeTotal - subtreeNodeCt, "removing subtree decreases total by subtree size");
		assertTrue(root.findFirst(n -> "B1".equals(extractValue(n)))  == null, "B1 removed");
		assertTrue(root.findFirst(n -> "B1a".equals(extractValue(n))) == null, "B1a removed");
		assertTrue(root.findFirst(n -> "B1b".equals(extractValue(n))) == null, "B1b removed");
		assertEquals(root.getLeafCount(), countLeafsRecursive(root), "leaf count still matches recursive after subtree remove");
	}

	void testLeafCountConsistency() {
		println("\n--- testLeafCountConsistency ---");

		assertEquals(root.getLeafCount(), countLeafsRecursive(root), "root.getLeafCount matches recursive after edits");

		DataNode<String> b = root.findFirst(n -> "B".equals(extractValue(n)));
		assertTrue(b != null, "found B for subtree leaf count");

		int bRecursive = countLeafsRecursive(b);
		int bMethod    = b.getLeafCount();

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

			if (n == root) {
				assertEquals(n.getParentIndex(), -1, "root parent index is -1");
				assertEquals(n.getDepth(), 0, "root depth is 0");
			} else {
				assertTrue(n.getParentIndex() >= 0, "non-root parent index valid for " + extractValue(n));
				assertEquals(n.getDepth(), n.getParent().getDepth() + 1,
					"depth increments from parent for " + extractValue(n));
			}

			assertTrue(root.getCore().nodeList.get(n.getIndex()) == n,
				"node reference matches nodeList slot for " + extractValue(n));
		});

		println("checked nodes = " + count[0]);
	}

	void testBFSAfterEdits() {
		println("\n--- testBFSAfterEdits ---");

		// tree state at this point: A2 removed, B1 subtree removed, C2 added
		ArrayList<String> bfs = collectBFS(root);
		println("BFS after edits = " + bfs);

		assertTrue( bfs.contains("C2"),  "BFS contains newly added node C2");
		assertTrue(!bfs.contains("A2"),  "BFS no longer contains removed node A2");
		assertTrue(!bfs.contains("B1"),  "BFS no longer contains removed subtree B1");
		assertTrue(!bfs.contains("B1a"), "BFS no longer contains removed subtree B1a");
	}

	// =========================================================
	// INDEPENDENT TESTS — each builds its own tree
	// =========================================================

	void testCopyNode() {
		println("\n--- testCopyNode ---");

		DataNode<String> fresh = buildInitialTree();
		DataNode<String> a    = fresh.findFirst(n -> "A".equals(extractValue(n)));
		DataNode<String> copy = a.copyNode(false);

		assertTrue(copy != null,                          "copyNode returns non-null");
		assertTrue(copy != a,                             "copyNode returns new instance");
		assertEquals(copy.getChildCount(), 0,             "copyNode(false) has no children");
		assertTrue("A".equals(extractValue(copy)),        "copyNode transfers subclass fields");
	}

	void testCopyNodeSubtree() {
		println("\n--- testCopyNodeSubtree ---");

		DataNode<String> fresh = buildInitialTree();
		DataNode<String> b     = fresh.findFirst(n -> "B".equals(extractValue(n)));
		int originalSize       = countNodesRecursive(b);

		DataNode<String> copy = b.copyNodeSubtree();

		assertEquals(countNodesRecursive(copy), originalSize,    "subtree copy has same node count");
		assertTrue(copy != b,                                     "subtree copy is new instance");
		assertTrue(copy.isRoot(),                                 "subtree copy is its own root");
		assertTrue(fresh.findFirst(n -> "B".equals(extractValue(n)))   != null, "original B still exists");
		assertTrue(copy.findFirst(n -> "B1a".equals(extractValue(n)))  != null, "deep nodes copied");

		// modifying copy must not affect original
		copy.findFirst(n -> "B1a".equals(extractValue(n))).remove();
		assertTrue(fresh.findFirst(n -> "B1a".equals(extractValue(n))) != null,
			"original unaffected by copy modification");
	}

	void testConvertNodeSubtree() {
		println("\n--- testConvertNodeSubtree ---");

		DataNode<String>  fresh     = buildInitialTree();
		DataNode<Integer> converted = fresh.convertNodeSubtree(n -> new DataNode<>(extractValue(n).length()));

		assertEquals(converted.getTotalSize(), fresh.getTotalSize(), "converted tree has same node count");
		assertEquals(converted.getData(), 4,                         "root node data converted correctly (\"root\".length == 4)");
		assertEquals(converted.getChildCount(), fresh.getChildCount(),"converted root has same child count");
	}

	void testSortChildren() {
		println("\n--- testSortChildren ---");

		DataNode<String> sortRoot = new DataNode<>("root");
		sortRoot.addChild(new DataNode<>("C"));
		sortRoot.addChild(new DataNode<>("A"));
		sortRoot.addChild(new DataNode<>("B"));

		sortRoot.sortChildren(n -> extractValue(n));

		assertEquals(extractValue(sortRoot.get(0)), "A", "sort: first child is A");
		assertEquals(extractValue(sortRoot.get(1)), "B", "sort: second child is B");
		assertEquals(extractValue(sortRoot.get(2)), "C", "sort: third child is C");

		forEachNodeRecursive(sortRoot, n -> {
			assertTrue(sortRoot.getCore().nodeList.get(n.getIndex()) == n,
				"index consistent after sort for " + extractValue(n));
		});
	}

	void testSortChildrenReverse() {
		println("\n--- testSortChildrenReverse ---");

		DataNode<String> sortRoot = new DataNode<>("root");
		sortRoot.addChild(new DataNode<>("A"));
		sortRoot.addChild(new DataNode<>("B"));
		sortRoot.addChild(new DataNode<>("C"));

		sortRoot.sortChildrenReverse(n -> extractValue(n));

		assertEquals(extractValue(sortRoot.get(0)), "C", "reverse sort: first child is C");
		assertEquals(extractValue(sortRoot.get(2)), "A", "reverse sort: last child is A");
	}

	void testReplaceNodeSubtree() {
		println("\n--- testReplaceNodeSubtree ---");

		DataNode<String> fresh      = buildInitialTree();
		DataNode<String> c1         = fresh.findFirst(n -> "C1".equals(extractValue(n)));
		assertTrue(c1 != null, "found C1 before replace");

		int beforeTotal = countNodesRecursive(fresh);

		c1.replaceNodeSubtree(new DataNode<>("C1_replaced"));

		assertTrue(fresh.findFirst(n -> "C1".equals(extractValue(n)))          == null, "C1 gone after replace");
		assertTrue(fresh.findFirst(n -> "C1_replaced".equals(extractValue(n))) != null, "replacement exists");
		assertEquals(countNodesRecursive(fresh), beforeTotal,   "total count unchanged after leaf replace");
		assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh), "leaf count consistent after replace");
	}

	void testInsertParent() {
		println("\n--- testInsertParent ---");

		DataNode<String> fresh = buildInitialTree();
		DataNode<String> a1    = fresh.findFirst(n -> "A1".equals(extractValue(n)));
		assertTrue(a1 != null, "found A1 before insertParent");

		int beforeTotal = countNodesRecursive(fresh);

		a1.insertParent();

		assertEquals(countNodesRecursive(fresh), beforeTotal + 1, "insertParent increases count by 1");

		DataNode<String> a1After = fresh.findFirst(n -> "A1".equals(extractValue(n)));
		assertTrue(a1After != null,             "A1 still exists after insertParent");
		assertEquals(a1After.getDepth(), 3,     "A1 depth increased by 1 after insertParent");
		assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh), "leaf count consistent after insertParent");
	}

	void testFindAll() {
		println("\n--- testFindAll ---");

		DataNode<String> fresh = buildInitialTree();

		// B, B1, B1a, B1b, B2, B2a = 6 nodes starting with B
		List<DataNode<String>> bNodes = fresh.findAll(n -> extractValue(n).startsWith("B"));
		assertEquals(bNodes.size(), 6, "6 nodes starting with B");

		List<DataNode<String>> depth1 = fresh.getNodesAtDepth(1);
		assertEquals(depth1.size(), 3, "3 nodes at depth 1");

		List<DataNode<String>> depth2 = fresh.getNodesAtDepth(2);
		assertEquals(depth2.size(), 5, "5 nodes at depth 2");

		List<DataNode<String>> leafNodes = fresh.findAll(n -> n.isLeaf());
		assertEquals(leafNodes.size(), fresh.getLeafCount(), "findAll leaves matches getLeafCount");
	}

	void testLocationCode() {
		println("\n--- testLocationCode ---");

		DataNode<String> fresh = buildInitialTree();
		DataNode<String> b1a  = fresh.findFirst(n -> "B1a".equals(extractValue(n)));
		assertTrue(b1a != null, "found B1a for location code test");

		// path: root -> B (index 1) -> B1 (index 0) -> B1a (index 0)
		List<Integer> code = b1a.getLocationCodeFromRoot();
		assertEquals(code.size(),     3, "location code length matches depth");
		assertEquals((int)code.get(0), 1, "first step: child index 1 (B)");
		assertEquals((int)code.get(1), 0, "second step: child index 0 (B1)");
		assertEquals((int)code.get(2), 0, "third step: child index 0 (B1a)");

		// round-trip
		assertTrue(fresh.getFromLocationCode(code) == b1a, "getFromLocationCode round-trips correctly");

		// getLocationCodeFromNode: path relative to B
		DataNode<String> b = fresh.findFirst(n -> "B".equals(extractValue(n)));
		List<Integer> relCode = b1a.getLocationCodeFromNode(b);
		assertEquals(relCode.size(), 2, "relative code length from B to B1a is 2");
		assertTrue(b.getFromLocationCode(relCode) == b1a, "relative location code round-trips correctly");
	}
	
	
	void testSortAll() {
	    println("\n--- testSortAll ---");

	    // build a tree with unsorted children at multiple levels
	    DataNode<String> fresh = new DataNode<>("root");
	    DataNode<String> b = new DataNode<>("B");
	    DataNode<String> a = new DataNode<>("A");
	    DataNode<String> c = new DataNode<>("C");
	    fresh.addChild(b);
	    fresh.addChild(a);
	    fresh.addChild(c);
	    b.addChild(new DataNode<>("B2"));
	    b.addChild(new DataNode<>("B1"));
	    b.addChild(new DataNode<>("B3"));

	    fresh.sortAll(n -> extractValue(n));

	    // root level sorted
	    assertEquals(extractValue(fresh.get(0)), "A", "sortAll: root level first child is A");
	    assertEquals(extractValue(fresh.get(1)), "B", "sortAll: root level second child is B");
	    assertEquals(extractValue(fresh.get(2)), "C", "sortAll: root level third child is C");

	    // B's children sorted
	    DataNode<String> bAfter = fresh.findFirst(n -> "B".equals(extractValue(n)));
	    assertEquals(extractValue(bAfter.get(0)), "B1", "sortAll: B first child is B1");
	    assertEquals(extractValue(bAfter.get(1)), "B2", "sortAll: B second child is B2");
	    assertEquals(extractValue(bAfter.get(2)), "B3", "sortAll: B third child is B3");

	    // index consistency after sortAll
	    forEachNodeRecursive(fresh, n ->
	        assertTrue(fresh.getCore().nodeList.get(n.getIndex()) == n,
	            "index consistent after sortAll for " + extractValue(n)));
	}

	
	void testShuffleChildren() {
	    println("\n--- testShuffleChildren ---");

	    DataNode<String> fresh = new DataNode<>("root");
	    fresh.addChild(new DataNode<>("A"));
	    fresh.addChild(new DataNode<>("B"));
	    fresh.addChild(new DataNode<>("C"));

	    fresh.shuffleChildren();

	    // can't assert order, but structure must remain intact
	    assertEquals(fresh.getChildCount(), 3, "shuffleChildren: child count unchanged");
	    assertTrue(fresh.findFirst(n -> "A".equals(extractValue(n))) != null, "A still exists after shuffle");
	    assertTrue(fresh.findFirst(n -> "B".equals(extractValue(n))) != null, "B still exists after shuffle");
	    assertTrue(fresh.findFirst(n -> "C".equals(extractValue(n))) != null, "C still exists after shuffle");

	    forEachNodeRecursive(fresh, n ->
	        assertTrue(fresh.getCore().nodeList.get(n.getIndex()) == n,
	            "index consistent after shuffle for " + extractValue(n)));
	}
	
	void testReverseChildren() {
	    println("\n--- testReverseChildren ---");

	    DataNode<String> fresh = new DataNode<>("root");
	    fresh.addChild(new DataNode<>("A"));
	    fresh.addChild(new DataNode<>("B"));
	    fresh.addChild(new DataNode<>("C"));

	    fresh.reverseChildren();

	    assertEquals(extractValue(fresh.get(0)), "C", "reverseChildren: first child is C");
	    assertEquals(extractValue(fresh.get(1)), "B", "reverseChildren: second child is B");
	    assertEquals(extractValue(fresh.get(2)), "A", "reverseChildren: last child is A");

	    forEachNodeRecursive(fresh, n ->
	        assertTrue(fresh.getCore().nodeList.get(n.getIndex()) == n,
	            "index consistent after reverse for " + extractValue(n)));
	}
	
	void testForEachNode() {
	    println("\n--- testForEachNode ---");

	    DataNode<String> fresh = buildInitialTree();
	    ArrayList<String> visited = new ArrayList<>();

	    fresh.forEachNode(n -> visited.add(extractValue(n)));

	    // should visit all nodes in DFS order
	    assertEquals(visited.size(), fresh.getTotalSize(), "forEachNode visits all nodes");
	    assertEquals(visited.get(0), "root", "forEachNode starts at root");

	    // verify DFS order matches iterator order
	    ArrayList<String> iterOrder = new ArrayList<>();
	    for (DataNode<String> n : fresh) iterOrder.add(extractValue(n));
	    assertListEquals(visited, iterOrder, "forEachNode order matches DFS iterator");
	}

	void testForEachNodeBottomUp() {
	    println("\n--- testForEachNodeBottomUp ---");

	    DataNode<String> fresh = buildInitialTree();
	    ArrayList<String> visited = new ArrayList<>();

	    fresh.forEachNodeBottomUp(n -> visited.add(extractValue(n)));

	    // root must be last
	    assertEquals(visited.get(visited.size() - 1), "root", "forEachNodeBottomUp: root is last");
	    assertEquals(visited.size(), fresh.getTotalSize(), "forEachNodeBottomUp visits all nodes");

	    // every parent must appear after all its children
	    forEachNodeRecursive(fresh, n -> {
	        if (n.hasChildren()) {
	            int parentPos = visited.indexOf(extractValue(n));
	            for (int i = 0; i < n.getChildCount(); i++) {
	                int childPos = visited.indexOf(extractValue(n.get(i)));
	                assertTrue(childPos < parentPos,
	                    "forEachNodeBottomUp: " + extractValue(n.get(i)) + " appears before parent " + extractValue(n));
	            }
	        }
	    });
	}
	
	
	
	
	
	

	// =========================================================
	// HELPERS
	// =========================================================

	ArrayList<String> collectBFS(DataNode<String> start) {
		ArrayList<String> out = new ArrayList<>();
		Iterator<DataNode<String>> it = new TreeNodeObject.BFSTraversal<>(start);
		while (it.hasNext()) out.add(it.next().getData());
		return out;
	}

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
		for (int i = 0; i < n.getChildCount(); i++)
			forEachNodeRecursive(n.get(i), fn);
	}

	void forEachLeafRecursive(DataNode<String> n, java.util.function.Consumer<DataNode<String>> fn) {
		if (n.getChildCount() == 0) { fn.accept(n); return; }
		for (int i = 0; i < n.getChildCount(); i++)
			forEachLeafRecursive(n.get(i), fn);
	}

	String extractValue(DataNode<String> n) {
		return n.getData();
	}

	// =========================================================
	// ASSERT HELPERS
	// =========================================================

	void assertTrue(boolean cond, String label) {
		println(cond ? "[PASS] " + label : "[FAIL] " + label);
	}

	void assertEquals(int a, int b, String label) {
		if (a == b) println("[PASS] " + label + " -> " + a);
		else        println("[FAIL] " + label + " -> got " + a + ", expected " + b);
	}

	void assertEquals(String a, String b, String label) {
		if (a != null && a.equals(b)) println("[PASS] " + label + " -> " + a);
		else                          println("[FAIL] " + label + " -> got " + a + ", expected " + b);
	}

	void assertListEquals(List<String> a, List<String> b, String label) {
		if (a.equals(b)) println("[PASS] " + label);
		else {
			println("[FAIL] " + label);
			println("  expected = " + b);
			println("  got      = " + a);
		}
	}
}