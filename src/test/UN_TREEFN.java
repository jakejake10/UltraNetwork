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





public class UN_TREEFN extends PApplet{
	TreeFunctionGroup<Void,Integer> root;
	
	public static void main(String... args) {
		UN_TREEFN pt = new UN_TREEFN();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		root = new TreeFunctionGroup<>( (nList,i) -> nList.get(floor(random(nList.size()))));
			root.addChild( new TreeFunctionGroup<>( (nList,i) -> nList.get(floor(random(nList.size())))));
			root.get(0).addChild( new TreeFunctionLeaf<>( (n,i) -> 1 ));
			root.get(0).addChild( new TreeFunctionLeaf<>( (n,i) -> 3 ));
			root.get(0).addChild( new TreeFunctionLeaf<>( (n,i) -> 5 ));
			root.addChild( new TreeFunctionLeaf<>( (n,i) -> floor(random(50,100)) ));
			
		for( int i = 0; i < 10; i++ ) println( root.runFn( null ) );
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
