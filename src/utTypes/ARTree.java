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
	
	
	public void whFromLeafs( List<Boundary> rectList ) {
		whFromLeafs.accept( root(), rectList );
	}
	
	BiConsumer<ARNode,List<Boundary>> whFromLeafs = (n,list) ->{		// leaf list of rects
		if( n.isLeaf() ) return;
		else {
			float w = 0; 
			float h = 0;
			float maxElem = 0;
			for( ARNode child : n.children() ) {
				if( child.get(list) == null ) this.whFromLeafs.accept( child, list );
				if( n.splitDir.equals("v" ) ) {
					w+=child.get(list).w;
					float ch = child.get(list).h;
					if( ch > maxElem ) maxElem = ch;
				}
				else {
					h+=child.get(list).h;
					float cw = child.get(list).w;
					if( cw > maxElem ) maxElem = cw;
				}
			}
			if( n.splitDir.equals("v") ) n.setVal( new Boundary(0,0,w, maxElem ), list );
			else                         n.setVal( new Boundary(0,0,maxElem, h ), list );
		}
	};
	
	public void arrangeRectsXY( float x, float y, List<Boundary> data ) {
		root().get( data ).x = x;
		root().get( data ).y = y;
		arrangeRects( root(), data );
	}
	
	public void arrangeRects( ARNode node, List<Boundary> data ) {
		if( !node.hasChildren() ) return;
		float changeVal = node.splitDir.equals("v") ? node.get(data).x : node.get(data).y;
		for( int i = 0; i < node.size; i++ ) {
			if( node.splitDir.equals("v" ) ) {
				float childX = changeVal;
				float childY = node.get(data).y;
				node.get(i).get(data).x = childX;
				node.get(i).get(data).y = childY;
				changeVal += node.get(i).get(data).w;
			} else {
				float childX = node.get(data).x;
				float childY = changeVal;
				node.get(i).get(data).x = childX;
				node.get(i).get(data).y = childY;
				changeVal += node.get(i).get(data).h;
			}
			arrangeRects( node.get(i), data );
		}
	}
	
	
	
	
	
	

}
