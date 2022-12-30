package utTypes;

import utCore.*;
import pFns_general.PFns;
import java.util.List;

import pFns_baseObjects.Boundary;



public class ARNode extends AbstractNode<ARTree, ARNode> {
	public String splitDir = "";
	public float ar;

	public ARNode(List<ARNode> parList, String... mode) {
		super( parList , mode );
	}
	
	public void addChild( float ar ) {
		super.addChild();
		lastChild().ar = ar;
	}
	
	public void addChild( float ar, String splitDir ) {
		super.addChild();
		lastChild().ar = ar;
		lastChild().splitDir = splitDir;
	}


	// ABSTRACT FNS //////////////////////////////////////////////////

	public ARNode defaultConstructor(List<ARNode> parList, String... mode) {
		return new ARNode( parList );
	}

	public ARNode getInstance() {
		return this;
	}
	
	// GET FNS /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	
	public static float ar(float w, float h) {
		return w / h;
	}

	public float hFromW(float wDim ) {
		return wDim / ar;
	}

	public float wFromH(float hDim ) {
		return hDim * ar;
	}

	public float areaFromW( float wDim ) {
		return wDim * ( wDim / ar );
	}

	public float areaFromH( float hDim ) {
		return hDim * ar * hDim;
	}

	public float hFromArea( float area ) {
		return (float) Math.sqrt(area / ar);
	}

	public float wFromArea( float area ) {
		return area / hFromArea( area );
	}
	
	public float changeVal( String splitDir, float constantVal ) {
		if( splitDir.equals("v")) return wFromH( constantVal );
		else                      return hFromW( constantVal );
	}

	// NEW FNS ///////////////////////////////////////////////////////
	
	public void subdivideEqually( String splitDirIn, int count) {
		float[] arr = new float[count];
		for (int i = 0; i < count; i++) arr[i] = 1;
		subdivide( splitDirIn, arr );
	}
	
	public void subdivide( String splitDir, float... percents ) {
		if ( hasChildren() || percents.length == 0 ) return;
		this.splitDir = splitDir;
		if (percents.length == 1)								// splits node at percentage
			percents = new float[] { percents[0], 1 - percents[0] };
		percents = PFns.makeSumTo1(percents);
		float[] newArs = new float[percents.length];
		for (int i = 0; i < percents.length; i++) {
			if ( splitDir.equals("v") )
				newArs[i] = ar * percents[i];
			else
				newArs[i] = 1 / (1 / ar * percents[i]);
		}
		for (int i = 0; i < newArs.length; i++)
			addChild( newArs[i] );
	}

	///////////////////////////////////////////////////////

	public float calcARFromLeafs() {
		if ( hasChildren() ) {
			float arSum = 0;
			for (ARNode child : children() )
				arSum += splitDir == "v" ? child.calcARFromLeafs() : 1 / child.calcARFromLeafs();
			ar = splitDir == "v" ? arSum : 1 / arSum;
		}
		return ar;
	}
	
	
	public String toString() {
		return "ar = " + ar + ", splitDir = " + splitDir + super.toString();
	}
	
	public void importRect( Boundary input, List<Boundary> treeRects ) {
		importData( input, treeRects, (n,list) -> n.ar = n.get(list).w/ n.get( list ).h );
//		for( ARNode n : this )
//			if( n.get(treeRects) != null ) n.ar = n.get(treeRects).w/ n.get( treeRects ).h ;
	}
	
	public void importRect( List<Boundary> input, List<Boundary> treeRects ) {
		importData( input, treeRects, (n,list) -> n.ar = n.get(list).w/ n.get( list ).h );
//		for( ARNode n : this )
//			if( n.get(treeRects) != null ) n.ar = n.get(treeRects).w/ n.get( treeRects ).h ;
	}
	
	
}
