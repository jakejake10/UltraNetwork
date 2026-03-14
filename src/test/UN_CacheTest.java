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

public class UN_CacheTest extends PApplet {

	// Replace DataNode with your actual concrete node type
	DataNode<String> root;
	DataNode<String> subtree;

	public static void main(String... args) {
		UN_CacheTest pt = new UN_CacheTest();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		surface.setTitle("Tree Cache Test");

		root = buildTestTree();
		subtree = pickTestSubtree(root);

		println("total tree size = " + root.getTotalSize());
		println("root leaf count = " + root.getLeafCount());
		println("subtree leaf count = " + subtree.getLeafCount());

		// build cache once
		root.rebuildLeafCache();

		int reps = 2000;

		println("\n=== ROOT TESTS ===");
		benchGetLeafsSize(root, reps);
		benchForEachLeafRecursive(root, reps);
		benchForEachLeafCached(root, reps);
		benchGetLeafCount(root, reps);

		println("\n=== SUBTREE TESTS ===");
		benchGetLeafsSize(subtree, reps);
		benchForEachLeafRecursive(subtree, reps);
		benchForEachLeafCached(subtree, reps);
		benchGetLeafCount(subtree, reps);

		noLoop();
	}

	@Override
	public void draw() {
		background(30);
		fill(255);
		text("See console for benchmark output", 20, 30);
	}

	// ---------------------------
	// benchmarks
	// ---------------------------

	void benchGetLeafsSize(DataNode<String> node, int reps) {
		long t0 = System.nanoTime();
		int sum = 0;
		for (int i = 0; i < reps; i++) {
			sum += node.getLeafs().size();
		}
		long t1 = System.nanoTime();
		printResult("getLeafs().size()", t1 - t0, reps, sum);
	}

	void benchForEachLeafRecursive(DataNode<String> node, int reps) {
		long t0 = System.nanoTime();
		int sum = 0;
		for (int i = 0; i < reps; i++) {
			final int[] count = { 0 };
			node.forEachLeafRecursive(n -> count[0]++);
			sum += count[0];
		}
		long t1 = System.nanoTime();
		printResult("forEachLeafRecursive()", t1 - t0, reps, sum);
	}

	void benchForEachLeafCached(DataNode<String> node, int reps) {
		long t0 = System.nanoTime();
		int sum = 0;
		for (int i = 0; i < reps; i++) {
			final int[] count = { 0 };
			node.forEachLeafCached(n -> count[0]++);
			sum += count[0];
		}
		long t1 = System.nanoTime();
		printResult("forEachLeafCached()", t1 - t0, reps, sum);
	}

	void benchGetLeafCount(DataNode<String> node, int reps) {
		long t0 = System.nanoTime();
		int sum = 0;
		for (int i = 0; i < reps; i++) {
			sum += node.getLeafCount();
		}
		long t1 = System.nanoTime();
		printResult("getLeafCount()", t1 - t0, reps, sum);
	}

	void printResult(String label, long nanos, int reps, int checksum) {
		float totalMs = nanos / 1_000_000f;
		float avgMs = totalMs / reps;
		println(label + " | total ms = " + totalMs + " | avg ms = " + avgMs + " | checksum = " + checksum);
	}

	// ---------------------------
	// test tree
	// ---------------------------

	DataNode<String> buildTestTree() {
		DataNode<String> r = new DataNode<>("root");

		// Example: wide-ish 5x5x5 tree
		for (int i = 0; i < 5; i++) {
			DataNode<String> a = new DataNode<>("a" + i);
			r.addChild(a);

			for (int j = 0; j < 5; j++) {
				DataNode<String> b = new DataNode<>("b" + i + "_" + j);
				a.addChild(b);

				for (int k = 0; k < 5; k++) {
					DataNode<String> c = new DataNode<>("c" + i + "_" + j + "_" + k);
					b.addChild(c);
				}
			}
		}

		return r;
	}

	DataNode<String> pickTestSubtree(DataNode<String> root) {
		// adjust to match your structure
		return root.get(2);
	}
}