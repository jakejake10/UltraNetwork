package test;

import java.util.function.BiFunction;
import java.util.function.Function;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_Copy_TreeNode extends PApplet {
	TreeNode<Integer> root,root2;
//	GraphNode<Integer> graph;
	
	public static void main(String... args) {
		UT_Copy_TreeNode pt = new UT_Copy_TreeNode();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		
		
		
		root = new TreeNode<Integer>().setData(3);
//		root.setDataGenerator( n -> n.index() );
			for( int i = 0; i < 4; i++ ) root.addChild().setData( i );
//		root.printOperation();
		println();
		
		
		//////////////////////////////////////////////////
		
//		root2 = root.copyTreeWithData();
		
		
		//////////////////////////////////////////////////
		
//		TreeNode<Integer> root2 = new TreeNode<Integer>();
		Function<TreeNode<Integer>,TreeNode<Integer>>
			convert = on -> new TreeNode<Integer>().setData( on.getData() );
		root2 = NodeObj.convertTypeFromTo(root, convert );
		
		root.printOperation();
		
		println();
		root2.printOperation();
		println();
//		for( TreeNode<Integer> n : root2.nodeList() ) println( n );
		
		
	}

	@Override
	public void draw() {}

}
