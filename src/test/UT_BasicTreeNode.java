package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import pFns_baseObjects.*;
import pFns_general.*;
import ugCore.*;
import usPrimitives.RectObject;
import usPrimitives.Rectangular;
import utTypes.BasicNode;
import utTypes.SelectionTree;
import uvCore.UVConstants;



public class UT_BasicTreeNode extends PApplet {
	BasicNode root;
	
	public static void main(String... args) {
		UT_BasicTreeNode pt = new UT_BasicTreeNode();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		root = new BasicNode().initTree();
			root.addChild();
			root.addChild();
			root.addChild();
			
		root.get(1).addChild();
		
	}

	@Override
	public void draw() {
		println( root.tree );
		println( root.get(1).get(0).get(0) );
		println( "done" );
	}

}
