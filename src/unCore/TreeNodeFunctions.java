package unCore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

import utTypes.DataNode;

//import processing.core.PApplet;
//import processing.data.*;
//import utTypes.DataNode;

public final class TreeNodeFunctions {
	private TreeNodeFunctions() {}
	
	
	// DATA LIST FUNCTIONS //////////////////////////////////////////////////////////////
	
	public static <N extends TreeNodeObject<N> & Iterable<N>,E> List<E> initDataList( N node, E value ){
		return IntStream.range(0, node.getTotalSize()).mapToObj(i -> value ).collect(Collectors.toList() );
	}

	
	// NEW METHODS ////////////////////////////////////////////////////////////////////////
	
	
	
	
	// TREE BUILDER FUNCTIONS /////////////////////////////////////////////////////////////
	
	/*
	 * builder class created by buildTree() method in TreeNodeObject
	 * takes in node instance in constructor, used for make method
	 * bullder pattern chains methods to cusomize output
	 * make() method creates tree from root and function parameters	 * 
	 */
	
	
	public static class TreeBuilder<N extends TreeNodeObject<N> & Iterable<N>> {
		BiFunction<N,Integer,Integer> childCtModFn = (n,i) -> i;
		BiFunction<N,Integer,Integer> maxDepthModFn = (n,i) -> i;
		Consumer<N> preGenModFn = n -> {};
		Consumer<N> postGenModFn = n -> {};
		BiFunction<N,Integer,Integer> leafCtModFn = (n,i) -> i;
		N root;
		int myChildCt = -1;
		int myMaxDepth = -1;
		int myLeafCt = -1;
		
		
		public TreeBuilder( N root ) {
			this.root = root;
		}
		
		public TreeBuilder<N> setChildCtMod( BiFunction<N,Integer,Integer> fn ) {
			this.childCtModFn = fn;
			return this;
		}
		public TreeBuilder<N> setMaxDepthMod( BiFunction<N,Integer,Integer> fn ) {
			this.maxDepthModFn = fn;
			return this;
		}
		public TreeBuilder<N> setLeafCtMod( BiFunction<N,Integer,Integer> fn ) {
			this.leafCtModFn = fn;
			return this;
		}
		
		public TreeBuilder<N> setPreGenMod( Consumer<N> modFn ) {
			this.preGenModFn = modFn;
			return this;
		}
		public TreeBuilder<N> setPostGenMod( Consumer<N> modFn ) {
			this.postGenModFn = modFn;
			return this;
		}
		
		public TreeBuilder<N> setLeafCt( int leafCt ) {
			this.myLeafCt = leafCt;
			return this;
		}
		public TreeBuilder<N> setChildCt( int childCt ) {
			this.myChildCt = childCt;
			return this;
		}
		public TreeBuilder<N> setMaxDepth( int maxDepth ) {
			this.myMaxDepth = maxDepth;
			return this;
		}
		
		public TreeBuilder<N> setChildCtByDepth( int...depthCts ) {
			this.myMaxDepth = depthCts.length;
			this.myChildCt = depthCts[0];
			setChildCtMod( (n,i) -> {
				int d = n.getDepth();
				if( d < depthCts.length-1 ) return depthCts[d+1];
				return i;
			});
			return this;
		}
		
				
		public void make() {
			if( myLeafCt < 0 && myChildCt < 0 ) throw new UnsupportedOperationException("need to have either leaf or child ct assigned");
			preGenModFn.accept(root);
			make( root, new int[] {myLeafCt,myChildCt,myMaxDepth});
			postGenModFn.accept(root);
		}
		
		public void make( N node, int[] lcd ) {
			if( lcd[2] > 0 && node.getDepth() >= maxDepthModFn.apply(node, lcd[2] )) return;
			int[] cldCts = null;
			// cldCts become leaf counts used for subdivision
			if( lcd[0] > 0 ) cldCts = partitionNumber( leafCtModFn.apply(node, lcd[0]), childCtModFn.apply(node, lcd[1] ) ); 
			else {
				//cldCts become number of children
				cldCts = new int[lcd[1]];
				for( int i = 0; i < cldCts.length; i++ ) cldCts[i] = childCtModFn.apply(node, lcd[1] );
			}
			for( int i : cldCts ) {
				if( i == 0 ) return;
				N child =  node.defaultConstructor();
				preGenModFn.accept(child);
				node.addChild();
				int[] newData = null;
				if( lcd[0]> 0) newData = new int[] {i,lcd[1],lcd[2]};
				else           newData = new int[] {lcd[0],i,lcd[2]};
				if( myLeafCt < 0 || i > 1 ) 
					make( node.getLastChild(), newData );
				postGenModFn.accept(node.getLastChild());
			}
		}
		
	}
	
	
	
	// IO FUNCTIONS ////////////////////////////////////////////////////////////////////
	
		// STATIC METHODS TO BE USED WITH NODES //////////////////////////////////////////
		
		public static DataNode<File> buildFromDirectory(String dir) {
			File file = new File(dir);
			DataNode<File> out = new DataNode<>(file);
			buildRecursive(out);
			return out;
		}

		public static void buildRecursive(DataNode<File> node) {
			for (File file : node.getData().listFiles()) {
				node.addChildWithData(file);
				if (file.isDirectory())
					buildRecursive(node.getLastChild()); // Calls same method again.
			}
		}
	
	
	
	
	
	
	
	
	
	
	
	
	// PROCESSING CANVAS RENDERING //////////////////////////////////
	
	

	// CALCULATIONS /////////////////////////////////////////////////
	
	public static int[] partitionNumber( int number, int partCt ) {
		int[] out = new int[partCt];
		for( int i = 0; i < number; i++ ) out[i % out.length]++; 
		return out;
	}
	
	public void makeTree( DataNode<Integer> node, int leafCt, int maxChildCt ) {
		int[] leafCts = partitionNumber( leafCt, maxChildCt );
		for( int i : leafCts )
			if( i == 0 ) return;
			else if( i == 1 ) node.addChild( 0 );
			else {
				node.addChild();
				makeTree( node.getLastChild(), i, maxChildCt );
			}
		}
	
	
	// PREDICATES ///////////////////////////////////////////////
	
	public static <N extends TreeNodeObject<N> & Iterable<N>>
	Predicate<N> withParentInversion(
	        Predicate<N> basePredicate,
	        boolean invertOnOddParent,
	        boolean rootValue
	) {
	    return node -> {
	        if (node == null) return false;
	        if (node.isRoot()) return rootValue;

	        boolean value = basePredicate.test(node);

	        if (!invertOnOddParent) return value;

	        boolean invert = false;
	        N cur = node.getParent();

	        while (cur != null && !cur.isRoot()) {
	            if ( (cur.getIndexInParent() & 1) == 1 )
	                invert = !invert;
	            cur = cur.getParent();
	        }

	        return invert ? !value : value;
	    };
	}


	
}
