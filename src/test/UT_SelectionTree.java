//package test;
//
////package processingFnTest;
//
////import processing.core.PApplet;
////import processing.core.PVector;
//import processing.core.PApplet;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import pFns_baseObjects.*;
//import pFns_general.*;
//import ugCore.*;
//import usPrimitives.RectObject;
//import usPrimitives.Rectangular;
//import utTypes.SelectionTree;
//import uvCore.UVConstants;
//
//
//
//public class UT_SelectionTree extends PApplet {
//	Grid g;
//	SelectionTree<Integer> st;
//	
//	public static void main(String... args) {
//		UT_SelectionTree pt = new UT_SelectionTree();
//		PApplet.runSketch(new String[] { "Test" }, pt);
//	}
//
//	@Override
//	public void settings() {
//		size(500, 500);
//	}
//
//	@Override
//	public void setup() {
//		noLoop();
//		g = new Grid( this, 50, 50 );
//		st = new SelectionTree<Integer>().initRoot();
//		
//		List<Integer> cols1 = new ArrayList<>();
//		for( int i = 0; i < 10; i++ ) cols1.add( color( ( i * ( 1f/ 10f ) ) * 255f ) );
//		List<Integer> cols2 = new ArrayList<>();
//			cols2.add( PFns.rColor() ); cols2.add( PFns.rColor() );
//		
//		st.addSelectionNodeGroup( 0.9f, cols1, RectObject.class, r -> UVConstants.easeSigmoid(6f).apply( noise( r.x() * 0.02f, r.y() * 0.02f ) ) )
//			.addSelectionNodeGroup( 0.001f, cols2 );
//		
//		
////		st.root().addChild();
////		st.root().addChild();
////		
////		
////		for( int i = 0; i < 10; i++ ) {
////			float val = ( i * ( 1f/ 10f ) ) * 255f;
//////			println(val);
////			st.root().get(0).addData( color( val ) );
////		}
////		st.root().get(1).addData( PFns.rColor() );
////		st.root().get(1).addData( PFns.rColor() );
////		st.root().setProbability( 0.9f, 0.1f );
//	}
//
//	@Override
//	public void draw() {
//		st.printOperation( n -> (n.data == null) ? n +  "null" :  n + n.data.toString() );
////		g.display(this);
////		st.apply( st.getRandom, g.getRects(), ( c,r ) -> { fill( c ); r.display( this ); } ); 
//		List<RectObject> rects = g.getRects();
//		
////		st.apply( st.getRandom, g.getRects(), ( c,r ) -> { fill( c ); r.display( this ); } ); 
//		st.apply( g.getRects(), ( c,r ) -> { fill( c ); r.display( this ); } );
////		println( );
////		for( int i = 0; i < 20; i++ ) {
//////			st.root().get(0).getRandomDisplay();
////			st.getRandomDisplay();
////			println( );
////		}
////		println( );
//		save( "\\data\\grid.jpg" );
//		println( "done" );
//	}
//
//}
