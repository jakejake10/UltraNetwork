package utTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;

import pFns_baseObjects.Boundary;
import pFns_general.PFns;
import utCore.*;

public class ARTree extends AbstractTree<ARTree, ARNode> {

	public ARTree(Float ar, String splitDir) {
		super(); // calls setRoot
		root().ar = ar;
		root().splitDir = splitDir;
	}

	public void setRoot() {
		new ARNode( nodes, "root"); // root node always at 0;
	}
	
	
	
	
	
	
	
	
	// INNER CLASSES ///////////////////////////////////////////////

	
	

	
	public List<Boundary> makeBoundariesXYW( float x, float y, float w ){
		Boundary rb = new Boundary( x,y,w,root().hFromW(w) );
		return makeOrderedList( rb, generateBounds2  );
	}
	
	public BiConsumer<ARNode,List<Boundary>> generateBounds2 = ( n,list ) -> {
		if( !n.hasParent() ) return;	// bound should already be added for root
		else {
			ARNode par = n.parent();
			Boundary parBound = par.get(list);
			float x,y,w,h;
			x = y = w = h = 0;
			if( par.splitDir.equals("v") ) {
				if( n.childIndex() == 0) x = parBound.x;
				else x = parBound.x + (float) IntStream.range(0, n.childIndex()).mapToDouble( i -> par.get(i).changeVal("v", parBound.h)).sum();
				y = parBound.y;
				w = n.changeVal( "v", parBound.h);
				h = parBound.h;
			}
			else {
				
				x = parBound.x;
				if( n.childIndex() == 0) y = parBound.y;
				else y = parBound.y + (float) IntStream.range(0, n.childIndex()).mapToDouble( i -> par.get(i).changeVal("h", parBound.w)).sum();
				w = parBound.w;
				h = n.changeVal( "h", parBound.w );
			}
			list.set( n.myLoc, new Boundary( x,y,w,h ) );
		}
	};
	
	
//	public BiFunction<ARNode,Float[],List<Boundary>> generateBounds = ( n,xywh ) -> {
//		List<Boundary> out = new ArrayList<>();
//		out.add( new Boundary( xywh[0],xywh[1],xywh[2],xywh[3]  ) );
//		if( n.hasChildren() ) {
//			float changePos =   n.splitDir.equals("v") ? xywh[0] : xywh[1];
//			float constantDim = n.splitDir.equals("v") ? xywh[3] : xywh[2];
//			float constantPos = n.splitDir.equals("v") ? xywh[1] : xywh[0];
//			
//			for( ARNode child : n.children() ) {
//				Float myDim = child.changeVal( constantDim );
//				Float[] childDims;
//				if( n.splitDir.equals("v" ) )
//					childDims = new Float[] { changePos,constantPos,myDim,constantDim };
//				else 
//					childDims = new Float[] { constantPos,changePos,constantDim,myDim };
//				out.addAll( this.generateBounds.apply( child, childDims ) );
//				changePos += myDim;
//			}
//		}
//		return out;
//	};
	
	

}
