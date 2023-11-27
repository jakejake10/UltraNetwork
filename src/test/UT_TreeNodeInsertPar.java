package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_TreeNodeInsertPar extends PApplet {
	TreeNode<Integer> root;
//	GraphNode<Integer> graph;
	
	public static void main(String... args) {
		UT_TreeNodeInsertPar pt = new UT_TreeNodeInsertPar();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(5, 5);
	}

	@Override
	public void setup() {
		noLoop();
		
		
		
		root = new TreeNode<Integer>().setData(0);
			root.addChild().setData(10);
				root.get(0).addChild().setData(20);	// bad
			root.addChild().setData(11);
				root.get(1).addChild().setData(21);
			root.addChild().setData(12);
				root.get(2).addChild().setData(22);
			


		root.printOperation();
//		
//		root.get(0).addChild();
		println();
//		root.printOperation();
////		root.get(0).addChildAtIndex(0);
////		root.get(0).addChildAtIndex(0);
//		root.addChildAtIndex(0).setData(666);
//		
//		
//		root.printOperation();
//		
//		println();
		
		
				
		root.insertParent();
		root.getRoot().insertParent();
		
		root.leafOperation( n -> n.insertParent() );
		root.leafOperation( n -> n.insertParent() );
		
		
				
		root.getRoot().printOperation();
//
//		root.printOperation();
//		
//		println();
////		println("leafs:");
////		println(root);
////		println( root.getRoot().get(0));
////		for( TreeNode<Integer> n : root.getLeafs() ) println( n );
////		for( TreeNode<Integer> node : root.nodeList() ) if( node.getData() != null && node.getData() == 20 ) println( node );
//		for( TreeNode<Integer> node : root.nodeList() ) println( node );
////		println( nd );
//		println();
//		println(root);
	
	}

	@Override
	public void draw() {

	}

}
