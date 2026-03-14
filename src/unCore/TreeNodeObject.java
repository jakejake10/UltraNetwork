package unCore;

import java.util.ArrayDeque;
import java.util.ArrayList;


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.*;
import java.util.stream.*;

//import processing.core.PApplet;
//import processing.data.JSONArray;
//import processing.data.JSONObject;
import unCore.TreeNodeFunctions.TreeBuilder;

/*
 * TODO: contiguous subtree storage
 * 	- simplifies many operations, faster subtree queries
 */

public interface TreeNodeObject<N extends TreeNodeObject<N> & Iterable<N>>
extends TreeNodeQueries<N>, TreeNodeTraversal<N>, TreeNodeCopy<N>,TreeNodeMutations<N> {	// , TreeNodeTraversal<N>, TreeNodeMutations<N>, TreeNodeCopy<N>
	// SUGGESTED FIELDS
	// SingularTreeData<N> core;
	// D data;
	// public int index;
	// public int parentIndex = -1; // index in tree.nodes
	// public int firstChild = -1;
	// public int childCt = 0;
	// public int depth = 0;

	// ABSRACT METHODS /////////////////////////////////////

	public int getIndex();

	public void setIndex(int index);

	public int getParentIndex();

	public void setParentIndex(int index);

	public int getFirstChildIndex();

	public void setFirstChildIndex(int index);

	public int getDepth();

	public void setDepth(int depth);

	public int getChildCount();

	public void setChildCount(int count);

	public N getInstance();

	public N defaultConstructor();

	public SingularTreeData<N> getCoreFn();

	public void setCore(SingularTreeData<N> input);

	default void initNodeFields() {
		setIndex(0);
		setParentIndex(-1);
		setFirstChildIndex(-1);
		setDepth(0);
		setChildCount(0);
	}

	// OPTIONAL METHODS /////////////////////////////////////

	/*
	 * - when node is copied in superclass, only essential fields are copied - use
	 * this method to transfer any additional fields to a newly copied node - how to
	 * handle when using abstract / multiple different subclasses? bounded generic?
	 */
	public default void transferSubclassFieldsTo(N newNode) {
	}

	/*
	 * - set to false if you don't want subclass to allow children - for use cases
	 * with a group subclass and various type subclasses
	 */
	public default boolean allowChildren() {
		return true;
	}
	
	/*
	 * this is called in the parent node after a child is added
	 * override in subclass for custom behavior
	 * things like adding a child's value to parent's total
	 */
	public default void addedChildMod() {
		// override to modify newly node after addChild() operation
	}
	
	/*
	 * specify additional text and line breaks for print operations
	 * example: if not root or leaf, return "\n"
	 * this will add an empty line between node text outputs
	 */
	public default String getPrintInfoLineBreak() {
		return "";
	}
//	
//	/*
//	 * use method for specifying what data to import from a json file
//	 * works with the build from json method, so json should follow same conventions
//	 * for trees of multiple subtypes, each subtype can have different method
//	 */
//	
//	public default void modifyFromJSON( JSONObject input ) {
//		
//	}
	

	public default N initCore() {
		setCore(new SingularTreeData<>());
		getCore().nodeList.add(getInstance());
		return getInstance();
	}
	
	public default SingularTreeData<N> getCore(){
		if( getCoreFn() == null ) initCore();
		return getCoreFn();
	}
	
	

	

//  // CHECK FNS
//  // ///////////////////////////////////////////////////////////////////////////

	public default boolean hasParent() {
		return getParentIndex() > -1;
	}

	public default boolean hasChildren() {
		return getFirstChildIndex() > -1;
	}

	public default boolean isLeaf() {
		return getFirstChildIndex() == -1;
	}

	public default boolean isRoot() {
		return getParentIndex() == -1;
	}

	
	
	

	// GETTERS ////////////////////////////////////////////////////////////////////

	public default N get(int indexIn) {
		if (getChildCount() < 1)
			throw new UnsupportedOperationException(getInstance() + "cannot get child of a node with 0 children");
		return getCore().nodeList.get(getFirstChildIndex() + indexIn);
	}
	
	public default N getParent() {
		return getCore().nodeList.get(getParentIndex());
	}

	public default int getIndexInParent() {
		if (!hasParent())
			return -1;
		else
			return getIndex() - getParent().getFirstChildIndex();
	}

	public default N getRoot() {
		return getCore().nodeList.get(0);
	}

	
	
	/*
	 * for a performance boost use indexed iteration instead:
	 * for (int i = 0; i < getChildCount(); i++) {
		    N child = get(i);
		    ...
		}
	 */
	public default List<N> getChildren() {
		return IntStream.range(0, getChildCount()).mapToObj(i -> get(i)).collect(Collectors.toList());
	}
	
	
	
	public default N getLastChild() {
	    return getChildCount() == 0 ? null : get(getChildCount() - 1);
	}	
	
	

	
	// BUILD FNS ///////////////////////////////////////////////

	public default TreeBuilder<N> buildTree() {
		return new TreeNodeFunctions.TreeBuilder<N>(getInstance());
	}

	

	  
	
	
	// DATA LIST FUNCTIONS //////////////////////////////////////////////////////////////

	public default <E> List<E> createDataList(E value) {
		return IntStream.range(0, getTotalSize()).mapToObj(i -> value).collect(Collectors.toList());
	}
	
	public default <E> List<E> createDataList(Function<N,E> fn ) {
		List<E> out = this.<E>createDataList((E) null);
		for( N node : getInstance() ) out.set( node.getIndex(), fn.apply(node));
		return out;
	}

	public default <D> void applyDataList(N node, List<D> dataList,
			BiConsumer<N, D> fn) {
		if (node.getTotalSize() != dataList.size())
			throw new UnsupportedOperationException("data list size not equal to node size");
		node.forEachNode(dataList, (dList, n) -> fn.accept(n, dList.get(n.getIndex())));
	}

	public static <I, O> List<O> convertDataList(List<I> dataList, Function<I, O> convertFn) {
		return dataList.stream().map(i -> convertFn.apply(i)).collect(Collectors.toList());
	}
	
	public default <D> void dataListOperation( List<D> dataList, BiConsumer<N,D> fn ) {
		forEachNode( dataList, (dList,n) -> {
			fn.accept(n,dList.get(n.getIndex()));
		} );
	}
	
	public default <E> void dataListLeafOperation( List<E> data, BiConsumer<N,E> fn ) {
		if( data.size() != getLeafCount() ) throw new UnsupportedOperationException("data list must be equal to leaf count" );
		List<N> leafs = getLeafs();
		for( int i = 0; i < leafs.size(); i++ ) fn.accept( leafs.get(i), data.get(i) );
	}
	
	public default <E> E getData( List<E> dataList ){
		return dataList.get( getIndex() );
	}
	public default <E> E setData( E data, List<E> dataList ){
		return dataList.set( getIndex(), data );
	}
	public default <E> E modifyData( Function<E,E> dataFn, List<E> dataList ){
		return dataList.set( getIndex(), dataFn.apply(dataList.get(getIndex())) );
	}

	// PRINT FNS ////////////////////////////////////////////////

	public default String nodeString() {
		return "node | index: " + getIndex() + " | depth: " + getDepth() + " | childCt " + getChildCount()
				+ " | firstChild: " + getFirstChildIndex() + " | indexInParent: " + getIndexInParent();
	}

	public default String getTreeStringPreface() {
		return new String(new char[getDepth() + 1]).replace("\0", "  ") + "- ";
	}

	public default String getTreeString(Function<N, Object> nodeToPrintInfo) {
		String out = "";
		for (N cur : getInstance()) {
			String lineBreak = cur.getPrintInfoLineBreak();
			Object printData = nodeToPrintInfo.apply(cur);
			out += lineBreak+cur.getTreeStringPreface() + (printData != null ? printData.toString() : "null") + "\n";
		}
		return out;
	}

	public default void printOperation() {
		printOperation(n -> n);
	}

	public default void printOperation(Function<N, Object> nodeToPrintInfo) {
		System.out.println(getTreeString(nodeToPrintInfo));
	}

}
