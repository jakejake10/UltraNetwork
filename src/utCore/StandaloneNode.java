package utCore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;




public abstract class StandaloneNode<N extends StandaloneNode<N>> implements Iterable<N> {
	public N parent;
	public List<N> children;
	public int depth = -1;
	public int index = -1;
	
	
	public StandaloneNode(){
//		checkType();
		this.depth = 0;
		this.index = 0;
	}
	
//	public StandaloneNode( T parent ){
////		checkType();
//		this.parent = parent;
//		this.depth = hasParent() ? parent.depth+1 : 0;
//	}
	
	
	
	// ABSTRACT METHODS ///////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	
	abstract public N defaultConstructor();
	abstract public N getInstance();
	
	
	// GET FNS ////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	
	
//	  
//	public T get( int index ) {
//		if ( !hasChildren() )
//			throw new NullPointerException();
//		return children.get(index);
//	}
//	
//	
//	public T getRoot() {
//		return !hasParent() ? getInstance() : parent.getRoot();
//	}
	
	
	
	
	// CHECK FNS ///////////////////////////////////////////////////////////////////////////

	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
		
	public boolean isLeaf() {
		return !hasChildren();
	}

	public boolean isRoot() {
		return !hasParent();
	}

	// GET FNS ////////////////////////////////////////////////////////////////

	public N get(int index) {
		return children.get( index );
	}

	public N parent() {
		return parent;
	}

	public int indexInParent() {
		if (!hasParent())
			return -1;
		else
			return index;
	}

	public N getRoot() {
		return hasParent() ? parent() : getInstance();
	}

	public N lastChild() {
		return children.get( children.size() - 1 );
	}

	public int depth() {
		return depth;
	}

	public int maxDepth() {
		int maxDepth = 0;
		for (N n : bft())
			if (n.depth() > maxDepth)
				maxDepth = n.depth();
		return maxDepth;
	}

	public List<N> getChildren() {
		return hasChildren() ? children : new ArrayList<N>();
	}

	public List<N> getLeafs() {
		return getLeafs( new ArrayList<N>() );
	}

	public List<N> getLeafs(List<N> data) {
		if ( !hasChildren() ) data.add(getInstance());
		else for ( N node : children ) node.getLeafs(data); // can't use iterable if used in iterable class?
		return data;
	}
	
	public List<N> getDepth( int targetLevel ){
		return getDepth( new ArrayList<N>(), targetLevel );
	}
	
	public List<N> getDepth( List<N> data, int targetLevel ){
		if( depth == targetLevel ) data.add( getInstance() );
		if( depth < targetLevel ) 
			for( N node : children ) node.getDepth( data, targetLevel );           // can't use iterable if used in iterable class?
		return data;
	}

	public int getChildCount() {
		return children != null ? children.size() : 0;
	}

	public int getLeafCount() {
		return getLeafs().size();
	}

	public int size() {
//		int out = 0;
//		for ( N n : this) out++;
//		return out;
		return children.size(); // modified from previous lines
	}



	

	// LIST FNS /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	
	
	
	// NODE FNS /////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	public N addChild() {
		N node = defaultConstructor();
		return addChild( node );
	}
	
//	public T addChild( T node ) {	
//		int pos = children == null ? 0 : getChildCount();
//		return addChild( pos, node );
//	}
	
	public N addChild( N node, int...index ) {	// returns instance for chainable methods
		if (children == null) children = new ArrayList<>();
		int pos = index.length > 0 ? index[0] : getChildCount();
//		node.depth = depth+1;
		node.parent = getInstance();
		node.index = children.size();
		node.increaseDepthRecursive();	// incase added node has its own children
		children.add( pos, node );
		return getInstance();
	}
	
	public N removeChild(int index) {
		children.remove(index);
		if (children.size() == 0) children = null;
		return getInstance();
	}
	
	void increaseDepthRecursive() {
		depth = hasParent() ? parent.depth + 1 : 0;
		if( hasChildren() )
			for( N n : this ) n.increaseDepthRecursive();
	}
	
	// ORDER CHANGE ////////////////////////////////////////
	
	public void reverse() {
		Collections.reverse( children );
	}
	
	public void shuffle() {
		Collections.shuffle( children );
	}
	
	public List<Integer> getOrder() {
		return IntStream.range( 0, size() ).map( i -> get(i).index ).boxed().collect( Collectors.toList() );
	}
	
	public void resetOrder() {
		IntStream.range( 0, size() ).forEach( i -> get(i).index = i );
	}
	
	
	
	// TREE TRAVERSAL ///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	public void leafOperation(Consumer<N> operation ) { // perform lambda only on leaf children
		for( N n : dft() ) if( !n.hasChildren() ) 
			operation.accept( n );
	}
	
	public void leafOperation(Consumer<N> operation, Iterable<N> it ) { // perform lambda only on leaf children
		for( N n : it ) if( !n.hasChildren() ) operation.accept( n );
	}
	
	public void depthOperation( int targetDepth, Consumer<N> operation ) { // perform lambda only on leaf children
		for( N n : bft( n -> n.depth < targetDepth ) ) operation.accept( n );
	}
	
	public void nodeOperation(Consumer<N> operation ) { // perform lambda only on leaf children
		for( N n : dft() ) operation.accept( n  );
	}
	
	public void nodeOperation(Consumer<N> operation, Iterable<N> it ) { // perform lambda only on leaf children
		for( N n : it ) operation.accept( n );
	}
	
	
	public <E extends StandaloneNode<E>> N convertFrom( E inputNode, Function<E,N> converter ) {
		N out = defaultConstructor();
		copyNodeFromTo( inputNode, out, converter );
		return out;
	}
	
	public <E extends StandaloneNode<E>> void copyNodeFromTo( E from, N to, Function<E,N> converter ) {
		if( !from.hasChildren() ) return;
		for( int i = 0; i < from.children.size(); i++ ) {
			to.addChild( converter.apply(from) );
			copyNodeFromTo( from.get(i), to.get(i), converter );
		}
	}
	
	
	// CHECKS ///////////////////////////////////////////////////////
	
	
	
