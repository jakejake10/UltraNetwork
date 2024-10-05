package unCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import unCore.NodeObj.CoreData;
import utTypes.TreeNode;
import utTypes.SelectionNode.SelectionData;

public abstract class TreeNodeStruct<N extends TreeNodeStruct<N,D>, D> implements NodeObj<N, D>, Iterable<N>, 
	TreeBuilder<N,D>, Build {
	//COMMON
	CoreData<N,D> core;
	public D data;
	/**
	 * Identifier generator, used to get a unique id for each created tree node
	 */
	final UUID id = UUID.randomUUID();
	//OTHER
	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int childCt = 0;
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
	
	public void setDepth( int depth ) {
		this.depth = depth;
		System.out.println( "settingDepth" );
		for( N child : getChildren() ) child.setDepth( depth+1 );
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
	
	//higher level node operations
	@Override
	public void insertNodeFn( N input ) { 
		addChild( input );
	}
	
	
//	// added, for insertparent problems?
//	@Override
//	public N getInstance() {
//		return nodeList().get(index);
//	}
	
	
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
	
	/**
	 * returns random node from the subtree of node
	 * @return
	 */
	public N getRandom() {
//		return nodeList().get(new Random().nextInt(nodeList().size()));
		if( !checkNonLeafErrorMSG("getRandom()")) return null;
		float sumOfWeight = size();
		float rnd = (float)Math.random() * sumOfWeight;
		float cumulativeProbability = 0;
		for( N nc : getInstance() ) {
			cumulativeProbability ++;
			if( rnd < cumulativeProbability )  return nc;
		}
		return null;
	}
	
	public N getRandomLeaf() {
		List<N> leafs = getLeafs();
		return leafs.get( (int)Math.floor( Math.random() * leafs.size() ) );
	}


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
		return get(childCt - 1);
	}

	public List<N> getChildren() {
//		List<N> out = new ArrayList<>();
//		for (int i = 0; i < size; i++)
//			out.add(get(i)); // can't use iterable if used in iterable class?
		return IntStream.range(firstChild, firstChild+childCt)
				.mapToObj(i -> nodeList().get(i) )
				.collect(Collectors.toList());
	}
	
	public N getRandomChild() {
		return get( (int)Math.floor(Math.random() * childCt ) );
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

	public List<N> getNodesAtDepth(int targetLevel) {
		return getNodesAtDepth(new ArrayList<N>(), targetLevel);
	}

	public List<N> getNodesAtDepth(List<N> data, int targetLevel) {
		if (depth == targetLevel)
			data.add(getInstance());
		if (depth < targetLevel)
			for (N node : getChildren())
				node.getNodesAtDepth(data, targetLevel); // can't use iterable if used in iterable class?
		return data;
	}

	public <E> List<E> getChildData(Function<N, E> fn) {
		List<E> out = new ArrayList<>();
		for (N child : getChildren())
			out.add(fn.apply(child));
		return out;
	}

	public int getChildCount() {
		return childCt;
	}

	public int leafSize() {
//		int out = 0;
//		while( leafs().iterator().hasNext() ) out++ ;
//		return out;
		return getLeafs().size();
	}

	public String locationCode() {
		if( isRoot() ) return "";
		return locationCodeFn("");
	}
	
	public String locationCodeFrom( N node ) {
		if( isRoot() ) return "";
		return locationCodeFromFn("", node );
	}

	String locationCodeFn(String code) {
		if (hasParent())
			return parent().locationCodeFn(Integer.toString(indexInParent()) + code);
		else
			return code;
	}
	
	String locationCodeFromFn(String code, N node ) {
		if ( getInstance().equals(node)) return code;
		else if( !hasParent() ) throw new RuntimeException("locationCodeFrom: node not found!");	
		else
			return parent().locationCodeFromFn(Integer.toString(indexInParent()) + code, node );
	}
	
	public N get(String locationCode) {
		if (locationCode.equals(""))
			return getInstance();
		if (locationCode.length() > 1) {
			String nextIndex = locationCode.substring(0, 1);
			locationCode = locationCode.substring(1);
			
			int loc = Integer.parseInt(locationCode);
			System.out.println( "locCode: " + locationCode + "int: " + loc + ", next Index: " + nextIndex );
			return get(nextIndex).get(loc);
		}
		return get(Integer.parseInt(locationCode));
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
	public N addChild( N childIn, int...indexIn ) {
		addChildTreeDataFn( childIn, null, indexIn );
		return indexIn == null || indexIn.length == 0 ? lastChild() : get( indexIn[0] );
	}
	public N addChildAtIndex( int indexIn ) {
		addChildTreeDataFn( null, null, indexIn );
		return get(indexIn);
	}
	public N addChildWithData( D dataIn, int...indexIn ) {
		addChildTreeDataFn( null, dataIn, indexIn );
		return indexIn == null || indexIn.length == 0 ? lastChild() : get( indexIn[0] );
	}
	
	// not working properly, commented out
//	public N addChild( List<D> dataIn ) {
//		for( D data : dataIn ) addChildTreeDataFn( null, data  );
//		return lastChild();
//	}
//	
	
	public N addChild( N childIn, D dataIn, int...indexIn ) {
		addChildTreeDataFn( childIn, dataIn, indexIn );
		return indexIn == null || indexIn.length == 0 ? lastChild() : get( indexIn[0] );
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
//		System.out.println("beforeSize = " + getRoot().size() );
		List<N> foundChildren = new ArrayList<>();
		if( child == null ) child = defaultConstructor(); //newNode( new NoInput() );
		else {
			if( child.hasChildren() ) foundChildren = child.getChildren();	// need to pull these out and reconnect
			if( child.getData() != null && dataIn == null ) dataIn = child.getData();
		}
		
		int indexInParent = indexIn.length == 0 || !hasChildren() || indexIn[0] > childCt ? childCt : indexIn[0];
		if (!hasChildren())	firstChild = totalSize();
		child.childCt = 0; // if child has children, it's size needs to be reset to 0, so adding back children shows proper size
		child.firstChild = -1;	// if child has children, first child needs to be reset to -1 for future add ops to work properly
		child.index = firstChild + indexInParent;
		child.parentIndex = this.index;
		child.depth = depth + 1;
		shiftNodesRight(child.index, indexInParent, child.depth );
		nodeList().add(child.index, child); // add to index after loc, and after any pre existing children
		child.setCore(getCore());
		
//		if( dataIn == null ) child.generateData();
		if( dataIn != null ) child.setData( dataIn );
		childCt++;
		subClassAddChildUpdate();
		if( foundChildren.size() > 0 ) for( N node : foundChildren ) child.addChild( node );
	}
	
	
	public void subClassAddChildUpdate() {
		// add any necessary updates in child class
	}
	
	public void subClassRemoveChildUpdate() {
		// add any necessary updates in child class
	}
	
	// indexInParent(){ return index - parent().firstChild; }
	void shiftNodesRight(int chIndex, int iip, int depth) { // addition
		if (chIndex == totalSize())
			return;
		int lastChangedParent = -2;
		for (int i = 0; i < totalSize(); i++) {
			N curNode = nodeList().get(i);
			if (curNode.index >= chIndex) {
				if (curNode.indexInParent() == 0) {
					if ((curNode.index > chIndex || iip != 0) && curNode.parent().index != lastChangedParent) {
						curNode.parent().firstChild += 1;
						lastChangedParent = curNode.parent().index();
					}
				}
				curNode.index += 1;
			}
			if (curNode.parentIndex >= chIndex)	curNode.parentIndex += 1;
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
	
	
	// need both?
	@Override
	public <R extends NodeObj<?,?>> void transferNodeDataTo( R input ) {
		if( input instanceof TreeNodeStruct ) {
			@SuppressWarnings("unchecked")
			TreeNodeStruct<N,?> tnInput = (TreeNodeStruct<N,?>) input;
			tnInput.childCt = childCt;
			tnInput.firstChild = firstChild;
			tnInput.index = index;
			tnInput.depth = depth;
			tnInput.parentIndex = parentIndex;
		}
	}
	
	/**
	 * static transfer method to transfer between different subclasses
	 * does not transfer coredata
	 * does not transfer nodeData
	 */
	public static <E extends TreeNodeStruct<?,?>> void copyFieldsFromTo( E from, E to ) {
		to.parentIndex =  from.parentIndex;
		to.index =        from.index;
		to.firstChild =   from.firstChild;
		to.childCt =      from.childCt;
		to.dataLoc =      from.dataLoc;
		to.depth =        from.depth;
	}
	
	public static <E extends TreeNodeStruct<E,M>,M> void copyCoreDataFromTo( E from, E to ){
		to.setCore( from.getCore() );
	}
			
	public static <E extends TreeNodeStruct<E,M>,M> void copyDataFromToWithFn( E from, E to, Function<M,M> dataTransferFn ){
		to.setData( dataTransferFn.apply( from.getData() ) );
	}
	
	public static <E extends TreeNodeStruct<E,M>,M> void copyDataFromToWithFn( E from, E to, BiFunction<E,M,M> toNodeFromDataFn ){
		to.setData( toNodeFromDataFn.apply( to, from.getData() ) );
	}
	
	/**
	 * copies tree structure to new root node with its own core data
	 */
	
	public N copyTree() {
		N out = defaultConstructor();
		NodeObj.nodeInitRoot( out );
		for( int i = 0; i < nodeList().size()-1; i++ ) {
			N newNode = defaultConstructor();
			newNode.setCore( out.getCore() );
			out.nodeList().add( newNode );
		}
		parallelTreeOperationFromTo( getInstance(), out, (from,to) -> from.copyFieldsTo(to) );
		return out;
	}
	
	public N copyTreeWithData() {
		N out = copyTree();
		parallelTreeOperationFromTo( getInstance(), out, (from,to) -> copyDataFromToWithFn( from,to, d -> d ) );
		return out;
	}
	
	/**
	 * dataFn would typically be used for a deep copy method within data object
	 * 	
	 *  */
	public N copyTreeWithData( Function<D,D> dataFn ) {
		N out = copyTree();
		parallelTreeOperationFromTo( getInstance(), out, (from,to) -> copyDataFromToWithFn( from,to, dataFn ) );
		return out;
	}

	public N copyTreeWithData( BiFunction<N,D,D> toNodeFromDataFn ) {
		N out = copyTree();
		parallelTreeOperationFromTo( getInstance(), out, (from,to) -> copyDataFromToWithFn( from,to, toNodeFromDataFn ) );
		return out;
	}
	
	
	public N copyNodeFields() {
		N out = defaultConstructor();
		copyFieldsFromTo(getInstance(), out);
		
		return out;
	}
	
	
	/**
	 * transfer mthod specific to same class types, which includes a subclass transfer method
	 */
	public void copyFieldsTo( N to ) {
		copyFieldsFromTo( getInstance(), to );
		transferSubclassFieldsTo( to );
	}
	
	public void transferSubclassFieldsTo( N input ) {
		// override in subclass to add aditional fields if needed
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
		if( !isRoot() ) {
			removeChildren();
			int iip = indexInParent();
			N parent = parent();
			remove();
			parent.addChild( nodeIn, null, iip );
		}
		else {
			setCore(nodeIn.getCore());
			nodeIn.transferNodeDataTo(getInstance());
			setData(nodeIn.getData());
		}
		return nodeIn;
	}
	
	// NOW LOCATED IN NODEOBJ
//	/**
//	 * replaces node with nodeIn
//	 * only swaps node for nodeIn, nodeIn's children ignored
//	 * all children of original node preserved
//	 * nodeIn children are ignored
//	 */
//	public N replaceWithNode( N nodeIn ) {
//		nodeList().set(index, nodeIn);
//		transferNodeDataTo( nodeIn );
//		nodeIn.setCore(getCore() );
//		return nodeIn;
//	}
	

//	/**
//	 * copies node tree recursively to a new default root node
//	 */
//	public N copy() {
//		N out = defaultConstructor();
//		NodeObj.nodeInitRoot( out );
//		copyNodeTreeRecursiveFromTo( getInstance(), out );
//		return out;
//	}
//	/**
//	 * copies node tree with a function to transfer data
//	 */
//	public N copy( Function<D,D> dataCopyFn ) {
//		N out = defaultConstructor();
//		NodeObj.nodeInitRoot( out );
//		copyNodeTreeRecursiveFromTo( getInstance(), out );
////		out.setData( dataCopyFn.apply( getData() ) );
//		parallelTreeOperationFromTo( getInstance(), out, (nOriginal,nNew) -> nNew.setData( dataCopyFn.apply( nOriginal.getData() ) ) );
//		return out;
//	}
//	
//	/**
//	 * copies node tree with a function to transfer data
//	 */
//	public N copy( BiFunction<N,D,D> dataCopyFn ) {
//		N out = defaultConstructor();
//		NodeObj.nodeInitRoot( out );
//		copyNodeTreeRecursiveFromTo( getInstance(), out );
////		out.setData( dataCopyFn.apply( getData() ) );
//		parallelTreeOperationFromTo( getInstance(), out, (nOriginal,nNew) -> nNew.setData( dataCopyFn.apply(nOriginal, nOriginal.getData() ) ) );
//		return out;
//	}
//	
	
	public N copyNode() {	// no coredata involved
		N out = defaultConstructor();
		copyFieldsFromTo(getInstance(), out);
		out.setData(getData());
		return out;
	}
//	
//	public <E extends TreeNodeStruct<E,R>,R> E map( Function<? super D, ? extends R> mapper ) {
//		E out = ((E)defaultConstructor()).setData( mapper.apply( getData()));
//		NodeObj.nodeInitRoot( out );
//		mapNodeTreeRecursiveFromTo( getInstance(), out, mapper );
//		return out;
//	}
//	
//	/**
//	 * copies subtree of node to a new node structure with no additional data
//	 * @return
//	 */
//	public TreeNode<Void> copyStructure(){
//		TreeNode<Void> out = new TreeNode<>();
//		parallelTreeOperationFromTo( getInstance(), out, (fr,to) ->{ for(int i =0; i < fr.getChildCount(); i++ ) to.addChild(); } );
//		return out;
//	}
//	
//	/**
//	 * removes a node from a tree
//	 * creates a tree copy, preserving removed node's children and tree structure
//	 */
//	public N decouple() {
//		N out = defaultConstructor();
//		NodeObj.nodeInitRoot( out );
//		copyNodeTreeRecursiveFromTo( getInstance(), out );
//		remove();
//		return out;
//		
//	}
//	
//	
//	/**
//	 * makes an empty copy of tree structure recursively
//	 * initial new node needs to have its own coredata obj set
//	 * @param oldNode	- node to copy structure from
//	 * @param newNode	- empty added child
//	 */
//	public static <E extends TreeNodeStruct<?,R>,R> void copyNodeTreeRecursiveFromTo( E oldNode, E newNode ) {
//		newNode.setData(oldNode.getData());
//		
//		for( int i = 0; i < oldNode.getChildCount(); i++ ) newNode.addChild();
//		for( int i = 0; i < oldNode.getChildCount(); i++ ) copyNodeTreeRecursiveFromTo( oldNode.get(i), newNode.get(i) );
//	}
//	
//	/**
//	 * map version of copyNodeTreeRecursive 
//	 * uses mapper from java Stream source
//	 * maps tree to another datatype
//	 */
//	public static <M extends TreeNodeStruct<?,R>,R,N extends TreeNodeStruct<?,Q>,Q> void mapNodeTreeRecursiveFromTo( M oldNode, N newNode,
//			Function<? super R, ? extends Q> mapper ) {
//		
//		newNode.setData( mapper.apply( oldNode.getData() ) );
//
//		for( int i = 0; i < oldNode.getChildCount(); i++ ) newNode.addChild();
//		for( int i = 0; i < oldNode.getChildCount(); i++ ) mapNodeTreeRecursiveFromTo( (M)oldNode.get(i), (N)newNode.get(i), mapper );
//	}
	
	

	
	
	/**
	 * changes node to a child of a new blank node
	 * returns new parent
	 * if doing operation on root node variable, do TreeNode<?> root = root.insertParent()
	 * 	variables that call this will now point to new inserted parent
	 */

	
	public N insertParent() {
		return insertParent( defaultConstructor() );
	}

	public N insertParent( N insertNode ) {
		if( isRoot() ) {
//			System.out.println("root!");
			for( N node : getInstance() ) node.depth++;
			shiftNodesRight( 0, -1, 0 );
			insertNode.setCore( getCore());
			insertNode.childCt = 1;
			insertNode.firstChild = 1;
			nodeList().add(0, insertNode );
			nodeList().get(1).parentIndex = 0;
			copyFieldsFromTo( nodeList().get(1), getInstance() );
			shiftFocus( getRoot() );
			return nodeList().get(0);
		}
		else {
//			System.out.println("not root!");
//			N oldData = copyNode();
//			oldData.setCore(getCore());
			int iip = indexInParent();
			N oldMe = copyTree();		 // to be added to new parent
			N par = parent();
			remove();
			par.addChildAtIndex( iip );
			par.get( iip ).addChild( oldMe );
			copyFieldsFromTo( oldMe, getInstance() );	// needs to be set before shift focus? otherwise breaks with leaf node operation
			shiftFocus( par.get(iip).get(0) );	// added
			return getInstance();
			
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
			n.childCt = 0;
			n.firstChild = -1;
		});
		nodeList().remove( index );	// added
		shiftNodesLeft(index());
		parent().childCt--;
		if( parent().childCt == 0 ) parent().firstChild = -1;	
		subClassRemoveChildUpdate();
	}
	
	public void removeChild(int index) {
		get(index).remove();
	}

	public <E> void removeChild(int index, List<E> treeData) {
		if (get(index).hasChildren()) // recursively clear all contained nodes bottom up
			for (int i = 0; i < get(index).getChildCount(); i++)
				get(index).removeChild(i, treeData);
		int childIndex = get(index).index;
		childCt--;
		if (childCt == 0)
			firstChild = -1;
		nodeList().remove(childIndex);
		shiftNodesLeft(childIndex);
		treeData.remove(childIndex);
	}

	public void removeChildren() {
		for (int i = getChildCount()-1; i > -1 ; i--) removeChild(i);
	}

	
	
	
	/**
	 * changes a variable reference to a new node in the tree
	 * cannot shift focus between trees
	 * @param node
	 */
	public void shiftFocus( N node ) {
		N origNode = getInstance().copyNode();		// copy original and link core
		origNode.setCore(getCore());
		copyFieldsFromTo( node, getInstance() );	// this node now references new node fields
		setData( node.getData() );
		nodeList().set( origNode.index, origNode );
		nodeList().set( index, getInstance() );
	}
	
	/**
	 * how does this work?
	 * initial variable goes from an actual node to just a reference w exact data as the node?
	 * variable becomes only a reference?
	 * how to get actual node?
	 * think its not possible, used it for insert parent, will try to work around
	 */
//	public static <E extends TreeNodeStruct<E,R>,R> void setFocusFromTo( E nodeFrom, E nodeTo ) {
//		E newNode = nodeFrom.copyNode(); // create disconnected reference
//		nodeFrom = nodeFrom.defaultConstructor();
//		TreeNodeStruct.copyFieldsFromTo( nodeFrom, nodeTo );
//		//		nodeFrom = nodeTo;
//	}

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
		int startNodeDepth = depth;	// if printing sub tree, makes first line not indented 
		return n -> {
			Object val = addedString.apply(n);
			String space = "";
			for (int i = 0; i < n.depth - startNodeDepth; i++)
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
	
	
	
	
	
	
//	public <E extends TreeNodeStruct<E,R>, R> E mapNode( Function<? super D, ? extends R> mapper ) {
//		@SuppressWarnings("unchecked")
////		E out = ((E)copy()).setData( mapper.apply(getData()));
//		
//		return out;
//	}
	
	
	
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
	
	public static <E extends TreeNodeStruct<E,R>,R,T extends TreeNodeStruct<T,Q>,Q> 
	void parallelTreeOperationFromTo( E tr1, T tr2, BiConsumer<E,T> fn ) {
		for( E node : tr1 ) fn.accept(node, tr2.nodeList().get(node.index) );
	}
	
	public N childSelectionOperation( Predicate<N> continueFn, Function<N,Integer> selectionFn ) {
		int cIndex = selectionFn.apply(getInstance());
		return !continueFn.test(getInstance()) ? getInstance() : cIndex > -1 ? get(cIndex).childSelectionOperation( continueFn, selectionFn ) : getInstance();
	}
//	public static <E extends TreeNodeStruct<E,R>,R> void parallelTreeOperationFromTo( E tr1, E tr2, BiConsumer<R,R> fn ) {
//		for( E node : tr1 ) {
//			if( node.isRoot() ) fn.accept(node, tr2 );
//			else fn.accept(node, tr2.get(node.locationCodeFrom( tr1 )));
//		}
//	}
	
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
	
	public void leafOperation( Consumer<N> fn ) {
		if( hasChildren() ) for( N node : getLeafs() ) {
			fn.accept( node );
		}
		else fn.accept(getInstance());
	}
	
	
	
	
//	// UTILITY FNS ////////////////////////////////////////
	
	
	
	// SORT FNS ///////////////////////////////////////////
	
	public void reverse() {
		reverse( nodeList().subList(firstChild, firstChild+childCt) );
	}
	public void shuffle() {
		shuffle( nodeList().subList(firstChild, firstChild+childCt) );
	}
	public void sort( Comparator<N> comparator ) {
		sort( nodeList().subList(firstChild, firstChild+childCt), comparator );
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

	@Override
	public String toString() {
		return "node index: " + index + ", depth: " + depth + ", childCt: " + childCt + ", fc: " + firstChild +
				", parIndex: " + parentIndex + ", indexInPar" + indexInParent() + ", data: " + ( getData() != null ? getData().toString() : "null" ); // multiLineString(n -> "node");
	}
	
	// gets TreeNode's toString, from subclass where it is overridden
	public final String nodeDataString() {
		return "node index: " + index + ", depth: " + depth + ", childCt: " + childCt + ", fc: " + firstChild +
				", parIndex: " + parentIndex + ", indexInPar" + indexInParent() + ", data: " + ( getData() != null ? getData().toString() : "null" ); // multiLineString(n -> "node");
	}

	public <E> void printList(List<E> inputList) {
		System.out.println(multiLineString(
				n -> (n.getElem(inputList) != null ? "node: " + n.getElem(inputList) : "node: (null)")));
	}
	
	public void printData() {
		System.out.println( "index: " + index +
				            ", parIndex: " + parentIndex +
				            ", firstChild: " + firstChild +
				            ", size: " + childCt + 
				            ", depth: " + depth );
	}

}
