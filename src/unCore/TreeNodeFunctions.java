package unCore;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

import processing.core.PApplet;
import processing.data.*;
import utTypes.DataNode;

public final class TreeNodeFunctions {
	private TreeNodeFunctions() {}
	
	
	// DATA LIST FUNCTIONS //////////////////////////////////////////////////////////////
	
	public static <N extends TreeNodeObject<N> & Iterable<N>,E> List<E> initDataList( N node, E value ){
		return IntStream.range(0, node.getTotalSize()).mapToObj(i -> value ).collect(Collectors.toList() );
	}
	
	public static <N extends TreeNodeObject<N> & Iterable<N>,D> void applyDataList( N node, List<D> dataList, BiConsumer<N,D> fn ) {
		if( node.getTotalSize() != dataList.size() )
			  throw new UnsupportedOperationException("data list size not equal to node size" );
		node.traverseOperation( dataList, (dList,n) -> fn.accept(n,dList.get(n.getIndex())) );
	}
	
	public static <I,O> List<O> convertDataList( List<I> dataList, Function<I,O> convertFn ){
		return dataList.stream().map(i -> convertFn.apply(i) ).collect(Collectors.toList() );
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
	
	
	// JSON METHODS ///////////////////////////////////////////
	
	public static <E extends TreeNodeObject<E> & Iterable<E>> JSONExportBuilder<E> 
		buildFromJSON( String dir, BiConsumer<JSONObject,E> nodeBuildFn) {
		// need to add code for this
		return null;
	}
		
	public static class JSONExportBuilder<N extends TreeNodeObject<N> & Iterable<N>>{
	    N inputNode;
	    PApplet pa;
	    Map<String,Function<N,Object>> objectFields = new HashMap<>();
	    
	    public JSONExportBuilder( N inputNode, PApplet pa ){
	      this.inputNode = inputNode;
	      this.pa = pa;
	    }
	    
	    public JSONExportBuilder<N> addField( String name, Function<N,Object> fieldFn ){
	      objectFields.put( name, fieldFn );
	      return this;
	    }
	    
	    public JSONObject exportToJSONObject(){
	      return exportJSONRecursiveFn( inputNode );
	    }
	    public void export( String dir ){
	    	pa.saveJSONObject( exportToJSONObject(), dir );
		}
	    
	    // UTILITY FNS ///////////////////////////////////////////
	    
	    JSONObject exportJSONRecursiveFn( N node ){
	      JSONObject parObj = new JSONObject();
	      parObj.setJSONObject( "node fields", insertFields( node, new JSONObject() ) );
//	      if( node.hasChildren() ){
//	        JSONArray childArr = new JSONArray();
//	        for( int i = 0; i < node.getChildCount(); i++ ){
//	          childArr.setJSONObject( i, exportJSONRecursiveFn( node.get(i) ) );
//	        }
//	        parObj.setJSONArray( "children",childArr );
//	      }
	        JSONArray childArr = new JSONArray();
	        for( int i = 0; i < node.getChildCount(); i++ ){
	          childArr.setJSONObject( i, exportJSONRecursiveFn( node.get(i) ) );
	        }
	        parObj.setJSONArray( "children",childArr );
	        return parObj;
	    }
	    
	    JSONObject insertFields( N node, JSONObject obj ){
	      for( String curFieldName : objectFields.keySet() ){
	        Object curField = objectFields.get( curFieldName ).apply( node );
	        if( curField instanceof Integer ) obj.setInt( curFieldName, (int) curField );
	        else if ( curField instanceof Float ) obj.setFloat( curFieldName, (float)curField );
	        else obj.setString( curFieldName, curField.toString() );
	      }
	      return obj;
	    }
	    
	  }

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

	
}
