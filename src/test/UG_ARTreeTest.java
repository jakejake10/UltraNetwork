//package test;
//
////package processingFnTest;
//
////import processing.core.PApplet;
////import processing.core.PVector;
//import processing.core.*;
//
//import usCore.*;
//import usPrimitives.ARObject;
//import usPrimitives.RectObject;
//import usPrimitives.Rectangular;
//import usPrimitives.TextBox;
//import utTypes.BasicNode;
//import ugCore.*;
//import uvCore.*;
////import utTypes.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.stream.*;
//
//import pFns_general.PFns;
//import pFns_general.PVAFns;
//
//
//
//public class UG_ARTreeTest extends PApplet implements USConstants,UVConstants {
//	
//	BasicNode n;
//	List<String> splitTypes = new ArrayList<>();
//	List<Float> ars = new ArrayList<>();
//	List<ARNode> ans = new ArrayList<>();
//	
//	public static void main(String... args) {
//		UG_ARTreeTest pt = new UG_ARTreeTest();
//		PApplet.runSketch(new String[] { "Test" }, pt);
//	}
//
//	@Override
//	public void settings() {
//		size(512, 512);
//	}
//
//	
//	
//	@Override
//	public void setup() {
//		n = new BasicNode().initTree();
//		for( int i = 0; i < 10; i++ ) ars.add( random( 0.5f, 2.5f ) );
//		
//		n.buildWithLeafData( ars, n.makeBinaryTree() );
////		ars = bn.setLeafElems(ars, null);
//		ans = n.setLeafElems( ars, f -> new ARNode(f) );
////		for( BasicNode bn : n )  if( !bn.isLeaf() ) bn.setElem( new ARNode(0), ans );
////		for( BasicNode bn : n )  if( !bn.isLeaf() ) bn.modifyElem( e -> e.splitDir = random(1) < 0.5 ? "h" : "v", ans );
////		for( ARNode a : n.bfs( ans ) ) a.splitDir = random(1) < 0.5 ? "h" : "v";
////		println( ars );
////		println( bn.tree );
////		n.tree.printOperation( n -> "splitDir: " + n.getElem(ans).splitDir + ", ar: " + n.getElem(ans).ar );
//	}
//	
//	
//	
//	
//
//	@Override
//	public void draw() {
//		
//	}
//	
//	class ARNode{
//		float ar,tar;
//		String splitDir = "";
//		
//		ARNode( float ar ){
//			this.ar = ar;
//		}
//	}
//
//
//}
