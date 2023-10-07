package unCore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.*;
import java.util.stream.*;

import pFns_general.PFns;
import ugCore.Grid;
import ugCore.Divider.Recursive;
import utTypes.BasicNode;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractNode<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> 
	implements Iterable<N>,Comparable<N>, NodeFunctions<N>{
//	protected List<N> myTree;
	public T tree;
	public int parentLoc = -1;   // index in tree.nodes
	public int myLoc = -1;	     // index in tree.nodes
	public int indexInParent = -1;
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
	
	// ExternalListModifier interface ///////////////////////////////////////////////
	
	public int index() {
		return myLoc;
	}
	
	public void updateIndex() {
		// ??
	};
	
	public int size() {
		int out = 0;
		for( N n : this ) out++;
		return out;
	}
	
	public Function<N,List<N>> dfsNodeGatherFn(){
		return n -> { 
			List<N> nc = n.getChildren();
			Collections.reverse(nc);
			return nc; };
	}
	
	public Function<N,List<N>> bfsNodeGatherFn(){
		return n -> { 
			List<N> nc = n.getChildren();
			return nc; };
	}
	
	@Override
	public List<N> nodeList() {
		return tree.nodes;
	}
	
	
	
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
		child.indexInParent = child.myLoc - firstChild; //calcIndexInParent();
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
		for( int i = index; i < size; i++ ) get(i).indexInParent--;
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
	
	public void clearChildren() {
		for ( int i = 0; i < getChildCount(); i++ )
			removeChild( i );
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
	
	// returns true if node has leafs as direct children;
	public boolean containsLeafs() {
		for( N node : getChildren() ) if( node.isLeaf() ) return true;
		return false;
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
//		if( !hasParent() ) return -1;
//		else return myLoc - parent().firstChild;
		return indexInParent;
	}
	
	public int calcIndexInParent() {	// used for new node creation and sort operations
		if( !hasParent() ) return -1;
		return myLoc - parent().firstChild;
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
		for( N node : getChildren() ) node.getLeafs( data );           // can't use iterable if used in iterable class?
		return data;
	}
	
	/**
	 * getLeafs of a data list using tree structure
	 */
	public <E> List<E> getLeafElems( List<E> dataList ){
		return getLeafs().stream().map( n -> n.getElem( dataList )).collect(Collectors.toList());
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
			for( N node : getChildren() ) {
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
			for( N node : getChildren() )
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
			for( N node : getChildren() ) {
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
			for( N node : getChildren() ) node.getDepth( data, targetLevel );           // can't use iterable if used in iterable class?
		return data;
	}
	
	public <E> List<E> getChildData( Function<N,E> fn ){
		List<E> out = new ArrayList<>();
		for( N child : getChildren() ) out.add( fn.apply(child) );
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
		return tree.nodes.size();
	}


	public String locationCode() {
		return locationCodeFn( "" ); 
	}
	
	String locationCodeFn( String code ) {
		if( hasParent() ) return parent().locationCodeFn( Integer.toString( indexInParent() ) + code );
		else return code;
	}
	
	/**
	 * returns data of a new child node, so info can be used without creating instance
	 * ultrashape patternneeds index information for creation of new node before it is added
	 * simulates default add with no index specification
	 */
	
	public int[] nextNodeData() {
		int childIndex =  size;
		int simulatedNodeLoc = !hasChildren() ? tree.nodes.size() : firstChild + childIndex; // myLoc + 1 + size;
		return new int[] {
				simulatedNodeLoc,   // new node: myLoc
				myLoc,			    // new node: parentLoc
				depth + 1,			// new node: depth
				myLoc - firstChild  // new node: indexInParent 
		};
	}
	
	
	
	
	
	// LIST FNS /////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
//	public <E> E getElem(List<E> inputList) {
//		try {
//			return inputList.get(myLoc);
//		} catch (Exception e) {	// index outside of list
//			return null;
//		}
//	}
//	
//	public <E> void setElem( E val, List<E> treeData ){
//		if( treeData.size() < tree.nodes.size() ) makeList( treeData );
//		if( myLoc != treeData.size() ) treeData.set(myLoc, val);
//		else  treeData.add(val);
//	}
//	
//	public <E> void modifyElem( Consumer<E> modifyFn, List<E> treeData ){
//		if( treeData.size() < tree.nodes.size() ) makeList( treeData );
//		modifyFn.accept( treeData.get(myLoc));
//	}
//	
//	public <E> void addElem( E val, List<E> treeData ){
//		if( treeData.size() == 0 ) makeList( treeData );
//		if( myLoc != treeData.size() ) treeData.add(myLoc, val);
//		else  treeData.add(val);
//	}
	
//	
//	public <E> List<E> getElems(List<N> nodeList, List<E> inputList) {
//		return nodeList.stream().map( n -> n.getElem(inputList) ).collect( Collectors.toList() );
//	}
//	
//	
//	
//	public <E> void makeList( List<E> treeData ) {
//		while( treeData.size() < tree.nodes.size() ) treeData.add(null);
//	}
//	
//	public <E> List<E> makeList( E... val ) {
//		List<E> out = new ArrayList<>();
//		while( out.size() < tree.nodes.size() ) out.add( val.length > 0 ? val[0] : null );
//		return out;
//	}
	
	
//	public BiConsumer<N,List<?>> IMPORT_BINARYTREE = (n,list) -> {
//		if( list.size() > 2 ) {
//			
//		}
//	};
	
	public BiConsumer<N,List<?>> makeBinaryTree(){
		Recursive<BiConsumer<N,List<?>>> op = new Recursive<>();
		op.func = (n,list) ->{
			if( list.size() > 1 ) {
				int n1Children = (int)list.size()/2;
				n.addChild();
				n.addChild();
				op.func.accept( n.get(0), list.subList( 0, n1Children ) );
				op.func.accept( n.get(1), list.subList( n1Children, list.size() ) );
			}
		};
		return op.func;
	}
	
	public <E> void buildWithLeafData( List<E> leafData, BiConsumer<N,List<?>> structureFn ) {
		if( hasChildren() ) this.clearChildren();
		structureFn.accept( getInstance(), leafData);
	}
	
//	
//	public <E> void generateFromData( List<E> input, BiConsumer<N,List<E>> modifyFn ){
//		if( treeData.size() < tree.size() ) fillList(treeData);
////		
////		int originalChildCt = size;
////		for( int i = 0; i < input.size(); i++ )  addChild();
////		for( int i = originalChildCt; i < size; i++ ) {
////			get(i).setElem(input.get(i), treeData);
////			modifyFn.accept( get(i), treeData);
////		}
//		modifyFn.accept( getInstance(), input );
//		int elemCt = 0;
//		for( N nChild : getLeafs() ) {
//			nChild.setElem( input.get( elemCt ), treeData );
//			elemCt++;
//		}
//	}
		
	
	public <E> List<E> fillList( List<E> input ){
		while( input.size() < tree.size() ) input.add( null );
		return input;
	}
	
	public <E> List<E> setLeafElems( List<E> leafData, List<E>... treeList ){
		List<E> out = treeList != null ? fillList( treeList[0]) : fillList( new ArrayList<>() );
		int elemCt = 0;
		for( N nChild : getLeafs() ) {
			nChild.setElem( leafData.get( elemCt ), out );
			elemCt++;
		}
		return out;
	}
	
	public <E,R> List<R> setLeafElems( List<E> leafData, Function<E,R> nodeFn, List<R>... treeList ){
		List<R> out = treeList != null && treeList.length > 0 ? fillList( treeList[0]) : fillList( new ArrayList<>() );
		int elemCt = 0;
		for( N nChild : getLeafs() ) {
			nChild.setElem( nodeFn.apply( leafData.get( elemCt ) ), out );
			elemCt++;
		}
		return out;
	}
	
	
	
	
	
	// ORDER CHANGE ////////////////////////////////////////

//	public void reverse() {
////		Collections.reverse(children); ??? how to do this? reverse index positions somehow?
//	}
//
//	public void shuffle() {
////		Collections.shuffle(children);  ???
////	}
//	
	// SORTING ////////////////////////////////////////////////////
	
	@Override
    public int compareTo(N other) {
        return Integer.compare(this.indexInParent(), other.indexInParent());
    }
	
	public void shuffle() {
		shuffle( getChildren() );
	}
	public void shuffleRecursive() {
		shuffleRecursive( n -> n.getChildren() );
	}
	public void reverse() {
		reverse( getChildren() );
	}
	public void sort( Comparator<N> comparator ) {
		sort( getChildren(), comparator);
	}
	public void sortRecursive( Comparator<N> comparator ) {
		sortRecursive( n -> n.getChildren(), comparator);
	}
	
	public <E> void sort( Comparator<N> comparator, List<E> dataList ) {
		sort( n -> n.getChildren(), comparator, dataList );
	}
//	
//	public void sort( Comparator<N> comparator ) {
//		if( !hasChildren() ) return;
//		Collections.sort( tree.nodes.subList(firstChild, firstChild+size), comparator );
//		sortTreeUpdate();
//		for( N node : children() ) node.sort( comparator );
//	}
//	
//	public void shuffle() {
//		if( !hasChildren() ) return;
//		Collections.shuffle( tree.nodes.subList(firstChild, firstChild+size) );
//		sortTreeUpdate();
//		for( N node : children() ) node.shuffle();
//	}
//	
//	public void sortChildren( Comparator<N> comparator ) {
//		Collections.sort( tree.nodes.subList(firstChild, firstChild+size), comparator );
//		sortTreeUpdate();
//	}
//	
//	public void shuffleChildren() {
//		Collections.shuffle(tree.nodes.subList(firstChild, firstChild+size));
//		sortTreeUpdate();
//	}
//	
//	public void reverse() {
//		Collections.reverse(tree.nodes.subList(firstChild, firstChild+size));
//		sortTreeUpdate();
//	}
//	
//	
//	public <E> void sort( Comparator<N> comparator, List<E> data ) {
//		List<N> nodeList = tree.nodes.subList(firstChild, firstChild+size);
//		List<E> dataList = data.subList(      firstChild, firstChild+size);
//		Collections.sort( nodeList, comparator );
//		
//		Comparator<E> compare = new Comparator<E>() {
//		     public int compare(E d1, E d2) {
//		         return nodeList.get( dataList.indexOf(d1) ).indexInParent() - nodeList.get( dataList.indexOf(d2) ).indexInParent();
//		     }
//		};
//		dataList.sort(compare);
//		  
//		sortTreeUpdate();
//	}
//	
//	
//	
////	Comparator<Integer>  nodeOrder = new Comparator<Item>( List<Integer> data ) {
////	    public int compare(N node, List<Integer> data ) {
////	        return Integer.compare(node.indexInParent(), data::get );
////	    }
////	};
//	
//	
	/**
	 * updates myloc for all children of updated node
	 */
	
	protected void sortTreeUpdate() {
		for( int i = 0; i < getChildCount(); i++ ) {
			get(i).indexInParent = i;
			get(i).myLoc = firstChild + i;
			if( get(i).hasChildren() ) 
				for( N node : get(i).getChildren() ) node.parentLoc = get(i).myLoc;
		}
		sortUpdate();
	}
	
	protected void sortUpdate() {} // override in subclass if needed (grid positionInParent() )
	
	
	
	
	
	
	/**
	 * sorts input list by myloc fields of tree nodes
	 * run before sortTreeUpdate() so it uses the out of order mylocs to sort
	 */
	public void sortDataList( List<?> dataList ) {
		if( dataList.size() != tree.nodes.size() ) throw new UnsupportedOperationException("data list size not equal to tree size");
		List<Integer> indexList = IntStream.range(0,tree.nodes.size()).map( i -> tree.nodes.get(i).myLoc ).boxed().collect(Collectors.toList());
		 Collections.sort( dataList, 
				    Comparator.comparing(item -> indexList.indexOf( dataList.indexOf( item ) ) ) );
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
		for( N node : getChildren() ) {
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
		return nodeIterator();
	}
//	
//	public Iterator<N> dfs(){
//		return new DFSTraversal<N>( getInstance(), n -> { 
//			List<N> nc = n.getChildren();
//			Collections.reverse(nc);
//			return nc; } );
//	}
	

	
	
	// UTILITY FNS ////////////////////////////////////////
	
	public <E extends AbstractNode<?,?>> void copyFieldsFromTo( E from, E to ) {
		to.parentLoc =  from.parentLoc;
		to.myLoc =      from.myLoc;
		to.firstChild = from.firstChild;
		to.size =       from.size;
		to.dataLoc =    from.dataLoc;
		to.depth =      from.depth;
	}
	
	
	
	/**
	 * generic helper class to wrap functional interface
     * https://stackoverflow.com/questions/19429667/implement-recursive-lambda-function-using-java-8
     */
	
	public class Recursive<I> {
	    public I func;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof AbstractNode ) ) return false;
	    AbstractNode<?,?> otherMyClass = (AbstractNode<?,?>)other;
	    if( myLoc == otherMyClass.myLoc
	    		&& size == otherMyClass.size
	    		&& depth == otherMyClass.depth
	    		&& dataLoc == otherMyClass.dataLoc ) return true;
	    return false;
	}
	
	
	// PRINT FNS //////////////////////////////////////////

	public String toString() {
		return "parentLoc = " + parentLoc + ", myLoc = " + myLoc + ", size = " + size + ", depth = " + depth
				+ ", firstChild = " + firstChild;
	}
	

}
