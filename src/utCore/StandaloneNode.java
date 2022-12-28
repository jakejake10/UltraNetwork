package utCore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.*;
//import java.util.stream.*;
import java.lang.reflect.*;



public abstract class StandaloneNode<T extends StandaloneNode<T>>{
	public T parent;
	public List<T> children;
	public int depth = -1;
	
	
	public StandaloneNode(){
		checkType();
		this.depth = 0;
	}
	
	public StandaloneNode( T parent ){
		checkType();
		this.parent = parent;
		this.depth = hasParent() ? parent.depth+1 : 0;
	}
	
	
	
	// ABSTRACT METHODS ///////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	
	abstract public T defaultConstructor();
//	abstract T castNode( Node nodein );
//	abstract public <T extends Node> T getParent();
//	abstract public <T extends Node> void setParent( T parent );
//	abstract public <T extends Node> List<T> getChildren();
//	abstract public <T extends Node> T getThis();
	
	
	// GET FNS ////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	
	public T getInstance() {
		@SuppressWarnings("unchecked")
		T thisIsT = (T) this;
		return thisIsT;
	}
	  
	public T get(int index) {
		if ( !hasChildren() )
			throw new NullPointerException();
		return children.get(index);
	}
	
	
	public T getRoot() {
		return !hasParent() ? getInstance() : parent.getRoot();
	}
	
	public int depth() {
		return depth;
	}
	
	public int maxDepth() {
		int maxDepth = 0;
		for( T n : bft() ) if( n.depth() > maxDepth) maxDepth = n.depth();
		return maxDepth;
	}

	
	
	// NODE FNS /////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	public void addChild( T node ) {
		addChild(0,node);
	}
	public void addChild( int index, T node ) {
		if (children == null) children = new ArrayList<>();
		node.depth = depth+1;
		children.add( index, node );
	}
		
	public void addChild( List<T> nodesIn ){
		addChild( 0, nodesIn );
	}
	public void addChild( int index, List<T> nodesIn ){
		for( T node : nodesIn ) addChild( index, node );
	}
	
	public void addChild( T... nodesIn ) {
		addChild( hasChildren() ? children.size() - 1 : 0, nodesIn);
	}
	
	public void addChild(int index, T... nodesIn) {
		for( T node : nodesIn ) addChild( index, node );
	}

	public void subtractChild(int index) {
		children.remove(index);
		if (children.size() == 0)
			children = null;
	}
	
	
	// TREE TRAVERSAL ///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	public void leafOperation(Consumer<T> operation ) { // perform lambda only on leaf children
		for( T n : dft() ) if( !n.hasChildren() ) 
			operation.accept( n );
	}
	
	public void leafOperation(Consumer<T> operation, Iterable<T> it ) { // perform lambda only on leaf children
		for( T n : it ) if( !n.hasChildren() ) operation.accept( n );
	}
	
	public void nodeOperation(Consumer<T> operation ) { // perform lambda only on leaf children
		for( T n : dft() ) operation.accept( n  );
	}
	
	public void nodeOperation(Consumer<T> operation, Iterable<T> it ) { // perform lambda only on leaf children
		for( T n : it ) operation.accept( n );
	}
	
//	public void copyTree( T input ) {
//		children.clear();
//		copyChildren( input );
//	}
//	
//	public void copyChildren( T input ) {
//		if( !input.hasChildren() ) return;
//		for( T n : input.children) {
//			T curNode = defaultConstructor();
//			curNode.copyChildren(n);
//			addChild( curNode );
//		}
//	}
	
//	public void createCopy( T inputNode ) {
//		T n = defaultConstructor();
//		copyNodeFromTo( inputNode, n );
//	}
//	
//	public void copyNodeFromTo( T from, T to ) {
//		if( !from.hasChildren() ) return;
//		for( int i = 0; i < from.children.size(); i++ ) {
//			to.addChild( to.defaultConstructor() );
//			copyNodeFromTo( from.get(i), to.get(i) );
//		}
//	}
	
