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





public class UN_DRAWTREE extends PApplet{
	DataNode<String> root;
	
	float nodeW = 50;
	float nodeH = 20;
	float xGap = 10;
	float yGap = 20;
	
	public static void main(String... args) {
		UN_DRAWTREE pt = new UN_DRAWTREE();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(1000, 500);
	}

	@Override
	public void setup() {
		noLoop();
		strokeWeight( 2 );
		root = new DataNode<>( "root" );
		
		root.buildTree()
		.setChildCt(3)
		.setChildCtMod( (n,i) -> floor(random( 1,3 )))
		.setMaxDepth( 4 )
		.setMaxDepthMod( (n,i) -> n.getDepth() > 2 ? floor( random(2,8) ) : i )
		.setPostGenMod( n -> n.setData(String.valueOf(n.getDepth())))
		.make();
		
		root.printOperation();
		fill( 255, 100 );
		
//		drawNode( root, 250, 100, 30, 10 );
		
//		List<Float> nodeWidths = TreeNodeFunctions.initDataList( root, 0f );
		textAlign( CENTER,CENTER );
		textSize( 16 );
		drawNode( root, 500, 100 );
	}

	@Override
	public void draw() {
		
		
	}
	
	public void drawNode( DataNode<String> node, float xc, float y ) {
		fill(255);
		strokeWeight( 1.5f );
		rect( xc - nodeW / 2, y, nodeW, nodeH );
		strokeWeight( 2 );
		fill(0);
		text( node.getData(), xc, y+5 );
		if( node.hasChildren() ) {
			int leafs = node.getLeafCount();
			float leafsW = leafs*nodeW;
			float spaceW = (leafs-1)*xGap;
			float gapW = spaceW / ( node.getChildCount() - 1 );
			float curX = xc - (leafsW+spaceW) / 2;
			if( node.getChildCount() > 1 ) line( xc, y+nodeH, xc, y+nodeH+yGap/2 );
			float startX = 0;
			float endX = 0;
			for( int i = 0; i < node.getChildCount(); i++ ) {
				float cWidth = ( node.get(i).getLeafCount() * nodeW );
				float cNodeCenter = curX + cWidth / 2;
				drawNode( node.get(i), cNodeCenter, y + nodeH + yGap );
				if( i == 0 ) startX = cNodeCenter;
				if( i == node.getChildCount()-1) endX = cNodeCenter;
				if( node.getChildCount() > 1 ) line( cNodeCenter, y+nodeH+yGap, cNodeCenter, y+nodeH+yGap/2);
				
				curX += cWidth+gapW;
			}
			if( node.getChildCount() == 1 ) line(xc, y+nodeH, xc,y+nodeH+yGap );
			else line( startX, y + nodeH + yGap/2, endX, y + nodeH + yGap / 2 );
		}
		
	}
	
//	public void drawNode( DataNode<String> node, float xc, float y ) {
//		fill(255);
//		strokeWeight( 1.5f );
//		rect( xc - nodeW / 2, y, nodeW, nodeH );
//		strokeWeight( 2 );
//		fill(0);
//		text( node.getData(), xc, y+5 );
//		if( node.hasChildren() ) {
//			int leafs = node.getLeafCount();
//			float leafsW = leafs* nodeW;
////			float spaceW = node.getChildCount() == 1 ? 0 : (leafs-1)*xGap;
//			float spaceW = (leafs-1)*xGap;
//			float gapW = spaceW / ( node.getChildCount() - 1 );
//			
//			float curX = xc - (leafsW+spaceW) / 2;
//			if( node.getChildCount() > 1 ) line( xc, y+nodeH, xc, y+nodeH+yGap/2 );
//			float startX = 0;
//			float endX = 0;
//			for( int i = 0; i < node.getChildCount(); i++ ) {
//				float cWidth = ( node.get(i).getLeafCount() * nodeW );
//				float cNodeCenter = curX + cWidth / 2;
//				drawNode( node.get(i), cNodeCenter, y + nodeH + yGap );
//				if( i == 0 ) startX = cNodeCenter;
//				if( i == node.getChildCount()-1) endX = cNodeCenter;
//				if( node.getChildCount() > 1 ) line( cNodeCenter, y+nodeH+yGap, cNodeCenter, y+nodeH+yGap/2);
//				
//				curX += cWidth+gapW;
//			}
//			if( node.getChildCount() == 1 ) line(xc, y+nodeH, xc,y+nodeH+yGap );
//			else line( startX, y + nodeH + yGap/2, endX, y + nodeH + yGap / 2 );
//		}
//		
//	}
	
//	public void drawNode( DataNode<?> node, float xc, float y, float w, float h ) {
//		rect( xc - w/2, y, w, h );
//		if( node.hasChildren() ) {
//			for( DataNode<?> c : node.getChildren() ) drawNode( c, xc, y + h + 10, w, h );
//		}
//	}
	
	
//	<E extends TreeNodeObject<E> & Iterable<E>> void drawTree( E node, float nodeX, float nodeY ){
//		  int levels = node.getMaxDepth();
//		  float xMargin = 10;
//		  float yMargin = 40;
//		  float w = 30;
//		  float h = 10;
//		  List<Integer> childWidths = (List<Integer>)TreeNodeFunctions.initDataList(node);
//		  node.bottomUpOperation(childWidths, (wList,n) -> {
//			  if(n.isLeaf()) childWidths.set(n.getIndex(), 1);
//			  else {
//				  int out = 0;
//				  for( E child : n.getChildren() ) out += childWidths.get(child.getIndex());
//				  childWidths.set( n.getIndex(), out );
//			  }
//		  });
//		  
//		  node.printOperation( n -> "width: " + childWidths.get(n.getIndex() ) );
//		  
//		  drawChildNode( node, childWidths, nodeX, nodeY );
//		}
//	
//	<E extends TreeNodeObject<E> & Iterable<E>> void drawChildNode( E node, List<Integer> childWidths, float parXC, float parY ) {
//		rect( parXC-15, parY, 30, 10 );
//		if( !node.hasChildren() ) return;
//		float newXC = parXC - ( childWidths.get(node.getIndex()) * 30f ) / 2f;
//		float newY = parY + 10 + 10;
//		for( int i = 0; i < node.getChildCount(); i++ ) {
//			int childW = childWidths.get(node.get(i).getIndex()) * 30;
//			float childWidth = 30 * childW + 10 * (childW-1);
//			drawChildNode( node.get(i), childWidths, newXC + childW/2, newY );
//			newXC += childWidth;
//		}
//	}
	

}
