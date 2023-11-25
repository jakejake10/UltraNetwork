package unCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import unCore.NodeObj.CoreData;

public abstract class TreeNodeStruct<N extends TreeNodeStruct<N,D>, D> implements NodeObj<N, D>, Iterable<N>, TreeBuilder<N,D> {
	//COMMON
	CoreData<N,D> core;
	D data;
	/**
	 * Identifier generator, used to get a unique id for each created tree node
	 */
	final UUID id = UUID.randomUUID();
	//OTHER
	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int size = 0;
	public int dataLoc = -1; // not incorporated into node shifting fn
	public int depth = 0;

	// constructors required in subclass
	public TreeNodeStruct( BaseNodeCommand...initType ){	// root constructor
		if( initType == null || initType.length == 0 ) NodeObj.nodeInitRoot(getInstance()); // is root, not empty
	}
	

	
	// NodeObj INTERFACE /////////////////////////////////
	/**
	 * methods for final class:
	 * defaultConstructor()
	 * getInstance()
	 */
	
	public int index() {
		return index;
	}

	/**
	 * also sets index of child nodes
	 * if extracting node group from a larger tree, set root index to 0?
	 * if adding child to another node group, set root to new addchild index
	 * gaps between nodes in nodelist?
	 */
	public void setIndex( int index ) {
		this.index = index;
	}
	

	public int size() {
		int out = 0;
		for (N n : getInstance())
			out++;
		return out;
	}


	public Function<N,List<N>> dfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getChildren();
			Collections.reverse(nc);
			return nc;
		};
	}

	public Function<N,List<N>> bfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getChildren();
			return nc;
		};
	}

	//COMMON /////////////////
	
	public CoreData<N,D> getCore(){
		return core;
	}
	public void setCore( CoreData<N,D> input ){	// no recursion, for node operations
		this.core = input;
	}
	public CoreData<N,D> makeCore() {
		return new CoreData<>( (n,i) -> n.firstChild + i );
	}
	public D getData() {
		return data;
	}
	public N setData( D data ) {
		this.data = data;
		return getInstance();
	}
	@Override
	public void insertNodeFn( N input ) { 
		addChild( input );
	}
	@Override
	public N nodeCopy() {	// no coredata involved
		N out = defaultConstructor();
		copyFieldsFromTo(getInstance(), out);
		out.setData(getData());
		return out;
	}
	
