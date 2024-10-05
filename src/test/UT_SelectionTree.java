package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import utTypes.SelectionNode;



public class UT_SelectionTree extends PApplet {
	SelectionNode<Integer> st;
	
	public static void main(String... args) {
		UT_SelectionTree pt = new UT_SelectionTree();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		st = new SelectionNode<>();
		
		List<Integer> cols1 = new ArrayList<>();
		for( int i = 0; i < 10; i++ ) cols1.add( color( ( i * ( 1f/ 10f ) ) * 255f ) );
		List<Integer> cols2 = new ArrayList<>();
//			cols2.add( color(random(255),random(255),random(255)) ); 
			cols2.add( color(255,0,0) );
			cols2.add( color(255,0,255) );
		
//		st.addValues( cols2 );
//		st.addSelectionNodeGroup( 0.9f, cols1, RectObject.class, r -> UVConstants.easeSigmoid(6f).apply( noise( r.x() * 0.02f, r.y() * 0.02f ) ) )
//			.addSelectionNodeGroup( 0.001f, cols2 );
//		
		
//		st.root().addChild();
//		st.root().addChild();
//		
//		
//		for( int i = 0; i < 10; i++ ) {
//			float val = ( i * ( 1f/ 10f ) ) * 255f;
////			println(val);
//			st.root().get(0).addData( color( val ) );
//		}
//		st.root().get(1).addData( PFns.rColor() );
//		st.root().get(1).addData( PFns.rColor() );
//		st.root().setProbability( 0.9f, 0.1f );
	}

	@Override
	public void draw() {
		background( st.get() );
		println( "done" );
	}

}
