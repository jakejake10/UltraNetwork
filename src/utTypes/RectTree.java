package utTypes;

import pFns_baseObjects.*;
import utCore.*;
import uvCore.CRObject;

import processing.core.PApplet;
import usPrimitives.RectObject;
import usPrimitives.Rectangular;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class RectTree extends AbstractTree<RectTree, RectNode> {
	public List<CRObject> crObjs;
	
	public float irregularity = 1f;	// 0 to 1
	Supplier<String> randSplitDir = () -> Math.random() < 0.5 ? "v" : "h";
	Function<String,String> opposite = s -> s.equals("v") ? "h" : "v";
	Consumer<RectNode> defaultSplit = n -> n.splitDir = n.hasParent() && Math.random() < irregularity ?
			opposite.apply( n.parent().splitDir) : randSplitDir.get();
	Consumer<RectNode> splitBehavior;
	

	public RectTree() {
		super();
		splitBehavior = defaultSplit;
//		crObjs.add(null);
	}

	public RectTree(float ar) {
		this();
//		root().myCrList = crObjs;
		root().ar = ar;
//		root().splitDir = "v";
		
	}

	public RectTree(RectObject rect) {
		this();
		root().ar = rect.ar();
		root().x = rect.x();
		root().y = rect.y();
		root().w = rect.w();
		root().h = rect.h();
//		root().myCrList = crObjs;
	}

	public RectNode nodeDefaultConstructor() {
		crObjs = new ArrayList<>();
		crObjs.add(null);
		return new RectNode( crObjs );
	}
	public RectTree getInstance() {
		return this;
	}

	// IMPORT FNS
	// ////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public void leafARGeneration(List<RectObject> dataIn, int... childCt) {
		int[] childCtNumber = childCt.length > 0 ? childCt : new int[] { 2 };
		generateTree( dataIn, childCtNumber );
		List<RectObject> dataStructured = dataIn.size() == size() ? dataIn : structureList(dataIn );
		root().applyThenToChildren(n -> n.setAR(n.getElem(dataStructured) != null ? n.getElem(dataStructured).ar() : 0 ));
		setSplitDir( splitBehavior );
		calcARFromLeafs();
	}
	
	// DATA FNS //////////////////////////////////////////////////////////////////////////////////////
	
	public void calcARFromLeafs() {
		root().bottomUpFn((n, list) -> {
			float sum = (float) list.stream().mapToDouble(a -> n.splitDir.equals("v") ? (double) a : (double) (1 / a)).sum();
			if (!n.splitDir.equals("v")) sum = 1 / sum;
			n.setAR(sum);
			return sum;
		}, n -> n.ar());
	}

	// MODIFICATION FNS //////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public void setSplitDir( Consumer<RectNode> dirFn ) { // bottom up fn to set v / h based on if child ar sum > or < target ar
		root().applyThenToChildren( n -> { if ( !n.isLeaf() ) dirFn.accept(n); } );
	}

	public void setDims(List<RectObject> dataIn) {
		root().applyThenToChildren(n -> Rectangular.positionFromTo(n.getElem(dataIn), n));
	}
	
	
	public void propagateXYW( float xIn, float yIn, float wIn ) {
		Rectangular.positionXYW( root(), xIn, yIn, wIn );
		System.out.println( "root: " + Rectangular.printDims( root() ) );
		root().transferDims();
	}
	
	// BUILDER FNS /////////////////////////////////////////////////////////////////////
	
	public void defineCR( float[] cols, float[] rows ) {
		root().subdivide("v", cols );
		for( RectNode n : root().children() ) n.subdivide("h", rows );
	}
	
	public RBuilder build( int maxDepth ) {
		return new RBuilder( maxDepth );
	}
	
	
	// DISPLAY /////////////////////////////////////////////////////////////////////////
	
	public void display( PApplet pa ) {
		for( RectNode n : leafs() ) n.display( pa );
	}
	
	
	
	
	
	
	// builder /////////////////////////
	
	public class RBuilder{
		List<Function<RectNode,Float[]>> splitActions = new ArrayList<>();
		int maxDepth;
		
		RBuilder( int maxDepth ){
			this.maxDepth = maxDepth;
		}
		
		public RBuilder addSplit( int count ){
			Float[] splitArr = new Float[count];
			for( int i = 0; i < count; i++ ) splitArr[i] = 1f;
			splitActions.add( n -> splitArr );
			return this;
		}
		
		public RBuilder addSplit( Function<RectNode,Integer> fn ){
			splitActions.add( n -> makeArr( fn.apply( n ) ) );
			return this;
		}
		
		public RBuilder addSplitPercent( Function<RectNode,Float[]> fn ){
			splitActions.add( fn );
			return this;
		}
		
		
		Float[] makeArr( int count ){
			Float[] out = new Float[count];
			for( int i = 0; i < count; i++ ) out[i] = 1f;
			return out;
		}
		
		public void make() {
			List<Function<RectNode,Float[]>> instructions = new ArrayList<>( splitActions );
			Function<RectNode,Float[]> curAction = instructions.size() > 1 ?
					instructions.remove(0) : instructions.get(0);
			int curDepth = 0;
			while( curDepth < maxDepth ) {
				for( RectNode n : root().leafs() ) {
					Float[] splitF = curAction.apply( n );
					float[] splits = new float[splitF.length];
					for( int i = 0; i < splits.length; i++ ) splits[i] = splitF[i];
					splitBehavior.accept(n);
					n.subdivide( n.splitDir, splits );
				}
				curAction = instructions.size() > 1 ?
						instructions.remove(0) : instructions.get(0);
				curDepth++;
			}
			
		}
		
		
		
		
	}
	
//	public class Split{
//		Function<RectNode,Float[]> splitAction;
//		
//		Split( int count ){
//			Float[] splitArr = new Float[count];
//			for( int i = 0; i < count; i++ ) splitArr[i] = 1f;
//			splitAction = n -> splitArr;
//		}
//		
//		Split( Function<RectNode,Integer> fn ){
//			splitAction = n -> makeArr( fn.apply( n ) );
//		}
//		
////		SplitInstruction( Function<RectNode,Float[]> fn ){
////			splitAction = fn;
////		}
//		
////		static SplitInstruction makeSI( int count ) {
////			return new SplitInstruction( count );
////		}
//		
//		Float[] makeArr( int count ){
//			Float[] out = new Float[count];
//			for( int i = 0; i < count; i++ ) out[i] = 1f;
//			return out;
//		}
//		
//		
//	}
	
}