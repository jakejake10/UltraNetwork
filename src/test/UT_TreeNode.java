package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_TreeNode extends PApplet {
	TreeNode<Integer> root;
//	GraphNode<Integer> graph;
	
	public static void main(String... args) {
		UT_TreeNode pt = new UT_TreeNode();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		
		root = new TreeNode<Integer>();
		root.setDataGenerator( n -> n.index() );
		
			root.addChild();
			root.addChild();
//			root.addChild();
		root.get(1).addChild();
		println( root.nodeList().size() );
		
//		graph = new GraphNode<>();
//		graph.addNodes( 3 );
////		graph.makeCircular();
//		graph.makeComplete();
//		println( graph.get(2) );
//		graph.addEdge( 0, 1);
//		graph.addEdge( 0, 3);
//		graph.addEdge( 1, 2);
//		graph.addEdge( 3, 2);
		
//		println( graph.totalSize() );
//		println("graph bfs");
//		for( GraphNode<Integer> n : graph.bfs() ) println( n.index() );
//		println();
//		println("graph dfs");
//		for( GraphNode<Integer> n : graph.dfs() ) println( n.index() );
//		println();
////		println( graph.get(3).index() );
	}

	@Override
	public void draw() {

		println( "treeNode:" );
		root.printOperation( n -> n.index() + ", data: " + n.getData() );
		println();
		root.reverse();
		root.printOperation( n -> n.index() + ", data: " + n.getData() );
		println( "done" );
		
	}

}
