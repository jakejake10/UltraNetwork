package unCore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.*;

/*
 * new treebuilder replacement class without all the generics
 * trying to simplify and make more useful
 * importing data, building empty trees, conversion?
 */

public interface Build {
	
	
	public static BiFunction<Integer,Integer,List<Integer>> distributeEven = (ct,buckets) -> {
		List<Integer> out = IntStream.range(0, buckets ).map(i -> (int)Math.floor(ct/buckets) ).boxed().collect(Collectors.toList());
		int lastInd = out.size()-1;
		int remainder = ct - out.get(0) * buckets;
		out.set(lastInd, out.get(lastInd) + remainder );
		return out;
	};
	
	public static <T extends TreeNodeStruct<T,D>,D> T importToLeavesBinary( T nodeIn, List<D> data ) {
		if( data.size() > 2 ) {
			List<List<D>> splitData = splitList( data );
			nodeIn.addChild();
			nodeIn.addChild();
			importToLeavesBinary( nodeIn.get(0), splitData.get(0) );
			importToLeavesBinary( nodeIn.get(1), splitData.get(1) );
		}
		else { // nodeIn.addChild( data );
			for( D curData : data ) {
				nodeIn.addChild();
				nodeIn.lastChild().setData( curData );
			}
		}
		return nodeIn;
	}
	
	public static <D> List<List<D>> splitList( List<D> input ){
		int groupSize = (int)Math.ceil( input.size()/2d );
		System.out.println( groupSize );
		List<List<D>> out = new ArrayList<>();
		out.add( input.subList( 0, groupSize ) );
		out.add( input.subList( groupSize, input.size() ) );
		System.out.println( "size: " + input.size() + ", halfSize: " + groupSize
				+ ", list1 size: " + out.get(0).size() + ", list2 size: " + out.get(1).size() );
		return out;
	}
	
//	public default void makeLeafCtBinary( N startNode, int leafCt ) {
//		makeLeafCtBinary( startNode, leafCt, n -> {} );
//	}
//	
//	public default void makeLeafCtBinary( N startNode, int leafCt, Consumer<N> nodeMod ) {
//		if( leafCt > 1 ) {
//			List<Integer> leafGroups = distributeEven.apply(leafCt, 2);
//			for( Integer i : leafGroups )  startNode.addChild();
//			for( int i = 0; i < leafGroups.size(); i++ ) makeLeafCtBinary( startNode.get(i), leafGroups.get(i), nodeMod );
//		}
//		nodeMod.accept(startNode);
//	}

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
	
	// CONVERT FNS? ////////////////////////////////////////////
	
	
	
}
