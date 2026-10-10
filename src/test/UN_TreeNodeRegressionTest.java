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
		
		
		// new method tests
		testGetSiblings();
		testGetNthAncestor();
		testIsAncestorDescendant();
		testGetPath();
		testForEachLeafWithIndex();
		testGetLeafIndex();
		testGetNodesAtRelativeDepth();
		testStream();
		testFindFirstLimited();
		testForEachNodeAtDepth();
		testPartition();
		testMapLeafs();
		testReduce();
		testReduceBottomUp();
		testDetach();
		testMoveTo();
		testSwapNodeData();
		testSwapSubtree();
		testMergeChildren();
		testFlatten();
		testMirrorSubtree();
		testZip();
		testInterleaveLeafs();
		testGetRandomPath();
		testDistributeToLeaves();

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
	// NEW METHOD TESTS — all use fresh trees
	// =========================================================

	void testGetSiblings() {
	    println("\n--- testGetSiblings ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> a = fresh.findFirst(n -> "A".equals(extractValue(n)));

	    List<DataNode<String>> siblings = a.getSiblings();
	    assertEquals(siblings.size(), 2, "A has 2 siblings");
	    assertTrue(siblings.stream().noneMatch(s -> s == a), "siblings do not include self");
	    assertTrue(siblings.stream().anyMatch(s -> "B".equals(extractValue(s))), "siblings contain B");
	    assertTrue(siblings.stream().anyMatch(s -> "C".equals(extractValue(s))), "siblings contain C");

	    // root has no siblings
	    assertTrue(fresh.getSiblings().isEmpty(), "root has no siblings");

	    // only child has no siblings
	    DataNode<String> c1 = fresh.findFirst(n -> "C1".equals(extractValue(n)));
	    assertTrue(c1.getSiblings().isEmpty(), "only child has no siblings");
	}

	void testGetNthAncestor() {
	    println("\n--- testGetNthAncestor ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> b1a = fresh.findFirst(n -> "B1a".equals(extractValue(n)));

	    assertEquals(extractValue(b1a.getNthAncestor(1)), "B1",   "1st ancestor of B1a is B1");
	    assertEquals(extractValue(b1a.getNthAncestor(2)), "B",    "2nd ancestor of B1a is B");
	    assertEquals(extractValue(b1a.getNthAncestor(3)), "root", "3rd ancestor of B1a is root");
	    // exceeding depth returns root
	    assertEquals(extractValue(b1a.getNthAncestor(99)), "root", "exceeding depth returns root");
	    // 0 returns self
	    assertEquals(extractValue(b1a.getNthAncestor(0)), "B1a",  "0th ancestor is self");
	}

	void testIsAncestorDescendant() {
	    println("\n--- testIsAncestorDescendant ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> b   = fresh.findFirst(n -> "B".equals(extractValue(n)));
	    DataNode<String> b1  = fresh.findFirst(n -> "B1".equals(extractValue(n)));
	    DataNode<String> b1a = fresh.findFirst(n -> "B1a".equals(extractValue(n)));
	    DataNode<String> c   = fresh.findFirst(n -> "C".equals(extractValue(n)));

	    assertTrue(b.isAncestorOf(b1a),    "B is ancestor of B1a");
	    assertTrue(b1.isAncestorOf(b1a),   "B1 is ancestor of B1a");
	    assertTrue(fresh.isAncestorOf(b1a),"root is ancestor of B1a");
	    assertTrue(!c.isAncestorOf(b1a),   "C is not ancestor of B1a");
	    assertTrue(!b1a.isAncestorOf(b),   "B1a is not ancestor of B");

	    assertTrue(b1a.isDescendantOf(b),    "B1a is descendant of B");
	    assertTrue(b1a.isDescendantOf(fresh),"B1a is descendant of root");
	    assertTrue(!b.isDescendantOf(b1a),   "B is not descendant of B1a");
	    assertTrue(!b1a.isDescendantOf(c),   "B1a is not descendant of C");
	}

	void testGetPath() {
	    println("\n--- testGetPath ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> b1a  = fresh.findFirst(n -> "B1a".equals(extractValue(n)));

	    List<DataNode<String>> path = b1a.getPath();
	    assertEquals(path.size(), 4, "path from root to B1a has 4 nodes");
	    assertEquals(extractValue(path.get(0)), "root", "path starts at root");
	    assertEquals(extractValue(path.get(1)), "B",    "path second node is B");
	    assertEquals(extractValue(path.get(2)), "B1",   "path third node is B1");
	    assertEquals(extractValue(path.get(3)), "B1a",  "path ends at B1a");

	    // root path is just root
	    List<DataNode<String>> rootPath = fresh.getPath();
	    assertEquals(rootPath.size(), 1, "root path has 1 node");
	    assertEquals(extractValue(rootPath.get(0)), "root", "root path contains root");
	}

	void testForEachLeafWithIndex() {
	    println("\n--- testForEachLeafWithIndex ---");

	    DataNode<String> fresh = buildInitialTree();
	    List<String> indexedLeafs = new ArrayList<>();

	    fresh.forEachLeafWithIndex((n, i) -> indexedLeafs.add(i + ":" + extractValue(n)));

	    assertEquals(indexedLeafs.size(), fresh.getLeafCount(), "forEachLeafWithIndex visits all leaves");
	    assertEquals(indexedLeafs.get(0), "0:A1",  "first leaf index 0 is A1");
	    assertEquals(indexedLeafs.get(5), "5:C1",  "last leaf index 5 is C1");
	    // verify indexes are sequential
	    for (int i = 0; i < indexedLeafs.size(); i++)
	        assertTrue(indexedLeafs.get(i).startsWith(i + ":"), "leaf index " + i + " is sequential");
	}

	void testGetLeafIndex() {
	    println("\n--- testGetLeafIndex ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> a1  = fresh.findFirst(n -> "A1".equals(extractValue(n)));
	    DataNode<String> c1  = fresh.findFirst(n -> "C1".equals(extractValue(n)));
	    DataNode<String> a   = fresh.findFirst(n -> "A".equals(extractValue(n)));

	    assertEquals(a1.getLeafIndex(), 0, "A1 leaf index is 0");
	    assertEquals(c1.getLeafIndex(), 5, "C1 leaf index is 5");
	    assertEquals(a.getLeafIndex(), -1, "non-leaf returns -1");
	}

	void testGetNodesAtRelativeDepth() {
	    println("\n--- testGetNodesAtRelativeDepth ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> b    = fresh.findFirst(n -> "B".equals(extractValue(n)));

	    List<DataNode<String>> relDepth0 = b.getNodesAtRelativeDepth(0);
	    assertEquals(relDepth0.size(), 1, "relative depth 0 returns self");
	    assertEquals(extractValue(relDepth0.get(0)), "B", "relative depth 0 is B");

	    List<DataNode<String>> relDepth1 = b.getNodesAtRelativeDepth(1);
	    assertEquals(relDepth1.size(), 2, "relative depth 1 from B has 2 nodes");

	    List<DataNode<String>> relDepth2 = b.getNodesAtRelativeDepth(2);
	    assertEquals(relDepth2.size(), 3, "relative depth 2 from B has 3 nodes");
	}

	void testStream() {
	    println("\n--- testStream ---");

	    DataNode<String> fresh = buildInitialTree();

	    // count via stream
	    long count = fresh.stream().count();
	    assertEquals((int)count, fresh.getTotalSize(), "stream visits all nodes");

	    // filter leaves via stream
	    long leafCount = fresh.stream().filter(n -> n.isLeaf()).count();
	    assertEquals((int)leafCount, fresh.getLeafCount(), "stream filter leaves matches getLeafCount");

	    // collect names via stream
	    List<String> names = fresh.stream()
	        .map(n -> extractValue(n))
	        .collect(java.util.stream.Collectors.toList());
	    assertEquals(names.get(0), "root", "stream first element is root");
	    assertEquals(names.size(), fresh.getTotalSize(), "stream collect size matches tree size");
	}

	void testFindFirstLimited() {
	    println("\n--- testFindFirstLimited ---");

	    DataNode<String> fresh = buildInitialTree();

	    List<DataNode<String>> results = fresh.findFirst(n -> extractValue(n).startsWith("B"), 3);
	    assertEquals(results.size(), 3, "findFirst with maxResults=3 returns 3 results");

	    // requesting more than exist returns all matches
	    List<DataNode<String>> all = fresh.findFirst(n -> extractValue(n).startsWith("B"), 99);
	    assertEquals(all.size(), fresh.findAll(n -> extractValue(n).startsWith("B")).size(),
	        "findFirst with large maxResults matches findAll");
	}

	void testForEachNodeAtDepth() {
	    println("\n--- testForEachNodeAtDepth ---");

	    DataNode<String> fresh = buildInitialTree();
	    List<String> depth1Nodes = new ArrayList<>();
	    List<String> depth2Nodes = new ArrayList<>();

	    fresh.forEachNodeAtDepth(1, n -> depth1Nodes.add(extractValue(n)));
	    fresh.forEachNodeAtDepth(2, n -> depth2Nodes.add(extractValue(n)));

	    assertEquals(depth1Nodes.size(), 3, "forEachNodeAtDepth(1) visits 3 nodes");
	    assertEquals(depth2Nodes.size(), 5, "forEachNodeAtDepth(2) visits 5 nodes");
	    assertTrue(depth1Nodes.contains("A"), "depth 1 contains A");
	    assertTrue(depth1Nodes.contains("B"), "depth 1 contains B");
	    assertTrue(depth1Nodes.contains("C"), "depth 1 contains C");
	}

	void testPartition() {
	    println("\n--- testPartition ---");

	    DataNode<String> fresh = buildInitialTree();
	    List<List<DataNode<String>>> parts = fresh.partition(
	        n -> extractValue(n).startsWith("B"));

	    assertEquals(parts.get(0).size(), 3, "3 leaves start with B: B1a, B1b, B2a");
	    assertEquals(parts.get(1).size(), 3, "3 leaves do not start with B: A1, A2, C1");
	    assertTrue(parts.get(0).stream().allMatch(n -> extractValue(n).startsWith("B")),
	        "matched partition all start with B");
	    assertTrue(parts.get(1).stream().noneMatch(n -> extractValue(n).startsWith("B")),
	        "unmatched partition none start with B");
	    assertEquals(parts.get(0).size() + parts.get(1).size(),
	        fresh.getLeafCount(), "partition covers all leaves");
	}

	void testMapLeafs() {
	    println("\n--- testMapLeafs ---");

	    DataNode<String> fresh = buildInitialTree();
	    List<Integer> lengths = fresh.mapLeafs(n -> extractValue(n).length());

	    assertEquals(lengths.size(), fresh.getLeafCount(), "mapLeafs returns one value per leaf");
	    assertEquals((int)lengths.get(0), 2, "A1 length is 2");
	    assertEquals((int)lengths.get(lengths.size()-1), 2, "C1 length is 2");
	}

	void testReduce() {
	    println("\n--- testReduce ---");

	    DataNode<String> fresh = buildInitialTree();

	    // count all nodes via reduce
	    int count = fresh.reduce(0, (acc, n) -> acc + 1);
	    assertEquals(count, fresh.getTotalSize(), "reduce count matches getTotalSize");

	    // sum all string lengths
	    int totalLength = fresh.reduce(0, (acc, n) -> acc + extractValue(n).length());
	    assertTrue(totalLength > 0, "reduce sum of lengths is positive");
	}

	void testReduceBottomUp() {
	    println("\n--- testReduceBottomUp ---");

	    DataNode<String> fresh = buildInitialTree();

	    // count leaves in each subtree bottom-up
	    int leafCount = fresh.reduceBottomUp(
	        n -> 1,
	        (n, childResults) -> childResults.stream().mapToInt(Integer::intValue).sum()
	    );
	    assertEquals(leafCount, fresh.getLeafCount(), "reduceBottomUp leaf count matches getLeafCount");

	    // max string length in subtree
	    int maxLength = fresh.reduceBottomUp(
	        n -> extractValue(n).length(),
	        (n, childResults) -> Math.max(
	            extractValue(n).length(),
	            childResults.stream().mapToInt(Integer::intValue).max().orElse(0))
	    );
	    assertTrue(maxLength > 0, "reduceBottomUp max length is positive");
	}

	void testDetach() {
	    println("\n--- testDetach ---");

	    DataNode<String> fresh = buildInitialTree();
	    int beforeTotal = countNodesRecursive(fresh);

	    DataNode<String> b1 = fresh.findFirst(n -> "B1".equals(extractValue(n)));
	    int subtreeSize = countNodesRecursive(b1);

	    DataNode<String> detached = b1.detach();

	    assertEquals(countNodesRecursive(fresh), beforeTotal - subtreeSize,
	        "tree shrinks by detached subtree size");
	    assertTrue(fresh.findFirst(n -> "B1".equals(extractValue(n))) == null,
	        "B1 no longer in original tree");
	    assertTrue(detached.isRoot(), "detached node is new root");
	    assertEquals(countNodesRecursive(detached), subtreeSize,
	        "detached subtree has correct size");
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after detach");
	}

	void testMoveTo() {
	    println("\n--- testMoveTo ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> a1 = fresh.findFirst(n -> "A1".equals(extractValue(n)));
	    DataNode<String> c  = fresh.findFirst(n -> "C".equals(extractValue(n)));
	    int beforeTotal = countNodesRecursive(fresh);
	    int beforeCChildren = c.getChildCount();

	    a1.moveTo(c);

	    assertEquals(countNodesRecursive(fresh), beforeTotal,
	        "total count unchanged after moveTo");
	    assertTrue(fresh.findFirst(n -> "A1".equals(extractValue(n))) != null,
	        "A1 still exists after moveTo");
	    assertEquals(c.getChildCount(), beforeCChildren + 1,
	        "C has one more child after moveTo");
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after moveTo");
	}

	void testSwapNodeData() {
	    println("\n--- testSwapNodeData ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> a1 = fresh.findFirst(n -> "A1".equals(extractValue(n)));
	    DataNode<String> c1 = fresh.findFirst(n -> "C1".equals(extractValue(n)));
	    int a1Index = a1.getIndex();
	    int c1Index = c1.getIndex();

	    a1.swapNodeData(c1);

	    // data swapped
	    assertEquals(extractValue(fresh.getCore().nodeList.get(a1Index)), "C1",
	        "A1 position now has C1 data");
	    assertEquals(extractValue(fresh.getCore().nodeList.get(c1Index)), "A1",
	        "C1 position now has A1 data");
	    // structure unchanged
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after swapNodeData");
	}

	void testSwapSubtree() {
	    println("\n--- testSwapSubtree ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> a = fresh.findFirst(n -> "A".equals(extractValue(n)));
	    DataNode<String> c = fresh.findFirst(n -> "C".equals(extractValue(n)));
	    int aChildren = a.getChildCount();
	    int cChildren = c.getChildCount();
	    int totalBefore = countNodesRecursive(fresh);

	    a.swapSubtree(c);

	    assertEquals(countNodesRecursive(fresh), totalBefore,
	        "total count unchanged after swapSubtree");
	    // A and C swapped positions
	    DataNode<String> aAfter = fresh.findFirst(n -> "A".equals(extractValue(n)));
	    DataNode<String> cAfter = fresh.findFirst(n -> "C".equals(extractValue(n)));
	    assertEquals(aAfter.getChildCount(), aChildren,
	        "A subtree intact after swap");
	    assertEquals(cAfter.getChildCount(), cChildren,
	        "C subtree intact after swap");
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after swapSubtree");
	}

	void testMergeChildren() {
	    println("\n--- testMergeChildren ---");

	    DataNode<String> fresh = buildInitialTree();
	    DataNode<String> b  = fresh.findFirst(n -> "B".equals(extractValue(n)));
	    int bChildCount     = b.getChildCount();
	    DataNode<String> bParent = b.getParent();
	    int parentChildCountBefore = bParent.getChildCount();
	    int totalBefore = countNodesRecursive(fresh);

	    b.mergeChildren();

	    // B is gone, its children now belong to root
	    assertTrue(fresh.findFirst(n -> "B".equals(extractValue(n))) == null,
	        "B removed after mergeChildren");
	    assertEquals(bParent.getChildCount(), parentChildCountBefore - 1 + bChildCount,
	        "parent gains B's children minus B itself");
	    assertEquals(countNodesRecursive(fresh), totalBefore - 1,
	        "total count decreases by 1 (only B removed)");
	    assertTrue(fresh.findFirst(n -> "B1".equals(extractValue(n))) != null,
	        "B1 still exists after mergeChildren");
	    assertTrue(fresh.findFirst(n -> "B2".equals(extractValue(n))) != null,
	        "B2 still exists after mergeChildren");
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after mergeChildren");
	}

	void testFlatten() {
	    println("\n--- testFlatten ---");

	    DataNode<String> fresh = buildInitialTree();
	    int leafCount = fresh.getLeafCount();

	    fresh.flatten();

	    assertEquals(fresh.getChildCount(), leafCount,
	        "after flatten root has same number of children as original leaf count");
	    assertTrue(fresh.containsOnlyLeafs(),
	        "after flatten all children are leaves");
	    assertEquals(fresh.getLeafCount(), leafCount,
	        "leaf count unchanged after flatten");
	    assertEquals(countNodesRecursive(fresh), leafCount + 1,
	        "total nodes = leaves + root after flatten");
	}

	void testMirrorSubtree() {
	    println("\n--- testMirrorSubtree ---");

	    DataNode<String> fresh = buildInitialTree();
	    int totalBefore = countNodesRecursive(fresh);

	    fresh.mirrorSubtree();

	    // structure size unchanged
	    assertEquals(countNodesRecursive(fresh), totalBefore,
	        "total count unchanged after mirrorSubtree");
	    // root children reversed: was A,B,C now C,B,A
	    assertEquals(extractValue(fresh.get(0)), "C", "after mirror first child is C");
	    assertEquals(extractValue(fresh.get(1)), "B", "after mirror second child is B");
	    assertEquals(extractValue(fresh.get(2)), "A", "after mirror third child is A");
	    assertEquals(fresh.getLeafCount(), countLeafsRecursive(fresh),
	        "leaf count consistent after mirrorSubtree");
	    // double mirror returns to original
	    fresh.mirrorSubtree();
	    assertEquals(extractValue(fresh.get(0)), "A", "double mirror restores A first");
	    assertEquals(extractValue(fresh.get(2)), "C", "double mirror restores C last");
	}

	void testZip() {
	    println("\n--- testZip ---");

	    DataNode<String> treeA = buildInitialTree();
	    DataNode<String> treeB = buildInitialTree();

	    List<String> pairs = new ArrayList<>();
	    treeA.zip(treeB, (a, b) -> pairs.add(extractValue(a) + "+" + extractValue(b)));

	    assertEquals(pairs.size(), treeA.getTotalSize(),
	        "zip visits all node pairs");
	    assertEquals(pairs.get(0), "root+root",
	        "first pair is root+root");
	    // all pairs should match since trees are identical
	    assertTrue(pairs.stream().allMatch(p -> {
	        String[] parts = p.split("\\+");
	        return parts[0].equals(parts[1]);
	    }), "all paired nodes have matching data in identical trees");
	}

	void testInterleaveLeafs() {
	    println("\n--- testInterleaveLeafs ---");

	    DataNode<String> treeA = buildInitialTree();
	    DataNode<String> treeB = buildInitialTree();

	    List<Object> interleaved = treeA.interleaveLeafs(treeB);

	    assertEquals(interleaved.size(), treeA.getLeafCount() + treeB.getLeafCount(),
	        "interleaved list contains all leaves from both trees");
	    // verify alternating pattern — cast and check
	    for (int i = 0; i < interleaved.size() - 1; i += 2) {
	        DataNode<String> a = (DataNode<String>) interleaved.get(i);
	        DataNode<String> b = (DataNode<String>) interleaved.get(i + 1);
	        assertEquals(extractValue(a), extractValue(b),
	            "interleaved leaves at positions " + i + " and " + (i+1) + " match");
	    }
	}

	void testGetRandomPath() {
	    println("\n--- testGetRandomPath ---");

	    DataNode<String> fresh = buildInitialTree();

	    // run several times to account for randomness
	    for (int i = 0; i < 10; i++) {
	        List<DataNode<String>> path = fresh.getRandomPath();
	        assertTrue(path.size() > 0,          "random path is non-empty");
	        assertTrue(path.get(0) == fresh,      "random path starts at root");
	        assertTrue(path.get(path.size()-1).isLeaf(), "random path ends at leaf");
	        // verify path is connected
	        for (int j = 1; j < path.size(); j++)
	            assertTrue(path.get(j).getParent() == path.get(j-1),
	                "random path nodes are connected");
	    }
	}

	void testDistributeToLeaves() {
	    println("\n--- testDistributeToLeaves ---");

	    DataNode<String> fresh = buildInitialTree();
	    List<String> values = Arrays.asList("v0","v1","v2","v3","v4","v5");

	    // store distributed values in a separate list to verify
	    List<String> received = new ArrayList<>();
	    fresh.distributeToLeaves(values, (n, v) -> received.add(extractValue(n) + "=" + v));

	    assertEquals(received.size(), fresh.getLeafCount(),
	        "distributeToLeaves called once per leaf");
	    assertEquals(received.get(0), "A1=v0", "first leaf A1 gets v0");
	    assertEquals(received.get(5), "C1=v5", "last leaf C1 gets v5");

	    // wrong size throws
	    boolean threw = false;
	    try {
	        fresh.distributeToLeaves(Arrays.asList("a","b"), (n, v) -> {});
	    } catch (UnsupportedOperationException e) {
	        threw = true;
	    }
	    assertTrue(threw, "distributeToLeaves throws on size mismatch");
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
	
	void assertEquals(long a, int b, String label) {
	    if (a == b) println("[PASS] " + label + " -> " + a);
	    else        println("[FAIL] " + label + " -> got " + a + ", expected " + b);
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