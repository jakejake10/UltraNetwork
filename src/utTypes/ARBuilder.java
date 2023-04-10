//package utTypes;
//
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
//import utCore.*;
//
//public class ARBuilder<V> extends TreeBuilder3<ARTree, ARNode,ARBuilder<V>,V> {
////	public Function<ARNode, String> splitDirFn =  n -> Math.random() < 0.5 ? "h" : "v";
//
//	
//	public ARBuilder( ARNode startNode ) {
//			super( startNode );
//			addChildFn = (n, count) ->{ 
//				n.subdivide( count );
//				System.out.println( "par = " + n );
//				for( ARNode arn : n.children() ) System.out.println( "child = " + arn );
//			};
//		}
//
//	public ARBuilder( ARNode startNode, V data) {
//			this ( startNode );
//			this.data = data;
//		}
//
//	// abstract fns ////////////////////////
//
////		public <E> ARTreeBuilder<E> typeConstructor( E data ){
////			return new ARTreeBuilder<E>();
////		}
////	public <E> TreeBuilder2<ARTree, ARNode, ARBuilder<V>, E> typeConstructor() {
//	public <E> ARBuilder<E> typeConstructor() {
//		return new ARBuilder<E>( startNode );
//	}
//
//	
//	public ARBuilder<V> getInstance() {
//		return this;
//	}
//
//	// BUILDER FNS ////////////////////////////////////////////////////
//
//	public ARBuilder<V> splitDirFn(Function<ARNode, String> fn) {
////		splitDirFn = fn;
//		return this;
//	}
//
//	// PRESETS ////////////////////////////////////////////////////////
//
////	public ARBuilder<V> rowCol( Function<ARNode, Integer> rows, Function<ARNode, Integer> cols) {
////		splitDirFn = n -> n.splitDir = n.hasParent() ? "v" : "h";
////		return addChildCount( (n, data,count) -> cols.apply(n), n -> n.hasParent() )
////			.addChildCount( (n, data, count) -> rows.apply(n), n -> !n.hasParent() )
//////			.addMod( n -> n.splitDir = n.hasParent() ? "v" : "h" )
////			.maxDepth(2);
////	}
//	
//	
//	public ARBuilder<V> rows( Function<ARNode, Integer> rows, int level ){
//		maxDepth = n ->  n.depth < 2;
//		return addChildCount( (n, data, count) -> rows.apply(n), n -> n.depth == level )
//				.addMod( n -> n.splitDir =  "h", n -> n.depth == level );
//	}
//	
//	public ARBuilder<V> rows( BiFunction<ARNode,V, Integer> rows, int level ){
//		maxDepth = n ->  n.depth < 2;
//		return addChildCount( (n, data, count) -> rows.apply(n,data), n -> n.depth == level )
//				.addMod( n -> n.splitDir =  "h", n -> n.depth == level );
//	}
//	
//	public ARBuilder<V> cols( Function<ARNode, Integer> cols, int level ){
//		maxDepth = n ->  n.depth < 2;
//		return addChildCount( (n, data, count) -> cols.apply(n),  n -> n.depth == level  )
//				.addMod( n -> n.splitDir =  "v", n -> n.depth == level );
//	}
//	
//	public ARBuilder<V> cols( BiFunction<ARNode,V, Integer> cols, int level ){
//		maxDepth = n ->  n.depth < 2;
//		return addChildCount( (n, data, count) -> cols.apply(n,data),  n -> n.depth == level  )
//				.addMod( n -> n.splitDir =  "v", n -> n.depth == level );
//	}
//	
////	public ARBuilder<V> colsAligned(){
////		return this.addMod( n -> n.splitDir = !n.hasParent() ? "v" : "h" );
////	}
////	public ARBuilder<V> rowsAligned(){
////		return this.addMod( n -> n.splitDir = !n.hasParent() ? "h" : "v" );
////	}
//	
//	
//
//	// OTHER /////////////////////////////////////////////////////////
//
//	public void applySubFns() {
////		appendMod(n -> n.splitDir = splitDirFn.apply(n));
////		addMod( n -> n.splitDir = splitDirFn.apply(n));
//	}
//
//}
