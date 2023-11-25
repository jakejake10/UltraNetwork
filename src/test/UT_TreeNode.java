package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_TreeNode extends PApplet {
	TreeNode<Integer> root,n2;
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
				root.get(1).addChild();
					root.get(1).get(0).addChild();
				root.get(1).addChild();
			root.addChild();
			
			
//		println( "size = " + root.size() );
			
//		n2 = new TreeNode<>();
//		n2.setData(8);;
		
		
//		root.get(1).addChild();
//		println( root.nodeList().size() );
		
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

		root.printOperation();
		println();
//		TreeNode<Integer> r2 = root.get(0).decouple();
//		root.addChild().addChild( r2 );
//		root.get(0).insertParent();
		
//		root.get(0).insertParent().setData(99);
		root.getRoot().printOperation();
		
		
		
		
////		println( root.get(0).getData() );
//		for( TreeNode<Integer> node : root.get(1) ) println( node );
//		TreeNode<Integer> nd = root.get(1).decoupleFromTree();
//		println();println();
//		nd.printOperation( n -> n.index() + ", data: " + n.getData() );
//		for( TreeNode<Integer> node : nd ) println( node );
//		rootCopy.printOperation( n -> n.index() + ", data: " + n.getData() );
		
//		TreeNode<Integer> copy = 

	}

}
