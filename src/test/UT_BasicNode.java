package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import utCore.*;
import utTypes.*;
import pFns_baseObjects.*;
import pFns_general.*;

import java.util.Iterator;
import java.util.List;



public class UT_BasicNode extends PApplet {
	UltraShape t;
	
	public static void main(String... args) {
		UT_BasicNode pt = new UT_BasicNode();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		t = new UltraShape();
		t.addChild();
		t.addChild();
		t.addChild();
		t.get(0).addChild();
//		t.printOperation( n -> n);
		for( UltraShape us : t ) println( us );
		println( "-----------");
		for( UltraShape us : t.dft() ) println( us );
		println( "-----------");
		for( UltraShape us : t.bft() ) println( us );
	}

	@Override
	public void draw() {
		background(255);
		println( "size = " + t.getLeafs().size() );
		int i = 0;
		
		println( "it = " + t.getLeafCount() );
//		for( RectNode n : t.leafs() ) n.display(this);
//		t.get(0).get(0).display(this);
	}
	
	
	class UltraShape extends StandaloneNode<UltraShape>{
		
		public UltraShape defaultConstructor() {
			return new UltraShape();
		}
		public UltraShape getInstance() {
			return this;
		}
	
	}

}
