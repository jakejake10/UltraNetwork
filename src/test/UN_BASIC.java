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
			root.addChild( "c0");
				root.get(0).addChild("c00");
			root.addChild( "c1");
			root.addChild( "c2");
			root.get(2).addChild("c20");
		root.printOperation();
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
