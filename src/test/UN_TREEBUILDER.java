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





public class UN_TREEBUILDER extends PApplet{
	DataNode<Integer> root;
	
	public static void main(String... args) {
		UN_TREEBUILDER pt = new UN_TREEBUILDER();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		root = new DataNode<>();
		root.buildTree()
			.setChildCt(2)
			.setChildCtMod( (n,i) -> floor(random( 1,4)))
			.setMaxDepth( 4 )
			.setPostGenMod( n -> n.setData(n.getDepth()))
			.setLeafCt(8)
		
//			.setChildCt(2)
//			.setMaxDepth(2)
//			.setMaxDepthMod( (n,i) -> i + floor(random(10)) )
//			.setPostGenMod( n -> { if(n.isLeaf()) n.setData(1); } )
			
//			.setChildCtByDepth( 3,2,1 )
//			.setPostGenMod( n -> n.setData( n.getChildCount() ) )
			
			.make();
		root.printOperation();
		
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
