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





public class UN_BFSNextNode extends PApplet{
	DataNode<String> root;
	
	public static void main(String... args) {
		UN_BFSNextNode pt = new UN_BFSNextNode();
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

		DataNode<String> curNode = root;
		println( curNode.getNextNodeBFS(0) );
		while( curNode.getNextNodeBFS(0) != null ) {
			println( curNode.getData());
			
			curNode = curNode.getNextNodeBFS(0);
		}
		println("hi");
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
