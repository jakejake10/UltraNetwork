package utTypes;
//
import utCore.*;

//import java.util.List;
//import java.util.ArrayList;
//import java.util.function.*;
//
//import pFns_general.*;
//import pFns_baseObjects.Boundary;
//
public class ARTree  {
//
//	private List<Float> ar = new ArrayList<Float>();
//	private List<String> splitDir = new ArrayList<String>();
//
//	public ARTree() {
//		this(null, null);
//	}
//
//	public ARTree(Float ar) {
//		this(ar, null);
//	}
//
//	public ARTree(Float ar, String splitDir) {
//		super();
//		this.ar.add(ar);
//		this.splitDir.add(splitDir);
//	}
//
//	///////////////////////////////////////////////////////////////////
//
//	public void addChild(AbstractNode par, float ar, String splitDir) {
//		par.addChild();
//		par.lastChild().setVal(ar, this.ar);
//		par.lastChild().setVal(splitDir, this.splitDir);
//	}
//
////	void addChild( Node node, float ar, String splitDir ) {
////		node.addChild();
////		ars.add(ar);
////		splitDirs.add(splitDir);
////	}
//
//	public void subdivideEqually( AbstractNode node, String splitDirIn, int count) {
//		float[] arr = new float[count];
//		for (int i = 0; i < count; i++)
//			arr[i] = 1;
//		subdivide(node, splitDirIn, arr);
//	}
//
//	public void subdivide(AbstractNode node, String splitDirIn, float... percents) {
//		node.setVal(splitDirIn, this.splitDir);
//		subdivide(node, percents);
//	}
//
//	public void subdivide(AbstractNode node, float... percents) {
//		if (node.hasChildren()) return;
//		if (percents.length == 1) percents = new float[] { percents[0], 1 - percents[0] };
//		percents = PFns.makeSumTo1(percents);
//		float[] newArs = new float[percents.length];
//		for (int i = 0; i < percents.length; i++) {
//			if (node.getVal(splitDir).equals("v"))
//				newArs[i] = node.getVal(ar) * percents[i];
//			else
//				newArs[i] = 1 / (1 / node.getVal(ar) * percents[i]);
//		}
//		for (int i = 0; i < newArs.length; i++)
//			addChild(node, newArs[i], null);
////			children.add(new ARNode(newArs[i], this));
//	}
//
//	///////////////////////////////////////////////////////
//
//	public void setARs(AbstractNode node) {
//		if (!node.hasChildren()) return;
//		else {
//			float arSum = 0;
//			for (AbstractNode child : node)
//				arSum = node.getVal(splitDir) == "v" ? child.getVal(ar) : 1 / child.getVal(ar);
//			node.setVal(node.getVal(splitDir) == "v" ? arSum : 1 / arSum, ar);
//		}
//	}
//
//	// GET FNS /////////////////////////////////////////////////////////
//	////////////////////////////////////////////////////////////////////
//	
//	public float ar( AbstractNode node ) {
//		return node.getVal(ar);
//	}
//	
//	public String splitDir( AbstractNode node ) {
//		return node.getVal(splitDir);
//	}
//	
//	public static float ar(float w, float h) {
//		return w / h;
//	}
//
//	public static float hFromWAR(float wDim, float ar) {
//		return wDim / ar;
//	}
//
//	public static float wFromHAR(float hDim, float ar) {
//		return hDim * ar;
//	}
//
//	public static float areaFromWAR(float wDim, float ar) {
//		return wDim * (wDim / ar);
//	}
//
//	public static float areaFromHAR(float hDim, float ar) {
//		return hDim * ar * hDim;
//	}
//
//	public static float hFromAreaAR(float area, float ar) {
//		return (float) Math.sqrt(area / ar);
//	}
//
//	public static float wFromArea(float area, float ar) {
//		return area / hFromAreaAR( area, ar );
//	}
//	
////	public List<Boundary> getAsBoundaries( float x, float y, float w ){
////		
////		
////		return makeListRecursive( n ->{
////			if(!n.hasChildren() ) return new Boundary( x, y, w, hFromWAR( w, ar(n ) ) );
////			
////		}
////	}
//	
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
//
}
