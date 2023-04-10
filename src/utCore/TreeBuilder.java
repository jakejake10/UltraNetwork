package utCore;

import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;





//abstract public class TreeBuilder<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>,M extends TreeBuilder<T,N,M,V>, V>  {
abstract public class TreeBuilder<T extends AbstractTree<T,N>, N extends AbstractNode<T,N>, V>  {
	public V data;
	public Predicate<N> growth = n -> true; // = n -> n.depth < 5;
	public Consumer<N> modifyFn  = n -> {};
	public Function<N,Function<N,Integer>> childCount = n -> n2 -> 0;
	public Consumer<N> addChildFn  = n -> { for( int i = 0; i < childCount.apply( n ).apply(n); i++ ) n.addChild(); };
	public BiConsumer<N,Stack<N>> nextFn = (n,s) -> n.children().forEach( c -> s.push(c) );
	public Consumer<N> leafFn = n -> {};
	public N startNode;
	
	
	public TreeBuilder( N startNode ) {
		this.startNode = startNode;
	}
	
	public TreeBuilder( N startNode, V data ) {
		this.startNode = startNode;
		this.data = data;
	}
	
	// ABSTRACT FNS /////////////////////////////////////////////////////
	
//	abstract public M getInstance();
	abstract public TreeBuilder<T,N,V> getInstance();
//	abstract public <E, R extends TreeBuilder<T,N,M,E>> R typeConstructor( E data);
	abstract public <E> TreeBuilder<T,N,E> typeConstructor();
//	abstract public N root();
	
//	public <E, R extends TreeBuilder<T,N,M,E>> R setData( E data ){
//	public <E> E setData( E data ){
//		R out =  typeConstructor( data );
//		out.data = data;
//		return out;
//	}
	
	public TreeBuilder<T,N,V>  maxDepth( Function<N,Integer> maxDepthIn ) {
		growth = growth.and( n -> n.depth < maxDepthIn.apply( n ) );
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  maxDepth( Supplier<Integer> maxDepthIn ) {
		growth = growth.and( n -> n.depth < maxDepthIn.get() );
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  maxDepth( int maxDepthIn ) {
		growth = growth.and( n -> n.depth < maxDepthIn );
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  appendMod( Consumer<N> fn ) {
		modifyFn = modifyFn.andThen(fn);
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  nextFn( BiConsumer<N,Stack<N>> fn ) {
		nextFn = fn;
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  addChildFn( Consumer<N> fn ) {
		addChildFn = fn;
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  childCount( int count ) {
		childCount = n -> nf -> count;
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  childCount( Function<N,Integer> countFn ) {
		childCount = n -> countFn;
		return getInstance();
	}
	
	public TreeBuilder<T,N,V>  childCount( Supplier<Integer> countFn ) {
		childCount = n -> n2 -> countFn.get();
		return getInstance();
	}
	
	/////////////////////////////////////////////////////
	
	
	
	public void applySubFns() {	// add required methods from subclass, that cannot be added in constructor
	}
	

	public void generate(  ) {	// if already has children, move to children without running fn?
		applySubFns();
		Stack<N> nextNodes = new Stack<>();
		nextNodes.push( startNode );
		
		while( !nextNodes.isEmpty() ) {
			N targetNode = nextNodes.pop();
			if( !growth.test( targetNode ) ) continue;
			modifyFn.accept( targetNode );
			addChildFn.accept(targetNode);
			if( targetNode.getChildCount() == 0 ) leafFn.accept( targetNode );
			nextFn.accept( targetNode, nextNodes );
		}
	}


	
	
//	@FunctionalInterface
//	interface BuilderInput<>
}
