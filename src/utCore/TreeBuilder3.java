package utCore;

import java.util.Stack;
import java.util.function.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





abstract public class TreeBuilder3<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>,M extends TreeBuilder3<T,N,M,V>, V>  {
//abstract public class TreeBuilder2<T extends AbstractTree<T,N>, N extends AbstractNode<T,N>, V>  {
	public V data;
	public Predicate<N> maxDepth = n -> true; // = n -> n.depth < 5;
	public BiFunction<N,V,Integer> childCount = (n,v) -> 0;
	public BiConsumer<N,Integer> addChildFn = ( n, j ) -> { for( int i = 0; i < j; i++ ) n.addChild(); };
	public BiConsumer<N,V> addDataFn = ( n, data ) -> {};
	public BiConsumer<N,Stack<N>> nextFn = (n,s) -> n.children().forEach( c -> s.push(c) );
	public Consumer<N> leafFn = n -> {};
	public N startNode;
	
//	List<BiFunction<N,Integer,Integer>> countFns = new ArrayList<>();
	List<TriFunction<N,V,Integer,Integer>> countFns = new ArrayList<>();
	List<Predicate<N>> countConditions = new ArrayList<>();
	List<Consumer<N>> modifyFns = new ArrayList<>();
	List<Predicate<N>> modifyConditions = new ArrayList<>();
	
	
	
	
	public TreeBuilder3( N startNode ) {
		this.startNode = startNode;
	}
	
	public TreeBuilder3( N startNode, V data ) {
		this( startNode );
		this.data = data;
	}
	
	// ABSTRACT FNS /////////////////////////////////////////////////////
	
//	abstract public M getInstance();
	abstract public M getInstance();
//	abstract public <E, R extends TreeBuilder<T,N,M,E>> R typeConstructor( E data);
	abstract public <E> TreeBuilder3<T,N,?,E> typeConstructor();
//	abstract public N root();
	
//	public <E, R extends TreeBuilder<T,N,M,E>> R setData( E data ){
//	public <E> E setData( E data ){
//		R out =  typeConstructor( data );
//		out.data = data;
//		return out;
//	}
	
	// FN ADDING ////////////////////////////////////////////////////
	
	public M addMod( Consumer<N> fn, Predicate<N> condition ) {
		modifyFns.add( fn );
		modifyConditions.add(condition);
		return getInstance();
	}
	
	public M addMod( Consumer<N> fn ) {
		modifyFns.add( fn );
		modifyConditions.add( n -> true );
		return getInstance();
	}
	//BiFunction<N,Integer,Integer>
	public M addChildCount( TriFunction<N,V,Integer,Integer> fn, Predicate<N> condition ) {
		countFns.add( fn );
		countConditions.add( condition );
		return getInstance();
	}
	
	public M addChildCount( TriFunction<N,V,Integer,Integer> fn ) {
		countFns.add( fn );
		countConditions.add( n -> true );
		return getInstance();
	}
	
	public M addDataFn( BiConsumer<N,V> fn ) {
		addDataFn = fn;
		return getInstance();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	
	
	public M  maxDepth( Function<N,Integer> maxDepthIn ) {
		maxDepth = maxDepth.and( n -> n.depth < maxDepthIn.apply( n ) );
		return getInstance();
	}
	
	public M  maxDepth( Supplier<Integer> maxDepthIn ) {
		maxDepth = maxDepth.and( n -> n.depth < maxDepthIn.get() );
		return getInstance();
	}
	
	public M  maxDepth( int maxDepthIn ) {
		maxDepth = maxDepth.and( n -> n.depth < maxDepthIn );
		return getInstance();
	}

	
	public M  nextFn( BiConsumer<N,Stack<N>> fn ) {
		nextFn = fn;
		return getInstance();
	}
	
	public M  addChildFn( BiConsumer<N,Integer> fn ) {
		addChildFn = fn;
		return getInstance();
	}
	
	public M  childCount( int count ) {
		childCount = ( n, data ) -> count;
		return getInstance();
	}
	
	public M  childCount( BiFunction<N,V,Integer> countFn ) {
		childCount = countFn;
		return getInstance();
	}
	
	public M  childCount( Supplier<Integer> countFn ) {
		childCount = (n,data) -> countFn.get();
		return getInstance();
	}
	
	
	
//	public M  childCount( int count ) {
//		childCount = n -> nf -> count;
//		return getInstance();
//	}
//	
//	public M  childCount( Function<N,Integer> countFn ) {
//		childCount = n -> countFn;
//		return getInstance();
//	}
//	
//	public M  childCount( Supplier<Integer> countFn ) {
//		childCount = n -> n2 -> countFn.get();
//		return getInstance();
//	}
	
	/////////////////////////////////////////////////////
	
	
//	public <E> Function<N,E> 
	
	
	
	
	/////////////////////////////////////////////////////
	
	
	public void applySubFns() {	// add required methods from subclass, that cannot be added in constructor
	}
	

	public M generate(  ) {	// if already has children, move to children without running fn?
		applySubFns();
		Stack<N> nextNodes = new Stack<>();
		nextNodes.push( startNode );
		
		while( !nextNodes.isEmpty() ) {
			N targetNode = nextNodes.pop();
			if( !maxDepth.test( targetNode ) ) continue;
			
			for( int i = 0; i < modifyFns.size(); i++ )
				if( modifyConditions.get(i).test(targetNode) ) modifyFns.get(i).accept(targetNode);
			
			int count = 0;
			for( int i = 0; i < countFns.size(); i++ )
				if( countConditions.get(i).test(targetNode) ) {
					int fnIndex = i;
					count = countFns.get(fnIndex).apply(targetNode, data, count );
				}
			System.out.println( "generated count = " + count );	
			addChildFn.accept( targetNode, count );
			addDataFn.accept( targetNode, data );
			if( targetNode.getChildCount() == 0 ) leafFn.accept( targetNode );
			nextFn.accept( targetNode, nextNodes );
		}
		return getInstance();
	}
	
	public V getData() {
		return data;
	}


	
	
	@FunctionalInterface
	public interface TriFunction<L,K,B,R>{
		public R apply( L input1, K input2, B input3 );
	}
}
