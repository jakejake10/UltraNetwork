package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_TreeNodeMap extends PApplet {
	TreeNode<Number> root,n2;
//	GraphNode<Integer> graph;
	
	public static void main(String... args) {
		UT_TreeNodeMap pt = new UT_TreeNodeMap();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		
		
		root = new TreeNode<Number>().setData(3);
			root.addChild().setData(99);
			root.get(0).addChild().setData(0);
			root.addChild().setData(Integer.valueOf(3));
			root.addChild().setData(99);
		
//		TreeNode<String> root2 = root.map( i -> "string: " + String.valueOf(i) );
//		root.map( n -> "string" + String.valueOf(n) ).printOperation( n -> n.getData() );
			
		root.printOperation( n -> n.getData() );
//		root2.printOperation( n -> n.getData() );
	}

	@Override
	public void draw() {}

}
