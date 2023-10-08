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
//import utTypes.BasicNode;
//import utTypes.SelectionTree;
//import uvCore.UVConstants;
//
//
//
//public class UT_BasicTreeNode extends PApplet {
//	BasicNode root;
//	List<Float> vals = new ArrayList<>();
//	List<Float> vals2;
//	
//	public static void main(String... args) {
//		UT_BasicTreeNode pt = new UT_BasicTreeNode();
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
//		
//		root = new BasicNode().initTree();
//			root.addChild();
//			root.addChild();
//			root.addChild();
//			
//		root.get(1).addChild();
//		
//		for( int i = 0; i < root.size(); i++ ) vals.add( (float) i );
//		
////		root.modifyElems( n -> n.getLeafs(), f -> f * 4, vals );
////		vals2 = root.makeList( (n,f) -> f * 4, vals );
////		vals2 = root.makeList( (n,f) -> n.sum( BasicNode::getChildren, vals ), vals );
////		root.modifyElems( (n,f) -> f.hasChildren(), null);
//	}
//
//	@Override
//	public void draw() {
////		root.tree.printOperation( n -> n.getElem(vals) );
////		println();
//		
////		root.tree.printOperation( n -> n.getElem(vals2) );
////		for( BasicNode n : root.dfs() ) println( n.getElem(vals) );
////		println();
////		for( BasicNode n : root.bfs() ) println( n.getElem(vals) );
//		println( "done" );
//	}
//
//}
