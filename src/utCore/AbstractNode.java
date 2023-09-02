package utCore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.*;
import java.util.stream.*;

import pFns_general.PFns;
import utTypes.BasicNode;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractNode<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N>{
//	protected List<N> myTree;
	public T tree;
	public int parentLoc = -1;
	public int myLoc = -1;
	public int firstChild = -1;
	public int size = 0;
	public int dataLoc = -1; // not incorporated into node shifting fn
	public int depth = -1;
	
	
	// CONSTRUCTORS ////////////////////////////////////////////////////////
	
	protected AbstractNode() {	// if not called by addChild, is root
	}
	
	
	/**
	 * used for converting subclasses to universal basicnode structure
	 * traverse through tree, copying all core data to new nodes
	 * from this, one subclass can be built with structure of another
	 * leafNodeFn is used to create a specific node with data from referenced element, vs just a default node
	 */
	
	public <E extends AbstractNode<?,E>> N buildWithStructureFrom( E nodeIn ) {
		initTree();
		copyFieldsFromTo( nodeIn,this );
		for( int i = 1; i < nodeIn.tree.nodes.size(); i++ ) {
			N newNode = defaultConstructor();
			newNode.tree = tree;
			copyFieldsFromTo( nodeIn.tree.nodes.get(i), newNode );
			tree.nodes.add( newNode );
		}
		return getInstance();
	}
	
	public <E extends AbstractNode<?,E>> N buildWithStructureFromWithLeafFn( E nodeIn, Function<E,N> leafNodeFn ) {
		initTree();
		copyFieldsFromTo( nodeIn,this );
		for( int i = 1; i < nodeIn.tree.nodes.size(); i++ ) {
			N newNode;
			if( nodeIn.tree.nodes.get(i).isLeaf() )
				newNode = leafNodeFn.apply(nodeIn.tree.nodes.get(i));
			else
				newNode= defaultConstructor();
			newNode.tree = tree;
			copyFieldsFromTo( nodeIn.tree.nodes.get(i), newNode );
			tree.nodes.add( newNode );
		}
		return getInstance();
	}
	
	
	// start w standalone node, create tree, add this as root
	public N initTree() {
		T par = treeDefaultConstructor();
		par.initRoot( getInstance() );
		return getInstance();
	}

	
	// ABSTRACT FNS /////////////////////////////////////////////////////////////////
	
	
	public abstract N defaultConstructor();
	public abstract N getInstance();
	public abstract T treeDefaultConstructor(); 	// just call t.getInstance so independent node can create its own tree
	
	// CHILDREN ///////////////////////////////////////////////////
	
	public void addChild( int...index ) {
		addChild( defaultConstructor(), index );
	}
	
	
	public void addChild( N child, int...index ) {
		int childIndex = index.length == 0 || !hasChildren() || index[0] > size ? size : index[0];
		int newIndex = !hasChildren() ? tree.nodes.size() : firstChild + childIndex; // myLoc + 1 + size;
		if ( !hasChildren() ) firstChild = newIndex;
		child.tree = tree;
		child.myLoc = newIndex;
		child.parentLoc = myLoc;
		child.depth = depth + 1;
		shiftNodesRight( newIndex, child.depth );
		tree.nodes.add( newIndex, child ); // add to index after loc, and after any pre existing children
		size++;
	}
	
	public void addChildren( List<N> children ) {
		for( N child : children ) addChild( child );
	}
	
	
	public <E> void addChild(E input, List<E> treeData, int...index ) {
		N c = defaultConstructor();
		addChild( c, index );
		if( treeData.size() == 0 ) makeList( treeData );
		if( c.myLoc != treeData.size() ) treeData.add( c.myLoc, input );
		else  treeData.add( input );
	}
	
	
	public  void removeChild( int index ) {
		if( get( index ).hasChildren() )	// recursively clear all contained nodes bottom up
			for ( int i = 0; i < get(index).getChildCount(); i++ )
				get(index).removeChild( i );
		int childIndex = get( index ).myLoc;
		size--;
		if( size == 0 ) firstChild = -1;
		tree.nodes.remove( childIndex );
		shiftNodesLeft( childIndex );
	}	
	
	
	public <E> void removeChild( int index, List<E> treeData ) {
		if( get( index ).hasChildren() )	// recursively clear all contained nodes bottom up
			for ( int i = 0; i < get(index).getChildCount(); i++ )
				get(index).removeChild( i, treeData );
		int childIndex = get( index ).myLoc;
		size--;
		if( size == 0 ) firstChild = -1;
		tree.nodes.remove( childIndex );
		shiftNodesLeft( childIndex );
		treeData.remove( childIndex );
	}

	
	public void shiftNodesRight( int index, int depth ) {	// addition
		if (index == tree.nodes.size())
			return;
		for (int i = 0; i < tree.nodes.size(); i++) {
			N curNode = tree.nodes.get(i);
			if ( curNode.myLoc >= index ) 
				curNode.myLoc+=1;
			if ( curNode.firstChild >= index && curNode.depth >= depth ) //curNode.firstChild != -1 )
				curNode.firstChild+=1;
			if (curNode.parentLoc >= index)
				curNode.parentLoc+=1;
		}
	}
	
	public void shiftNodesLeft(int index ) {  // removal
		if (index == tree.nodes.size())
			return;
		for (int i = 0; i < tree.nodes.size(); i++) {
			N curNode = tree.nodes.get(i);
			if (curNode.myLoc >= index)
				curNode.myLoc--;
			if ( curNode.firstChild > index && curNode.firstChild != -1 )
				curNode.firstChild--;
			if (curNode.parentLoc >= index)
				curNode.parentLoc--;
		}
	}
	
	// CHECK FNS ///////////////////////////////////////////////////////////////////////////

	public boolean hasParent() {
		return parentLoc > -1;
	}
	public boolean hasChildren() {
		return firstChild > -1;
	}
	
	public boolean isLeaf() {
		return firstChild == -1;
	}

	public boolean isRoot() {
		return parentLoc == -1;
	}

	
	
	// GET FNS ////////////////////////////////////////////////////////////////

	
	public N get(int childIndex ) {
		if( firstChild == -1 ) throw new NoSuchElementException( "Node (root) -> " + this.locationCode() + ": has no children");
		return tree.nodes.get( firstChild + childIndex );
	}
		
	
	public N parent() {
		return tree.nodes.get(parentLoc);
	}
	
	public int indexInParent() {
		if( !hasParent() ) return -1;
		else return myLoc - parent().firstChild;
	}
	
	public N getRoot() {
		return hasParent() ? parent().getRoot() : getInstance();
	}
	
	public N lastChild() {
		return get(size - 1);
	}
	
	public List<N> getChildren(){
		List<N> out = new ArrayList<N>();
		for( int i = 0; i < size; i++ ) out.add( get( i ) ); // can't use iterable if used in iterable class?
		return out;
	}
	
	public List<N> getLeafs(){
		return getLeafs( new ArrayList<N>() );
	}
	public List<N> getLeafs( List<N> data ){
		if( !hasChildren() ) data.add( getInstance() );
		for( N node : children() ) node.getLeafs( data );           // can't use iterable if used in iterable class?
		return data;
	}
	
	/*
	 * runs selector fn at each level to get next child until leaf is reached
	 */
	public N getLeafIndexSelectorFn( Function<N,Integer> childSelectionFn ) {
		if( hasChildren() ) return get( childSelectionFn.apply(getInstance()) ).getLeafIndexSelectorFn( childSelectionFn );
		else return getInstance();
	}
	
	public N getLeafSelectorFn( Predicate<N> childSelectionFn ) {	// returns first match
		if( hasChildren() ) {
			for( N node : children() ) {
				if( childSelectionFn.test(node) )
					return node.getLeafSelectorFn( childSelectionFn );
			}
			return null;
		}
		else return getInstance();
	}
	
	 public List<N> getLeafsSelectorFn( Predicate<N> childSelectionFn ) {
		 List<N> out = new ArrayList<N>();
		 getLeafsSelectorFnRecursive( out, childSelectionFn );
		 return out;
	 }
	 
	 void getLeafsSelectorFnRecursive( List<N> foundLeafs, Predicate<N> childSelectionFn ) {
		if( hasChildren() ) {
			for( N node : children() )
				if( childSelectionFn.test(node) )
					node.getLeafsSelectorFnRecursive( foundLeafs, childSelectionFn );
		}
		else foundLeafs.add( getInstance() );
	}
	
	/*
	 * gets all leafs and then filters based on predicate
	 */
	public List<N> getFilteredLeafs( Predicate<N> selectionFn ){
		List<N> out = new ArrayList<>();
		for( N node : getLeafs() ) if( selectionFn.test(node) ) out.add(node);
		return out;
	}
	
	public int getMaxDepth() {
		if( hasChildren() ) {
			int maxChildDepth = depth;
			for( N node : children() ) {
				int curDepth = node.getMaxDepth();
				if( curDepth > maxChildDepth ) maxChildDepth = curDepth;
			}
			return maxChildDepth;
		}
		
		return depth;
	}
	public List<N> getDepth( int targetLevel ){
		return getDepth( new ArrayList<N>(), targetLevel );
	}
	
	public List<N> getDepth( List<N> data, int targetLevel ){
		if( depth == targetLevel ) data.add( getInstance() );
		if( depth < targetLevel ) 
			for( N node : children() ) node.getDepth( data, targetLevel );           // can't use iterable if used in iterable class?
		return data;
	}
	
	public <E> List<E> getChildData( Function<N,E> fn ){
		List<E> out = new ArrayList<>();
		for( N child : children() ) out.add( fn.apply(child) );
		return out;
	}
	
	public int getChildCount() {
		return size;
	}
	
	
	public int leafSize() {
//		int out = 0;
//		while( leafs().iterator().hasNext() ) out++ ;
//		return out;
		return getLeafs().size();
	}
	
	public int treeSize() {
		int out = 0;
		for( AbstractNode<T,N> n : this ) out++;
		return out;
	}


	public String locationCode() {
		return locationCodeFn( "" ); 
	}
	
	String locationCodeFn( String code ) {
		if( hasParent() ) return parent().locationCodeFn( Integer.toString( indexInParent() ) + code );
		else return code;
	}
	
	
	
	
	
	// LIST FNS /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	public <E> E getElem(List<E> inputList) {
		try {
			return inputList.get(myLoc);
		} catch (Exception e) {	// index outside of list
			return null;
		}
	}
	
	public <E> void setElem( E val, List<E> treeData ){
		if( treeData.size() < tree.nodes.size() ) makeList( treeData );
		if( myLoc != treeData.size() ) treeData.set(myLoc, val);
		else  treeData.add(val);
	}
	
	public <E> void addElem( E val, List<E> treeData ){
		if( treeData.size() == 0 ) makeList( treeData );
		if( myLoc != treeData.size() ) treeData.add(myLoc, val);
		else  treeData.add(val);
	}
	
	
	public <E> List<E> getElems(List<N> nodeList, List<E> inputList) {
		return nodeList.stream().map( n -> n.getElem(inputList) ).collect( Collectors.toList() );
	}
	
	
	
	public <E> void makeList( List<E> treeData ) {
		while( treeData.size() < tree.nodes.size() ) treeData.add(null);
	}
	
	public <E> List<E> makeList( E... val ) {
		List<E> out = new ArrayList<>();
		while( out.size() < tree.nodes.size() ) out.add( val.length > 0 ? val[0] : null );
		return out;
	}
	
	
	