//	@Override
//	public N defaultConstructor() {
//		return new TreeNodeStruct<>( 0 );
//	}
//
//	@Override
//	public N getInstance() {
//		return this;
//	}
//
//	public int size() {
//		int out = 0;
//		for (N n : this)
//			out++;
//		return out;
//	}
//
//	public Function<N, Collection<N>> dfsNodeGatherFn() {
//		return n -> {
//			List<N> nc = n.getChildren();
//			Collections.reverse(nc);
//			return nc;
//		};
//	}
//
//	public Function<N, Collection<N>> bfsNodeGatherFn() {
//		return n -> {
//			List<N> nc = n.getChildren();
//			return nc;
//		};
//	}
//	
	

	// CHECK FNS
	// ///////////////////////////////////////////////////////////////////////////

	public boolean hasParent() {
		return parentIndex > -1;
	}

	public boolean hasChildren() {
		return firstChild > -1;
	}

	public boolean isLeaf() {
		return firstChild == -1;
	}

	public boolean isRoot() {
		return parentIndex == -1;
	}

	// returns true if node has leafs as direct children;
	public boolean containsLeafs() {
		if(!hasChildren() ) return false;
		for (N node : getChildren())
			if (node.isLeaf()) return true;
		return false;
	}
	
	public boolean containsOnlyLeafs() {
		if(!hasChildren() ) return false;
		for (N node : getChildren())
			if (!node.isLeaf()) return false;
		return true;
	}
	
	/**
	 * Indicates whether some object equals to this one
	 *
	 * @param obj the reference object with which to compare
	 * @return {@code true} if this object is the same as the obj
	 *         argument; {@code false} otherwise
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TreeNodeStruct<N,D> that = (TreeNodeStruct<N,D>) obj;
		return this.id == that.id;
	}
	
	public boolean checkLeafErrorMSG( String fnName ) {
		if( !isLeaf() ) {
			System.out.println(fnName + " can only be called on a leaf node");
			return false;
		}
		return true;
	}
	public boolean checkNonLeafErrorMSG( String fnName ) {
		if( isLeaf() ) {
			System.out.println(fnName + " can only be called on a non leaf node");
			return false;
		}
		return true;
	}

	// GET FNS ////////////////////////////////////////////////////////////////

//	public TreeNode<D> get(int childIndex) {
//		if (firstChild == -1)
//			throw new NoSuchElementException("Node (root) -> " + this.locationCode() + ": has no children");
//		return nodeList.get(firstChild + childIndex);
//	}

	public N parent() {
		return nodeList().get(parentIndex);
	}

	public int indexInParent() {
		if (!hasParent())
			return -1;
		else
			return index - parent().firstChild;
//		return indexInParent;
	}

	public int calcIndexInParent() { // used for new node creation and sort operations
		if (!hasParent())
			return -1;
		return index - parent().firstChild;
	}

	public N getRoot() {
		return hasParent() ? parent().getRoot() : getInstance();
	}

	public N lastChild() {
		return get(size - 1);
	}

	public List<N> getChildren() {
//		List<N> out = new ArrayList<>();
//		for (int i = 0; i < size; i++)
//			out.add(get(i)); // can't use iterable if used in iterable class?
		return IntStream.range(firstChild, firstChild+size)
				.mapToObj(i -> nodeList().get(i) )
				.collect(Collectors.toList());
	}

	public List<N> getLeafs() {
		return getLeafs(new ArrayList<N>());
	}

	public List<N> getLeafs(List<N> data) {
		if (!hasChildren())
			data.add(getInstance());
		for (N node : getChildren())
			node.getLeafs(data); // can't use iterable if used in iterable class?
		return data;
	}

	public List<N> getFilteredLeafs(Predicate<N> selectionFn) {
		List<N> out = new ArrayList<>();
		for (N node : getLeafs())
			if (selectionFn.test(node))
				out.add(node);
		return out;
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

	public int getMaxDepth() {
		if (hasChildren()) {
			int maxChildDepth = depth;
			for (N node : getChildren()) {
				int curDepth = node.getMaxDepth();
				if (curDepth > maxChildDepth)
					maxChildDepth = curDepth;
			}
			return maxChildDepth;
		}

		return depth;
	}

	public List<N> getDepth(int targetLevel) {
		return getDepth(new ArrayList<N>(), targetLevel);
	}

	public List<N> getDepth(List<N> data, int targetLevel) {
		if (depth == targetLevel)
			data.add(getInstance());
		if (depth < targetLevel)
			for (N node : getChildren())
				node.getDepth(data, targetLevel); // can't use iterable if used in iterable class?
		return data;
	}

	public <E> List<E> getChildData(Function<N, E> fn) {
		List<E> out = new ArrayList<>();
		for (N child : getChildren())
			out.add(fn.apply(child));
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

	public String locationCode() {
		return locationCodeFn("");
	}

	String locationCodeFn(String code) {
		if (hasParent())
			return parent().locationCodeFn(Integer.toString(indexInParent()) + code);
		else
			return code;
	}
	
	/**
	 * copies node tree or any subtree
	 * data copy fn deep copies data associated with node
	 * uses built in addChild method, corrects fields where needed
	 */
