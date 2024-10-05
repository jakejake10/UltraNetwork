//package unCore;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.*;
//import java.util.stream.*;
//
//public interface TreeBuilder<N extends TreeNodeStruct<N,D>,D> {
//	
//	
//	public static BiFunction<Integer,Integer,List<Integer>> distributeEven = (ct,buckets) -> {
//		List<Integer> out = IntStream.range(0, buckets ).map(i -> (int)Math.floor(ct/buckets) ).boxed().collect(Collectors.toList());
//		int lastInd = out.size()-1;
//		int remainder = ct - out.get(0) * buckets;
//		out.set(lastInd, out.get(lastInd) + remainder );
//		return out;
//	};
//	
////	public <N extends TreeNodeStruct<N,?>> BiConsumer<N,Integer> buildBinary(){
////		return (n,i) -> { n.addChild(); n.addChild(); };
////	}
//	
//	
//	public static <E extends TreeNodeStruct<E,R>,R> void makeLeafCtBinary( E startNode, int leafCt ) {
//		makeLeafCtBinary( startNode, leafCt, n -> {} );
//	}
//	public static <E extends TreeNodeStruct<E,R>,R> void makeLeafCtBinary( E startNode, int leafCt, Consumer<E> nodeMod ) {
//		if( leafCt > 1 ) {
//			List<Integer> leafGroups = distributeEven.apply(leafCt, 2);
//			for( Integer i : leafGroups )  startNode.addChild();
//			for( int i = 0; i < leafGroups.size(); i++ ) makeLeafCtBinary( startNode.get(i), leafGroups.get(i), nodeMod );
//		}
//		nodeMod.accept(startNode);
//	}
//	
//	public default void makeLeafDataBinary( N startNode, List<D> data ) {
//		makeLeafDataBinary( startNode, data, n -> {} );
//	}
//	
//	public default <T extends N> void makeLeafDataBinary( T startNode, List<D> data, Consumer<N> nodeMod ) {
//		if( data.size() > 1 ) {
//			List<Integer> leafGroups = distributeEven.apply(data.size(), 2);
//			for( int i = 0; i < leafGroups.size(); i++ ) startNode.addChild();
//			for( int i = 0; i < leafGroups.size(); i++ ) {
//				int st = i == 0 ? 0 : i * leafGroups.get(0);
//				makeLeafDataBinary( startNode.get(i), data.subList(st, st+leafGroups.get(i) ),nodeMod );
//			}
//		}
//		else  startNode.setData( data.get(0) );
//		nodeMod.accept(startNode);
//	}
//	
////	public BiConsumer<N,List<D>> makeBinaryTree(){
////		Recursive<BiConsumer<N,List<D>>> op = new Recursive<>();
////		op.func = (n,list) ->{
////			if( list.size() > 1 ) {
////				int n1Children = (int)list.size()/2;
////				n.addChild();
////				n.addChild();
////				op.func.accept( n.get(0), list.subList( 0, n1Children ) );
////				op.func.accept( n.get(1), list.subList( n1Children, list.size() ) );
////			}
////			else n.setData( list.get(0) );
////		};
////		return op.func;
////	}
//	
//	
//	
//	
//	// CONVERT ///////////////////////////////////////////////
//	
////	public static <I extends TreeNodeStruct<I,X>,R extends TreeNodeStruct<R,Y>,X,Y> R convert ( Function<X,Y> convertFn ){
////		R 
////	}
//	
//	
//	/**
//	 * generic helper class to wrap functional interface
//     * https://stackoverflow.com/questions/19429667/implement-recursive-lambda-function-using-java-8
//     */
//	
//	public class Recursive<I> {
//	    public I func;
//	}
//	
//	
//	
//}
package oldFiles;