//	public <E> void childLeafImport( List<E> input, List<E> treeData, BiConsumer<N,List<E>> modifyFn ){
//		if( treeData.size() < myTree.size() ) fillList(treeData);
//		int originalChildCt = size;
//		for( int i = 0; i < input.size(); i++ )  addChild();
//		for( int i = originalChildCt; i < size; i++ ) {
//			get(i).setElem(input.get(i), treeData);
//			modifyFn.accept( get(i), treeData);
//		}
//	}
	
	// ORDER CHANGE ////////////////////////////////////////

	public void reverse() {
//		Collections.reverse(children); ??? how to do this? reverse index positions somehow?
	}

	public void shuffle() {
//		Collections.shuffle(children);  ???
	}
	
	
	
	// LAMDAS /////////////////////////////////////////////
	
	public <E> void applyThenToChildren(Consumer<N> fn) {
		fn.accept(getInstance());
		for (int i = 0; i < getChildCount(); i++) {
			get(i).applyThenToChildren(fn);
		}
	}
	
	public <E> E bottomUpFn( BiFunction<N,List<E>,E> fn, Function<N,E> leafFn ) {
		List<E> out = new ArrayList<>();
		for( N node : children() ) {
			if( node.isLeaf() ) out.add( leafFn.apply( node ) );
			else out.add( node.bottomUpFn( fn, leafFn ) );
		}
		return fn.apply( getInstance(), out );
	}
	
	public void leafOperation( Consumer<N> leafFn ) {
		
	}

	
	
	/**
	 * applies biconsumer to each node child
	 */
	