//	public N copy( Function<D,D> dataCopyFn ) {
//		N out = defaultConstructor();
//		NodeObj.nodeInitRoot(out);
//		out.setData( dataCopyFn.apply( getData() ));
//		out.copyNodeList( getInstance(), dataCopyFn );
//		return out;
//	}
//	
//	public void copyNodeList( N nodeIn, Function<D,D> dataCopyFn ) {
//		int myInd = index;
//		int myPar = parentIndex;
//		int myFC = nodeList().size();
//		if( nodeIn.hasChildren() ) {
//			for( N child : nodeIn.getChildren() ) addChild( dataCopyFn.apply( child.getData() ) );
//			for( int i = 0; i < getChildCount(); i++ )  get(i).copyNodeList( nodeIn.get(i), dataCopyFn );
//		}
//		copyFieldsFromTo( nodeIn, getInstance() );
//		if( nodeIn.hasChildren() ) firstChild = myFC;
//		index = myInd;
//		parentIndex = myPar;
//		depth = hasParent() ? parent().depth + 1 : 0;
//	}
	


	// STRUCTURE MODIFICATION OPERATIONS //////////////////////////////////////////////


	
	
	/**
	 * node adding update
	 * override the addChild method in subclass for added functionality
	 * call super.addChild() 
	 */
	public N addChild() {
		addChildTreeDataFn( null, null );
		return lastChild();
	}
	public N addChild( int indexIn ) {
		addChildTreeDataFn( null, null, indexIn );
		return lastChild();
	}
	public N addChild( D dataIn, int...indexIn ) {
		addChildTreeDataFn( null, dataIn, indexIn );
		return lastChild();
	}
	
	public N addChild( List<D> dataIn ) {
		for( D data : dataIn ) addChildTreeDataFn( null, data  );
		return lastChild();
	}
	public N addChild( N childIn, int...indexIn ) {
		addChildTreeDataFn( childIn, null, indexIn );
		return lastChild();
	}
	
	public void addChildren(List<N> children) {
		for (N child : children)
			addChild(child);
	}
	
	
	/**
	 * this is the heart of the tree class
	 * need to deal with adding a "child" that is itself a tree with children, done?
	 * sets the core, adds new child to nodeList()
	 * if childnode has chldren, they are pulled out and reconnected through recursive addchild operation
	 */
	private final void addChildTreeDataFn( N child, D dataIn, int...indexIn ) {
		List<N> foundChildren = new ArrayList<>();
		if( child == null ) child = defaultConstructor(); //newNode( new NoInput() );
		else {
			if( child.hasChildren() ) foundChildren = child.getChildren();	// need to pull these out and reconnect
			if( child.getData() != null && dataIn == null ) dataIn = child.getData();
		}
		
		int indexInParent = indexIn.length == 0 || !hasChildren() || indexIn[0] > size ? size : indexIn[0];
		if (!hasChildren())	firstChild = totalSize();
		child.size = 0; // if child has children, it's size needs to be reset to 0, so adding back children shows proper size
		child.firstChild = -1;	// if child has children, first child needs to be reset to -1 for future add ops to work properly
		child.index = firstChild + indexInParent;
		child.parentIndex = this.index;
		child.depth = depth + 1;
		shiftNodesRight(child.index, child.depth);
		nodeList().add(child.index, child); // add to index after loc, and after any pre existing children
		child.setCore(getCore());
//		child.core = core;
		if( dataIn == null ) child.generateData();
		else child.setData( dataIn );
		size++;
		if( foundChildren.size() > 0 ) for( N node : foundChildren ) child.addChild( node );
	}
	
	@Override
	public <R extends NodeObj<?,?>> void transferNodeDataTo( R input ) {
		if( input instanceof TreeNodeStruct ) {
			@SuppressWarnings("unchecked")
			TreeNodeStruct<N,?> tnInput = (TreeNodeStruct<N,?>) input;
			tnInput.size = size;
			tnInput.firstChild = firstChild;
			tnInput.index = index;
			tnInput.depth = depth;
			tnInput.parentIndex = parentIndex;
		}
	}
	
	
	/**
	 * updates parentIndex, firstChild, depth
	 * if a node group is extracted from a larger tree
	 * if a node tree is added to another tree
	 * node data needs to be updated to match
	 * root should be added in standard way
	 * children should be updated recursively with this fn
	 * same as addchildtreedatafn? but without creating new nodes?
	 */
	
	private final void updateChildTreeDataFn() {
		
	}
	

	/**
	 * replaces node with nodIn
	 * children of input node are added to tree
	 * original node children removed
	 */
	public N replaceWithTree( N nodeIn ) {
		removeChildren();
		nodeList().set(index, nodeIn);
		transferNodeDataTo( nodeIn );
		nodeIn.setCore(getCore() );
		return nodeIn;
	}
	
	/**
	 * replaces node with nodeIn
	 * only swaps node for nodeIn, nodeIn's children ignored
	 * all children of original node preserved
	 * nodeIn children are ignored
	 */
	public N replaceWithNode( N nodeIn ) {
		nodeList().set(index, nodeIn);
		transferNodeDataTo( nodeIn );
		nodeIn.setCore(getCore() );
		return nodeIn;
	}
	

	
	public N copy() {
		N out = defaultConstructor();
		NodeObj.nodeInitRoot( out );
		copyNodeTreeRecursiveFromTo( getInstance(), out );
		
		return out;
	}
	
	/**
	 * removes a node from a tree
	 * creates a tree copy, preserving removed node's children and tree structure
	 */
	public N decouple() {
		N out = defaultConstructor();
		NodeObj.nodeInitRoot( out );
		copyNodeTreeRecursiveFromTo( getInstance(), out );
		remove();
		return out;
		
	}
	
	
	/**
	 * makes an empty copy of tree structure recursively
	 * initial new node needs to have its own coredata obj set
	 * @param oldNode	- node to copy structure from
	 * @param newNode	- empty added child
	 */
	public static <E extends TreeNodeStruct<?,R>,R> void copyNodeTreeRecursiveFromTo( E oldNode, E newNode ) {
		newNode.setData(oldNode.getData());
		
		for( int i = 0; i < oldNode.getChildCount(); i++ ) newNode.addChild();
		for( int i = 0; i < oldNode.getChildCount(); i++ ) copyNodeTreeRecursiveFromTo( oldNode.get(i), newNode.get(i) );
	}

	
	
	/**
	 * changes node to a child of a new blank node
	 * returns new parent
	 * if doing operation on root node variable, do TreeNode<?> root = root.insertParent()
	 * 	this will set variable "root" as new parent
	 * 	if variable was root, it is not longer root after operation
	 * 	or do root.getRoot().printOperation() etc
	 */
	public N insertParent() {
		N newNode = defaultConstructor();
		if( isRoot() ) {
			for( N node : getInstance() ) node.depth++;
			shiftNodesRight( 0, 0 );
			newNode.setCore( getCore());
			newNode.size = 1;
			newNode.firstChild = 1;
			nodeList().add(0, newNode );
			nodeList().get(1).parentIndex = 0;
			return newNode.nodeList().get(0);
		}
		else {
			N me = copy();
			replaceWithTree( newNode ).addChild(me);
			return newNode;
		}
	}

	

	/**
	 * removes a child node
	 * works recursively to remove any children of child from tree
	 * @param index
	 * doesn't return node because all children are removed with operation
	 */
	
	public void remove() {
		bottomUpOperation( n -> {
			if( n.hasChildren() ) for( N child : n.getChildren() ) {
				n.nodeList().remove( child.index() );
				n.shiftNodesLeft(child.index());
			}
			n.size = 0;
			n.firstChild = -1;
		});
		parent().size--;
	}
	public void removeChild(int index) {
		get(index).remove();
	}

	public <E> void removeChild(int index, List<E> treeData) {
		if (get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < get(index).getChildCount(); i++)
				get(index).removeChild(i, treeData);
		int childIndex = get(index).index;
		size--;
		if (size == 0)
			firstChild = -1;
		nodeList().remove(childIndex);
		shiftNodesLeft(childIndex);
		treeData.remove(childIndex);
	}

	public void removeChildren() {
		for (int i = 0; i < getChildCount(); i++)
			removeChild(i);
	}

	public void shiftNodesRight(int index, int depth) { // addition
		if (index == totalSize())
			return;
		for (int i = 0; i < totalSize(); i++) {
			N curNode = nodeList().get(i);
			if (curNode.index >= index)
				curNode.index += 1;
			if (curNode.firstChild >= index && curNode.depth >= depth) // curNode.firstChild != -1 )
				curNode.firstChild += 1;
			if (curNode.parentIndex >= index)
				curNode.parentIndex += 1;
		}
	}

	public void shiftNodesLeft(int index) { // removal
		if (index == totalSize())
			return;
		for (int i = 0; i < totalSize(); i++) {
			N curNode = nodeList().get(i);
			if (curNode.index >= index)
				curNode.index--;
			if (curNode.firstChild > index && curNode.firstChild != -1)
				curNode.firstChild--;
			if (curNode.parentIndex >= index)
				curNode.parentIndex--;
		}
	}

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String multiLineString(Function<N, ?> fn) {
		String out = "";
		for (N n : getInstance())
			out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}

	public void printOperation() {
		printOperation( n -> n );
	}
	public void printOperation(Function<N, ?> fn) {
		for (N n : getInstance())
			System.out.println(createPrintFn(fn).apply(n).toString());
	}

	public Function<N, String> createPrintFn(Function<N, ?> addedString) {
		return n -> {
			Object val = addedString.apply(n);
			String space = "";
			for (int i = 0; i < n.depth; i++)
				space = "  " + space;
			return space + " - " + (val == null ? "(null) " : val.toString());
		};
	}
	
	// BUILDER FNS ///////////////////////////////////////////////////////////
	
	/**
	 * used for converting subclasses to universal basicnode structure
	 * traverse through tree, copying all core data to new nodes
	 * from this, one subclass can be built with structure of another
	 * leafNodeFn is used to create a specific node with data from referenced element, vs just a default node
	 */
	
	public <E extends TreeNodeStruct<E,?>> N buildWithStructureFrom( E nodeIn ) {
		copyFieldsFromTo( nodeIn,this );
		for( int i = 1; i < nodeIn.totalSize(); i++ ) {
//			N newNode = defaultConstructor( 0 );
			N newNode = defaultConstructor();//newNode( getInstance() );
			newNode.setCore(getCore());
			copyFieldsFromTo( nodeIn.nodeList().get(i), newNode );
//			nodeList().add( newNode );
//			getCore().attach( newNode );
		}
		return getInstance();
	}
	
	public <E extends TreeNodeStruct<E,?>> N buildWithStructureFromWithLeafFn( E nodeIn, Function<E,N> leafNodeFn ) {
		copyFieldsFromTo( nodeIn,this );
		for( int i = 1; i < nodeIn.totalSize(); i++ ) {
			N newNode = defaultConstructor();//newNode( getInstance() );
			newNode.setCore(getCore());
			if( nodeIn.nodeList().get(i).isLeaf() )
				newNode = leafNodeFn.apply(nodeIn.nodeList().get(i));
			else
				newNode= defaultConstructor();
			copyFieldsFromTo( nodeIn.nodeList().get(i), newNode );
//			getCore().attach( newNode );
		}
		return getInstance();
	}
	
	
	
	// OPERATIONS /////////////////////////////////////////
	
	public void topDownModOperation( Consumer<N> fn ) { // recurses operation to children, then runs fn
		fn.accept( getInstance() );
		if( hasChildren() ) for( N child : getChildren() ) child.topDownModOperation(fn); // used for ar calc
	}
	
	public void topDownModOperation( Consumer<N> fn, Predicate<N> checkFn ) { // recurses operation to children, then runs fn
		if( !checkFn.test(getInstance() ) ) return;
		fn.accept( getInstance() );
		if( hasChildren() ) for( N child : getChildren() ) child.topDownModOperation(fn,checkFn); // used for ar calc
	}
	
	public void topDownModOperation( Predicate<N> traverseFn, Predicate<N> runFn, Consumer<N> fn ) { // recurses operation to children, then runs fn
		if( !traverseFn.test(getInstance() ) ) return;
		fn.accept( getInstance() );
		if( hasChildren() ) for( N child : getChildren() ) child.topDownModOperation(traverseFn,runFn,fn); // used for ar calc
	}
	public <E> E topDownReturnOperation( Predicate<N> traverseFn, Predicate<N> runFn, Function<N,E> returnFn ) { // recurses operation to children, then runs fn
		if( runFn.test( getInstance() ) ) return returnFn.apply( getInstance() );
		if( hasChildren() ) for( N child : getChildren() )
			if( traverseFn.test( child ) ) return child.topDownReturnOperation(traverseFn,runFn,returnFn); // used for ar calc
		return null;
	}
	public <E> E topDownReturnOperation( Function<N,N> nextNodeFn, Function<N,E> returnFn ) { // recurses operation to children, then runs fn
		N next = null;
		try{ next = nextNodeFn.apply( getInstance() ); }
		catch( Exception E ) {}
		if( next != null )
			return next.topDownReturnOperation( nextNodeFn, returnFn ); // used for ar calc
		else return returnFn.apply( getInstance() );
	}
	public void bottomUpOperation( Consumer<N> fn ) { // recurses operation to children, then runs fn
		if( hasChildren() ) for( N child : getChildren() ) child.bottomUpOperation(fn); // used for ar calc
		fn.accept( getInstance() );
	}
	
	/**
	 * excludes start node
	 * for each child, recursively runs
	 */
	
	public void childOperation( Consumer<N> fn ) {
		if( hasChildren() ) for( N child : getChildren() ) {
			fn.accept( child );
			child.childOperation( fn );
		}
	}
	
	
	
	