//	private void checkType() {
//		try {		// if type is parametrized, will throw exception
//		final Class<?> t = ( Class<?> ) ((ParameterizedType) getClass().getGenericSuperclass())
//				.getActualTypeArguments()[0];
//		if( !t.isInstance( this ) ) throw new IllegalArgumentException();
//		} catch( Exception e ) {}
//	}
	
	
	
	// ITERABLES ////////////////////////////////////////////////////
	
	
	// ITERATORS ////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public Iterator<N> iterator() {
		return children.iterator();
	}

	public Iterable<N> leafs() {
		return hasChildren() ? getLeafs() : Collections.singleton( getInstance() );
	}
	
	public Iterable<N> dft() {
		return makeIterable( new DFTTraversal() );
	}
	
	public Iterable<N> dft( Predicate<N> traverseCondition ) {
		return makeIterable( new DFTTraversal( traverseCondition ) );
	}
	
	public Iterable<N> bft() {
		return makeIterable( new BFTTraversal() );
	}
	
	public Iterable<N> bft( Predicate<N> traverseCondition ) {
		return makeIterable( new BFTTraversal( traverseCondition ) );
	}

	public Iterable<N> makeIterable(Iterator<N> type) {
		return new Iterable<N>() {
			public Iterator<N> iterator() {
				return type;
			}
		};
	}

	

	public class DFTTraversal implements Iterator<N> {
		public Stack<N> traversal = new Stack<>();
		public Predicate<N> traverseCondition = n -> true;

		public DFTTraversal() {
			traversal.push(getInstance());
		}
		
		public DFTTraversal( Predicate<N> traverseCondition ) {
			this.traverseCondition = traverseCondition;
			traversal.push(getInstance());
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public N next() {
			N n = traversal.pop();
			if ( n.hasChildren() && traverseCondition.test( n ) ) {
				for (int i = n.getChildCount() - 1; i >= 0; i--)
					traversal.push( n.get(i) ); // reverse order
			}
			return n;
		}
	}
	
	public class BFTTraversal implements Iterator<N> {
		public Queue<N> traversal = new LinkedList<>();
		public Predicate<N> traverseCondition = n -> true;

		public BFTTraversal() {
			traversal.add(getInstance());
		}
		
		public BFTTraversal( Predicate<N> traverseCondition ) {
			this.traverseCondition = traverseCondition;
			traversal.add(getInstance());
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public N next() {
			if (!hasNext())
				throw new NoSuchElementException();
			N n = traversal.poll();
			if (n.hasChildren() && traverseCondition.test(n) )
//					for (int i = n.children.size()-1; i >= 0; i--)
				for (int i = 0; i < n.getChildCount(); i++ )
					traversal.add(n.get(i)); // reverse this?
			return n;
		}
		
	}
		
		
	
	
	


	
////	public String printOperation() {
////		return printOperation( createPrintFn( n -> "node" ) );
////	}
//	
//	public String printOperation( Function<T,?> fn ) {
//		String out = "";
//		for( T n : dft() ) out += createPrintFn( fn ).apply(n).toString() + "\n";
//		return out;
//	}
//	
////	Function<T,String> basicPrint = n -> new String(new char[depth()]).replace('\0', ' ')+"node";
//	
//	public Function<T,String> createPrintFn( Function<T,?> addedString ){
//		return n -> new String( new char[n.depth()]).replace( '\0', ' ' ) + addedString.apply(n).toString();
//	}
		
	// LAMBDA FNS ////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String multiLineString(Function<N, ?> fn) {
		String out = "";
		for (N n : this ) out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}
	
	public void printOperation(Function<N, ?> fn) {
		for ( N n : dft() ) System.out.println( createPrintFn( fn ).apply( n ).toString() );
	}

	public Function<N, String> createPrintFn(Function<N, ?> addedString) {
		return n -> {
			Object val = addedString.apply(n);
			String space = "";
			for( int i = 0; i < n.depth; i++ ) space = "  " + space;
			return space + " - " + ( val == null ? "(null) " : val.toString() );
		};
	}
	
	public String toString() {
//		return ( hasParent() ? "node " + index : "root" ) + ( hasChildren() ? ", childCt = " + getChildCount() : "leaf") + ", depth = " + depth;
		String out = "";
		if( !hasParent() )        out += "root: " + ( hasChildren() ? "childCt: " + getChildCount() : "" );
		else if( hasChildren() ) out += "node: childCt: " + getChildCount();
		else                     out += "leaf: ";
		out += ", depth = " + depth;
		return out;
	}
	
	
	
}



