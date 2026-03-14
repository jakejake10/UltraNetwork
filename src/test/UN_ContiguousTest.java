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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class UN_ContiguousTest extends PApplet {

    DataNode<String> root;

    public static void main(String... args) {
        UN_ContiguousTest pt = new UN_ContiguousTest();
        PApplet.runSketch(new String[] { "UN_ContiguousTest" }, pt);
    }

    @Override
    public void settings() {
        size(500, 300);
    }

    @Override
    public void setup() {
        surface.setTitle("NodeList Contiguity Test");

        root = buildTestTree();

        println("=== TREE INFO ===");
        println("total size = " + root.getCore().nodeList.size());

        println("\n=== CONTIGUITY TEST ===");
        testWholeTreeContiguity(root);

        noLoop();
    }

    @Override
    public void draw() {
        background(30);
        fill(255);
        text("See console for contiguity results", 20, 30);
    }

    // -------------------------------------------------------
    // MAIN TEST
    // -------------------------------------------------------

    void testWholeTreeContiguity(DataNode<String> root) {
        final int[] total = {0};
        final int[] passed = {0};
        final int[] failed = {0};

        forEachNodeRecursive(root, n -> {
            total[0]++;
            boolean ok = testNodeContiguity(n, true);
            if (ok) passed[0]++;
            else failed[0]++;
        });

        println("\n=== SUMMARY ===");
        println("nodes checked = " + total[0]);
        println("passed = " + passed[0]);
        println("failed = " + failed[0]);
    }

    boolean testNodeContiguity(DataNode<String> node, boolean printFailuresOnly) {
        ArrayList<DataNode<String>> subtreeNodes = new ArrayList<>();
        collectSubtreeNodes(node, subtreeNodes);

        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < subtreeNodes.size(); i++) {
            idxs.add(subtreeNodes.get(i).getIndex());
        }

        Collections.sort(idxs);

        boolean contiguous = true;
        for (int i = 1; i < idxs.size(); i++) {
            if (idxs.get(i) != idxs.get(i - 1) + 1) {
                contiguous = false;
                break;
            }
        }

        boolean startsAtNodeIndex = !idxs.isEmpty() && idxs.get(0) == node.getIndex();

        boolean noDuplicates = new HashSet<>(idxs).size() == idxs.size();

        boolean ok = contiguous && startsAtNodeIndex && noDuplicates;

        if (!printFailuresOnly || !ok) {
            println("-----------------------------------");
            println("node index = " + node.getIndex());
            println("subtree size(recursive) = " + subtreeNodes.size());
            println("sorted indexes = " + idxs);
            println("startsAtNodeIndex = " + startsAtNodeIndex);
            println("contiguous = " + contiguous);
            println("noDuplicates = " + noDuplicates);
            println("PASS = " + ok);
        }

        return ok;
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    void collectSubtreeNodes(DataNode<String> node, List<DataNode<String>> out) {
        out.add(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            collectSubtreeNodes(node.get(i), out);
        }
    }

    void forEachNodeRecursive(DataNode<String> node, java.util.function.Consumer<DataNode<String>> fn) {
        fn.accept(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            forEachNodeRecursive(node.get(i), fn);
        }
    }

    // -------------------------------------------------------
    // SAMPLE TREE
    // Replace this with your actual tree construction
    // -------------------------------------------------------

    DataNode<String> buildTestTree() {
        DataNode<String> r = new DataNode<String>("root");

        for (int i = 0; i < 3; i++) {
            DataNode<String> a = new DataNode<String>("a" + i);
            r.addChild(a);

            for (int j = 0; j < 3; j++) {
                DataNode<String> b = new DataNode<String>("b" + i + "_" + j);
                a.addChild(b);

                for (int k = 0; k < 2; k++) {
                    DataNode<String> c = new DataNode<String>("c" + i + "_" + j + "_" + k);
                    b.addChild(c);
                }
            }
        }

        return r;
    }
}