//	public T createCopy() {
//		T out = defaultConstructor();
//		copyChildren( out );
//		return out;
//	}
//	
//	public void copyChildren( T parentIn ) {
//		if( !parentIn.hasChildren() ) return;
//		for( T node : parentIn.children ) {
//			parentIn.addChild( defaultConstructor() );
//			copyChildren( node );
//		}
//	}
//	
//	public <E extends Node<E>> T generateFromImport( E inputNode ) {
//		T out = defaultConstructor();
//		copyNodeFromTo( inputNode, out );
//		return out;
//	}
//	
//	public <E extends Node<E>> void copyNodeFromTo( E from, T to ) {
//		if( !from.hasChildren() ) return;
//		for( int i = 0; i < from.children.size(); i++ ) {
//			to.addChild( to.defaultConstructor() );
//			copyNodeFromTo( from.get(i), to.get(i) );
//		}
//	}
	
	public <E extends StandaloneNode<E>> T convertFrom( E inputNode, Function<E,T> converter ) {
		T out = defaultConstructor();
		copyNodeFromTo( inputNode, out, converter );
		return out;
	}
	
	public <E extends StandaloneNode<E>> void copyNodeFromTo( E from, T to, Function<E,T> converter ) {
		if( !from.hasChildren() ) return;
		for( int i = 0; i < from.children.size(); i++ ) {
			to.addChild( converter.apply(from) );
			copyNodeFromTo( from.get(i), to.get(i), converter );
		}
	}
	
//	public <C extends Node<C>> Node<C> copyTree( Node<C> input ){	// copy input's tree structure
//		
//	}
	
	
	
	// CHECKS ///////////////////////////////////////////////////////
	
	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	private void checkType() {
		try {		// if type is parametrized, will throw exception
		final Class<?> t = ( Class<?> ) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		if( !t.isInstance( this ) ) throw new IllegalArgumentException();
		} catch( Exception e ) {}
	}
	
	
	
	// ITERABLES ////////////////////////////////////////////////////
	
	
	public void iterateFn( Iterable<T> traverse, Consumer<T> nodeFn ) {
		while( traverse.iterator().hasNext() ) nodeFn.accept( traverse.iterator().next() );
	}
	
	public <E> void iterateFn( Iterable<T> traverse, BiConsumer<T,E> nodeFn, E inputVal ) {
		while( traverse.iterator().hasNext() ) nodeFn.accept( traverse.iterator().next(),inputVal );
	}
	
	
	public Iterable<T> bft() {
		Iterator<T> it = new Iterator<T>() {
			public Queue<T> traversal = new LinkedList<>(Arrays.asList(getInstance()));

			@Override
			public boolean hasNext() {
				return !traversal.isEmpty();
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				T n = traversal.poll();
				if (n.hasChildren())
					for (int i = n.children.size()-1; i >= 0; i--)
						traversal.add(n.get(i)); // reverse this?
				return n;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return it;
			}
		};
	}

	public Iterable<T> dft() {
		Iterator<T> it = new Iterator<T>() {
			public Stack<T> traversal = new Stack<>();
			boolean started = false;

			@Override
			public boolean hasNext() {
				if (!started) {
					traversal.push(getInstance());
					started = true;
				}
				return !traversal.isEmpty();
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				T n = traversal.pop();
				if (n.hasChildren())
					for ( int i = n.children.size()-1; i >= 0; i-- )
						traversal.push(n.get(i)); // reverse order?
				return n;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

		return new Iterable<T>() {
			public Iterator<T
			> iterator() {
				return it;
			}
		};
	}
	
//	public String printOperation() {
//		return printOperation( createPrintFn( n -> "node" ) );
//	}
	
	public String printOperation( Function<T,?> fn ) {
		String out = "";
		for( T n : dft() ) out += createPrintFn( fn ).apply(n).toString() + "\n";
		return out;
	}
	
//	Function<T,String> basicPrint = n -> new String(new char[depth()]).replace('\0', ' ')+"node";
	
	public Function<T,String> createPrintFn( Function<T,?> addedString ){
		return n -> new String( new char[n.depth()]).replace( '\0', ' ' ) + addedString.apply(n).toString();
	}
	
	
//	public <R> Node<R> makeDataTree( Function<T,R> translateFn ){
//		return makeTreeFn( null, translateFn );
//	}
//	public <R> Node<R> makeTreeFn( Node<R> parent, Function<T,R> translateFn ){
//		Node<R> out = null;
//		if( !hasParent() ) out = new Node<R>( parent, translateFn.apply(getInstance()) );
//		else 			   out = new Node<R>( parent, translateFn.apply(getInstance()) );
//		if( out != null && hasChildren() ) for( StandaloneNode<T> n : children ) n.makeTreeFn( out, translateFn );
//		return out;
//	}
	
	// FIELDS ////////////////////////////////////
	
	public String toString() {
		return printOperation( createPrintFn( n -> "node" ) );
	}
	
	
	
}