//	// UTILITY FNS ////////////////////////////////////////
	
	public <E extends TreeNodeStruct<?,?>> void copyFieldsFromTo( E from, E to ) {
		to.parentIndex =  from.parentIndex;
		to.index =      from.index;
		to.firstChild = from.firstChild;
		to.size =       from.size;
		to.dataLoc =    from.dataLoc;
		to.depth =      from.depth;
	}
	
	// SORT FNS ///////////////////////////////////////////
	
	public void reverse() {
		reverse( nodeList().subList(firstChild, firstChild+size) );
	}
	public void shuffle() {
		shuffle( nodeList().subList(firstChild, firstChild+size) );
	}
	public void sort( Comparator<N> comparator ) {
		sort( nodeList().subList(firstChild, firstChild+size), comparator );
	}
	@Override
	public void customTreeUpdateFn() {
		for (int i = 0; i < getChildCount(); i++) {
//			get(i).indexInParent = i;
			get(i).index = firstChild + i;
			if (get(i).hasChildren())
				for (N node : get(i).getChildren())
					node.parentIndex = get(i).index;
		}
	}
	
	// INNER CLASSES ////////////////////////////////////////////////////////////////

	
	
	
	
	// PRINT FNS ////////////////////////////////////////////////////////////////////

	public void printTree() {
		printOperation(n -> n);
	}

	public String toString() {
		return "node index: " + index + ", depth: " + depth + ", childCt: " + size + ", fc: " + firstChild +
				", parentIndex: " + parentIndex + ", data: " + ( getData() != null ? getData().toString() : "null" ); // multiLineString(n -> "node");
	}

	public <E> void printList(List<E> inputList) {
		System.out.println(multiLineString(
				n -> (n.getElem(inputList) != null ? "node: " + n.getElem(inputList) : "node: (null)")));
	}
	
	public void printData() {
		System.out.println( "index: " + index +
				            ", parIndex: " + parentIndex +
				            ", firstChild: " + firstChild +
				            ", size: " + size + 
				            ", depth: " + depth );
	}

}
