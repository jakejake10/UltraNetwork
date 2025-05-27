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





public class UN_SORT extends PApplet{
	DataNode<String> root;
	
	public static void main(String... args) {
		UN_SORT pt = new UN_SORT();
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
			root.addChildWithData("sugar");
				root.get(0).addChildWithData("rock");
				root.get(0).addChildWithData("cane");
				root.get(0).addChildWithData("brown");
				root.get(0).addChildWithData("white");
			root.addChildWithData( "molasses");
			root.addChildWithData( "chocolate");
			root.addChildWithData( "honey");
			root.addChildWithData( "stevia");
			root.addChildWithData( "fruit juice");
			root.addChildWithData( "monk fruit");
				
				
			
		root.printOperation();
			
		
//		root.get(0).sortChildren( n -> (float) n.getData().length() );
//	    root.sortChildren( n -> (float)n.getChildCount() );
		root.sortAllReverse( n -> n.getData() );
	    
//	    for( DataNode<String> node : root ) println(node + ( node.hasParent() ? "pIndex" + node.getParent().getChildCount():"") );
	    
	    println();
	    root.printOperation();
	    
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
