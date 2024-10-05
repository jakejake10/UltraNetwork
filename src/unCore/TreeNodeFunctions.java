package unCore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

import processing.data.*;

public final class TreeNodeFunctions {
	private TreeNodeFunctions() {}
	
	// TREE BUILD FUNCTIONS /////////////////////////////////////////////////////////////
	/*
	 * set of static functions to generatively create a tree
	 * TODO: method to import a list of data to leafs, add simplified method to datanode
	 */
	public <E extends TreeNodeObject<E> & Iterable<E>> E buildRandomTree(E root, int maxChildCt, int maxDepth,
			Consumer<E> nodeMod) {
		root.traverseOperation((Supplier<Integer>) () -> (int)Math.floor(Math.random()* maxChildCt), (d, n) -> {
			if (n.getDepth() < maxDepth) {
				int cCount = d.get();
				for (int i = 0; i < cCount; i++) {
					n.addChild();
					nodeMod.accept(n.getLastChild());

				}
			}
		});
		return root;
	}

	public <E extends TreeNodeObject<E> & Iterable<E>> E buildUniformTree(E root, int childCt, int depth,
			Consumer<E> nodeMod) {
		root.traverseOperation((Supplier<Integer>) () -> childCt, (d, n) -> {
			if (n.getDepth() < depth) {
				int cCount = d.get();
				for (int i = 0; i < cCount; i++) {
					n.addChild();
					nodeMod.accept(n.getLastChild());
				}
			}
		});
		return root;
	}
	
	
	
	
	
	// JSON FUNCTIONS ////////////////////////////////////////////////////////////////////
	
	// STATIC METHODS TO BE USED WITH NODES //////////////////////////////////////////

	public static <E extends TreeNodeObject<E> & Iterable<E>> JSONExportBuilder<E> makeJSON(E inputNode) {
		return new JSONExportBuilder<E>(inputNode);
	}
	
	
	public static <E extends TreeNodeObject<E> & Iterable<E>> JSONExportBuilder<E> 
		buildFromJSON( String dir, BiConsumer<JSONObject,E> nodeBuildFn) {
		// need to add code for this
		return null;
	}
		
	static class JSONExportBuilder<N extends TreeNodeObject<N> & Iterable<N>>{
	    N inputNode;
	    Map<String,Function<N,Object>> objectFields = new HashMap<>();
	    
	    public JSONExportBuilder( N inputNode ){
	      this.inputNode = inputNode;
	    }
	    
	    public JSONExportBuilder<N> addField( String name, Function<N,Object> fieldFn ){
	      objectFields.put( name, fieldFn );
	      return this;
	    }
	    
	    public JSONObject export(){
	      return exportJSONRecursiveFn( inputNode );
	    }
	    
	    
	    public JSONObject exportJSONRecursiveFn( N node ){
	      JSONObject parObj = new JSONObject();
	      parObj.setJSONObject( "node fields", insertFields( node, new JSONObject() ) );
	      if( node.hasChildren() ){
	        JSONArray childArr = new JSONArray();
	        for( int i = 0; i < node.getChildCount(); i++ ){
	          childArr.setJSONObject( i, exportJSONRecursiveFn( node.get(i) ) );
	        }
	        parObj.setJSONArray( "children",childArr );
	      }
	      return parObj;
	    }
	    
	    public JSONObject insertFields( N node, JSONObject obj ){
	      for( String curFieldName : objectFields.keySet() ){
	        Object curField = objectFields.get( curFieldName ).apply( node );
	        if( curField instanceof Integer ) obj.setInt( curFieldName, (int) curField );
	        else if ( curField instanceof Float ) obj.setFloat( curFieldName, (float)curField );
	        else obj.setString( curFieldName, curField.toString() );
	      }
	      return obj;
	    }
	    
	  }


	
}
