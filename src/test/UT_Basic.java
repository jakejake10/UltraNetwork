package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;
import usPrimitives.RectObject;
import utCore.*;
import utTypes.*;
import pFns_baseObjects.*;
import pFns_general.*;

import java.util.List;



public class UT_Basic extends PApplet {
	RectTree t;
	
	public static void main(String... args) {
		UT_Basic pt = new UT_Basic();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		t = new RectTree(new RectObject(50, 50, 400, 400));
//		t.root().subdivide( "v", 3 );
		t.build( 2 ).addSplit(3).addSplit( n -> (int)random(1,10) ).make();
		t.get(0).get(0).get(0).subdivide("h", 6);
		t.printOperation( n -> n.getElem( t.crObjs ));
	}

	@Override
	public void draw() {
		background(255);
		t.display( this );
//		for( RectNode n : t.leafs() ) n.display(this);
//		t.get(0).get(0).display(this);
	}

}