//	public <E> void recursiveChildOperation( E data, BiConsumer<N,E> fn ) {
//		if( hasChildren() ) {
//			fn.accept( getInstance(), data);
//			for( int i = 0; i < getChildCount(); i++ ) {
//				if( get(i).hasChildren() ) {
//					get(i).recursiveChildOperation( data, fn );
//				} 
//			}
//		}
//	}
//	
//	/**
//	 * generates list of values for each child, adds to datalist, and recurses
//	 */
//	
//	public <E> void recursiveChildOperation( List<E> data, BiFunction<N,List<E>,List<E>> fn ) {
//		if( hasChildren() ) {
//			List<E> childData = fn.apply( getInstance(), data ); // returns value for each child
//			for( int i = 0; i < getChildCount(); i++ ) {
//				get(i).setElem(childData.get(i), data);
//				if( get(i).hasChildren() ) {
//					get(i).recursiveChildOperation( data, fn );
//				} 
//			}
//		}
//	}
//	public void applyFn(Consumer<N> fn ) {
//		fn.accept( getInstance() );
//	}
//	
//	public <E> E toObj( Function<N,E> fn ) {
//		return fn.apply( getInstance() );
//	}
//
//	public <E, R> E applyFn(BiFunction<N, R, E> fn) {
//		return fn.apply( getInstance(), null );
//	}
//
//	public <E, R> E applyFn(R input, BiFunction<N, R, E> fn) {
//		return fn.apply( getInstance(), input );
//	}
	
	// ITERATORS ////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	
	/*
	 * iterator defaults as depth first traversal
	 */
	public Iterator<N> iterator() {
		return new DFTTraversal<Void>();
	}
	
	

	public Iterable<N> leafs() {
		return getLeafs();
	}

	public Iterable<N> makeIterable(Iterator<N> type) {
		return new Iterable<N>() {
			public Iterator<N> iterator() {
				return type;
			}
		};
	}

	public Iterable<N> children() {
		return new Iterable<N>() {
			public Iterator<N> iterator() {
				return new Iterator<N>() {
					int index = 0;

					public boolean hasNext() {
						return index < size;
					}

					public N next() {
						N out = get(index);
						index++;
						return out;
					}

					public void remove() {
					}
				};
			}
		};
	}
	
	public Iterable<N> bfs(){
		Iterator<N> it = new Iterator<N>() {
			public Queue<N> traversal = new LinkedList<>(Arrays.asList(getInstance()));

			@Override
			public boolean hasNext() {
				return !traversal.isEmpty();
			}

			@Override
			public N next() {
				if (!hasNext())
					throw new NoSuchElementException();
				N n = traversal.poll();
				if (n.hasChildren())
					for (int i = n.getChildCount()-1; i >= 0; i--)
						traversal.add(n.get(i)); // reverse this?
				return n;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	

		return new Iterable<N>() {
			public Iterator<N> iterator() {
				return it;
			}
		};
	}

	public class DFTTraversal<V> implements Iterator<N> {
		public Stack<N> traversal = new Stack<>();
		public BiConsumer<N, V> traverseFn;
		public V data;

		public DFTTraversal() {
			traversal.push(getInstance());
		}

		public DFTTraversal(V data, BiConsumer<N, V> traverseFn) {
			traversal.push(getInstance());
			this.data = data;
			this.traverseFn = traverseFn;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public N next() {
			N n = traversal.pop();
			if (traverseFn != null)
				traverseFn.accept(n, data);
			if ( n.hasChildren() ) {
				for (int i = n.size - 1; i >= 0; i--)
					traversal.push( n.get(i) ); // reverse order
			}
			return n;
		}

		public void remove() {
			// Default throws UnsupportedOperationException.
		}
	}
	
	
	// UTILITY FNS ////////////////////////////////////////
	
	public <E extends AbstractNode<?,?>> void copyFieldsFromTo( E from, E to ) {
		to.parentLoc =  from.parentLoc;
		to.myLoc =      from.myLoc;
		to.firstChild = from.firstChild;
		to.size =       from.size;
		to.dataLoc =    from.dataLoc;
		to.depth =      from.depth;
	}
	
	
	// PRINT FNS //////////////////////////////////////////

	public String toString() {
		return "parentLoc = " + parentLoc + ", myLoc = " + myLoc + ", size = " + size + ", depth = " + depth
				+ ", firstChild = " + firstChild;
	}
	

}
