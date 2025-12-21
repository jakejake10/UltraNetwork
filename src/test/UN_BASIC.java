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





public class UN_BASIC extends PApplet{
	DataNode<String> root;
	
	public static void main(String... args) {
		UN_BASIC pt = new UN_BASIC();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		root = new DataNode<>("root");
			root.addChildWithData( "c0");
				root.get(0).addChildWithData("c00");
			root.addChildWithData( "c1");
			root.addChildWithData( "c2");
				root.get(2).addChildWithData("c20");
////		root.printOperation();
//		
		DataNode<String> root2 = new DataNode<>("root2");
			root2.addChildWithData("newChd");
		
		root2.replaceNodeSubtree(root.get(0));
//		root.get(1).replaceWith( root2 );
//		root.get(0).replaceWith(root2);
//		
		root.printOperation();
//		
//		println( root.getCore());
//		root2.get(0).replaceWith( root );
		println();
//		root2.printOperation();
//		root.removeChildren();
		root.removeChild(0);
		root.printOperation();
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
