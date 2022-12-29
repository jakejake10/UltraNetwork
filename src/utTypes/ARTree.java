package utTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import pFns_baseObjects.Boundary;
import pFns_general.PFns;
import utCore.*;

public class ARTree extends AbstractTree<ARTree, ARNode> {

	public ARTree(Float ar, String splitDir) {
		super(); // calls setRoot
		getRoot().ar = ar;
		getRoot().splitDir = splitDir;
	}

	public void setRoot() {
		new ARNode(this, "root"); // root node always at 0;
	}
	
	
	
	
	
	
	
	
	// INNER CLASSES ///////////////////////////////////////////////

	
	
//	public Boundary makeBoundaryFromW( AbstractNode node, float x, float y, float w ) {
//		return new Boundary( x, y, w, hFromWAR( w, ar(node) ) );
//	}
//	
//	public List<Boundary> makeRects(  Boundary initRect, AbstractNode...node ) {
//		AbstractNode targetNode = node.length > 0 ? node[0] : root();
//		System.out.println( "tarset" );
//		List<Boundary> out = new ArrayList<Boundary>();
//		for( int i = 1; i < size(); i++  ) out.add( null );
//		targetNode.setVal(initRect, out);
//		System.out.println( "valAdded" );
//		makeRects( targetNode, out );
//		return out;
//	}
//	
//	public void makeRects(AbstractNode node, List<Boundary> data ){
//		if( !node.hasChildren() ) return; 
//		Boundary par = node.getVal(data);
//		float change = splitDir(node).equals("v") ? par.x : par.y;
//		for( AbstractNode child : node ) {
//			if (splitDir(node).equals("v")) {
//				float wVal = par.w * (ar(child) / ar(node) );
//				child.setVal( new Boundary( change, par.y, wVal, par.h ), data );
//				change += wVal;
//			} else {
//				float hVal = par.h * ( ( 1 / ar(child) ) / ( 1 / ar(node) ) );
//				child.setVal( new Boundary( par.x, change, par.w, hVal ), data );
//				change += hVal;
//			}
//			makeRects( child, data );
//		}
//		
//	}
	
	public List<Boundary> makeBoundariesXYW( float x, float y, float w ){
		return generateBounds.apply(getRoot(), new Float[] { x,y,w,getRoot().hFromW(w) } );
	}
	
	
	public BiFunction<ARNode,Float[],List<Boundary>> generateBounds = ( n,xywh ) -> {
		List<Boundary> out = new ArrayList<>();
		if( !n.hasParent() || !n.hasChildren() ) out.add( new Boundary( xywh[0],xywh[1],xywh[2],xywh[2]  ) );
		if( n.hasChildren() ) {
			float changePos =   n.splitDir.equals("v") ? xywh[0] : xywh[1];
			float constantDim = n.splitDir.equals("v") ? xywh[3] : xywh[2];
			float constantPos = n.splitDir.equals("v") ? xywh[1] : xywh[0];
			for( ARNode child : n.children() ) {
				Float myDim = child.changeVal( constantDim, n.ar );
				Float[] childDims;
				if( n.splitDir.equals("v" ) )
					childDims = new Float[] { changePos,constantPos,myDim,constantDim };
				else 
					childDims = new Float[] { constantPos,changePos,constantDim,myDim };
				List<Boundary> bounds =  this.generateBounds.apply( child, childDims );
				System.out.println( "bounds generated, size = " + bounds.size() );
				out.addAll( bounds );
				changePos += myDim;
			}
		}
		return out;
	};

}
