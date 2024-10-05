package test;

import java.util.ArrayList;
import java.util.List;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import utTypes.*;
import unCore.*;



public class UT_ImportData extends PApplet {
	TreeNode<Integer> root,root2;
//	GraphNode<Integer> graph;
	
	public static void main(String... args) {
		UT_ImportData pt = new UT_ImportData();
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
		
		List<Integer> data = new ArrayList<>();
		for( int i = 0; i < 50; i++ ) data.add( floor(random(5) ) );
		
		Build.importToLeavesBinary( root, data );
		
		root.printOperation();
	}

	@Override
	public void draw() {}

}
