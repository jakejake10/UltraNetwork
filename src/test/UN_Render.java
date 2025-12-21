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





public class UN_Render extends PApplet{
	DataNode<String> root;
	
	public static void main(String... args) {
		UN_Render pt = new UN_Render();
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

		root.printOperation();
//		
		TreeNodeFunctions.renderTree(root, this).setPosition(250,60).make().render();
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
