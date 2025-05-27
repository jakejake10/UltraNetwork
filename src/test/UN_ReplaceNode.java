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





public class UN_ReplaceNode extends PApplet{
	DataNode<String> root;
	DataNode<Number> root2;
	
	public static void main(String... args) {
		UN_ReplaceNode pt = new UN_ReplaceNode();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
//		root = new DataNode<>("root");
//			root.addChildWithData( "c0");
//				root.get(0).addChildWithData("c00");
//			root.addChildWithData( "c1");
//			root.addChildWithData( "c2");
//				root.get(2).addChildWithData("c20");
//////		root.printOperation();
////		
//		DataNode<String> root2 = new DataNode<>("root2");
//			root2.addChildWithData("newChd");
//		
//		root2.replaceWith(root.get(0));
////		root.get(1).replaceWith( root2 );
////		root.get(0).replaceWith(root2);
////		
//		root.printOperation();
////		
////		println( root.getCore());
////		root2.get(0).replaceWith( root );
//		println();
//		root2.printOperation();
		
		//////////////////////////////////////////////
		
		root2 = new DataNode<Number>(Integer.valueOf(3) );
		println(root2.getData().getClass());
		root2.replaceNodeSubtree( new DataNode<>( Float.valueOf(3) ) );
		println(root2.getData().getClass());
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
