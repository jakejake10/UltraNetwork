//package test;
//
////package processingFnTest;
//
////import processing.core.PApplet;
////import processing.core.PVector;
//import processing.core.PApplet;
//import usCore.UltraShape;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//import usPrimitives.Rectangular;
//import utTypes.SelectionNode;
//import uvCore.UVConstants;
//
//
//
//public class UT_SelectionTree2 extends PApplet {
//	UltraShape g;
//	SelectionNode<Integer> sn;
//	
//	public static void main(String... args) {
//		UT_SelectionTree2 pt = new UT_SelectionTree2();
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
//		g = UltraShape.grid( 50, 50 ).position(10,10,480,480);
//		sn = new SelectionNode<Integer>();
//		
//		List<Integer> cols1 = new ArrayList<>();
//		for( int i = 0; i < 10; i++ ) cols1.add( color( ( i * ( 1f/ 10f ) ) * 255f ) );
//		List<Integer> cols2 = new ArrayList<>();
//			cols2.add( UVConstants.rColor() ); cols2.add( UVConstants.rColor() );
//		
////		sn.addValues( 0.9f, cols1, RectObject.class, r -> UVConstants.easeSigmoid(6f).apply( noise( r.x() * 0.02f, r.y() * 0.02f ) ) )
//		sn.addValues( 0.9f, cols1 )
//
//			.addSelectionNodeGroup( 0.1f, cols2 );
//		
//		
//		
//	}
//
//	@Override
//	public void draw() {
//		sn.tree.printOperation( n -> (n.data == null) ? n +  "null" :  n + n.data.toString() );
//		List<RectObject> rects = g.getRects();
//		
//		sn.selectFn( g.getRects(), ( c,r ) -> { fill( c ); r.display( this ); } );
//
//		save( "\\data\\grid.jpg" );
//		println( "done" );
//	}
//
//}
