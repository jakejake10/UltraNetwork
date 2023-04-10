package utCore;

import java.util.List;
import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.*;

import pFns_general.PFns;
import utCore.AbstractNode.DFTTraversal;


//import processing.core.*;

public abstract class AbstractTree<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N> {
	public List<N> nodes = new ArrayList<>();	// linkedlist instead? better for removing nodes w many children?
	public TreeFns<T,N> fns = new TreeFns<>();
		
	
	public AbstractTree() {
//		setRoot(); // root node always at 0;
		
//		root.tree = getInstance();
		
	}
	
	/*
	 * must be called to create tree
	 */
	public T initRoot() {
		N root = nodeDefaultConstructor();
		root.tree = getInstance();
		root.myLoc = 0;
		root.depth = 0;
		nodes.add( root );
		return getInstance();
	}
	
	public T initRoot( N startNode ) {
		startNode.tree = getInstance();
		startNode.myLoc = 0;
		startNode.depth = 0;
		nodes.add( startNode );
		return getInstance();
	}
	
//	
//	  <E extends AbstractTree<T,N> E createContents(Class<E> clazz) {
//	        return clazz.newInstance();
//	    }
//	
	
	// ABSTRACT FNS ///////////////////////////////////////////////////
	
	public abstract T getInstance();
	public abstract N nodeDefaultConstructor();
	

	// GET FNS ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	public N root() {
		return nodes.get(0);
	}

	public int size() {
		return nodes.size();
	}

	public int leafSize() {
		return root().leafSize();
	}
	

	public N get(int index) {
		return nodes.get(index);
	}

	public List<N> getLeafs() {
		return root().getLeafs();
	}
	
	public List<N> getDepth( int targetDepth ){
		return root().getDepth( targetDepth );
	}

	
	// LIST FUNCTIONS /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	
//	public <E> List<E> makeList( Function<N, E> fn ) {	// for recursive lambdas, just make new lambda
//		List<E> out = new ArrayList<E>();
//		for (N n : this)
//			out.add(fn.apply(n));
//		return out;
//	}
	
	
//	public <E> List<E> makeList( Function<N, E> fn ) {	// for recursive lambdas, just make new lambda
//		List<E> out = new ArrayList<E>();
//		for ( N node : this ) {
//			E val = fn.apply( node );
//			if( val != null )out.add( val );
//		}
//		return out;
//	}
//	
//	public <E> List<E> makeOrderedList( E rootData, BiConsumer<N,List<E>> fn ) {	// for recursive lambdas, just make new lambda
//		List<E> out = new ArrayList<E>();
//		out.add(rootData);
//		while( out.size() < size() ) out.add(null);
//		for ( N node : this ) fn.accept( node, out ); // dft
// 		return out;
//	}
	
//	public <E> List<E> generateLeafImport( List<E> inputData, int maxChildCount, List<E> treeData ) {
//		if( nodes.size() ==1 ) 
//		if( treeData.size() < myTree.size() ) fillList( treeData );
//		
//		
//		int[] childGroups = PFns.divideToCtEqually( inputData.size(), maxChildCount );
//		
//		return treeData;
//	}
	
	
	


	

	// ITERATOR //////////////////////////////////////////////
	
	public Iterator<N> iterator() {
		return root().iterator();
	}
	
	public Iterable<N> leafs(){
		return leafs( root() );
	}
	public Iterable<N> leafs( N baseNode ){
		return baseNode.leafs();
	}
	
	
	// TREE BUILDER
	// //////////////////////////////////////////////////////////////////////////

//	public  TreeBuilder build() {
//		return new TreeBuilder();
//	}
	

	
	
	
	// TREE BUILDER
	// //////////////////////////////////////////////////////////////////////////

//	public TreeBuilder<Void> build() {
//		return new TreeBuilder<Void>();
//	}
//
//
//	public <E>  TreeBuilder<E> build(E data, BiConsumer<N, E> buildFn) {
//		return new TreeBuilder<E>(data, buildFn);
//	}

	
//	public class TreeBuilder<V> {
//		public N targetNode;
//		public BiConsumer<N, V> buildFn;
//		public V data;
////		public Iterable<N> traversalOrder = dft();
//		
////		Function<Node,String> splitType; // need subclass?
//		Function<N,Integer> depthFn;
//		Function<N,Integer> childCountFn;
//		BiConsumer<N,N> addChildFn = (n,c) -> n.addChild(c);
//		
//
//		public TreeBuilder() {
//			this.targetNode = root();
//		}
//
//		public TreeBuilder(V data, BiConsumer<N, V> buildFn) {
//			this.data = data;
//			this.buildFn = buildFn;
//			this.targetNode = root();
//		}
//
//		// SETTER FNS
//		// ///////////////////////////////////////////////////////////////////
//
//		public TreeBuilder<V> setFn(BiConsumer<N, V> buildFnIn) {
//			this.buildFn = buildFnIn;
//			return this;
//		}
//
//		public TreeBuilder<V> setData(V data) {
//			this.data = data;
//			return this;
//		}
//
//		public TreeBuilder<V> setNode(N targetNode) {
//			this.targetNode = targetNode;
//			return this;
//		}
//
////		public TreeBuilder<T> makeTraversalFn() {	// traversal iterator runs buildfn before add children to queue
////			if (buildFn == null)
////				throw new IllegalStateException("build fn must be assigned before setting as traversal");
////			this.isTraversalFn = true;
////			traversalOrder = dft(data, buildFn);
////			return this;
////		}
//
//		// TERMINAL FNs ///////////////////////////////////////////////////////////
//
//		public void generate() {	// if already has children, move to children without running fn?
//			buildFn.accept(targetNode, data);
//		}
////		public void generateForEach() {
////			traversalOrder = dft(data, buildFn);
////			for (N n : traversalOrder) continue;
////		}
//
//		
//		// TYPE SPECIFIC FNS ///////////////////////////////////////////////////////
//
//		public TreeBuilder<Integer[]> leafChildCount(int leafs, int maxChildren) {
//			return new TreeBuilder<Integer[]>(new Integer[] { leafs, maxChildren }, fns.makeLeafs).setNode(targetNode);
//		}
//		
//		BiConsumer<N,V> recursiveBuild = ( n, t ) -> {
//			if( n.depth < depthFn.apply(n) ) {
//				for( int i = 0; i < childCountFn.apply(n); i++ )
//					n.addChild();
//			}
//				
//		};
//		
//		public interface FunctionalBuilder{
//			
//		}
//	}
	
	
	// NEW FNS ///////////////////////////////////////////////////////////////////////
	
	
	public void generateTree( int leafCount, int... childCount) {
		if (childCount.length == 1 && childCount[0] == 1)
			return;
		sendToLeafs( root(), leafCount, childCount );
	}
	

	public <E> void generateTree( List<E> data, int... childCt) {
		List<Integer> indexList = new ArrayList<>();
		for (int i = 0; i < data.size(); i++)
			indexList.add(i);
		sendToLeafs( root(), indexList, childCt);
	}
	
	public void sendToLeafs( N n, int input, int[] childCt) {
		if (input == 1)
			return;
		else if (input < childCt[0])
			for (int i = 0; i < input; i++)
				n.addChild();
		else {
			int[] childGroups = PFns.splitEvenly(input, childCt[0]);
			int[] newChildCt = childCt;
			if (childCt.length > 1) {
				newChildCt = new int[childCt.length - 1];
				for (int i = 0; i < newChildCt.length; i++)
					newChildCt[i] = childCt[i + 1];
			}
			for (int i = 0; i < childCt[0]; i++)
				n.addChild();
			for (int i = 0; i < childCt[0]; i++)
				sendToLeafs(n.get(i), childGroups[i], newChildCt);
		}

	}
	

	public void sendToLeafs( N n, List<Integer> input, int[] childCt) {
		if (input.size() == 1) {
			n.dataLoc = input.get(0);
			return;
		} else if (input.size() < childCt[0]) {
			for (int i = 0; i < input.size(); i++) {
				n.addChild();
				n.get(i).dataLoc = input.get(i);
			}
		} else {
			int[] childGroups = PFns.splitEvenly(input.size(), childCt[0]);
			int[] newChildCt = childCt;
			if (childCt.length > 1) {
				newChildCt = new int[childCt.length - 1];
				for (int i = 0; i < newChildCt.length; i++)
					newChildCt[i] = childCt[i + 1];
			}
			for (int i = 0; i < childGroups.length; i++)
				n.addChild();
			int curIndex = 0;
			for (int i = 0; i < childGroups.length; i++) {
				sendToLeafs(n.get(i), input.subList(curIndex, curIndex + childGroups[i]), newChildCt);
				curIndex += childGroups[i];
			}
		}
	}
	
	
	public <E> List<E> structureList( List<E> listIn ) {
		return structureList( listIn, () -> null  );
	}
	public <E> List<E> structureList( List<E> listIn, Supplier<E> emptyVal ) {
		List<E> listOut = new ArrayList<>();
		for (int i = 0; i < root().treeSize(); i++)
			listOut.add( emptyVal.get() );
		root().applyThenToChildren( n -> { if( n.dataLoc != -1 ) listOut.set( n.myLoc, listIn.get( n.dataLoc ) ); } );
		return listOut;
	}


	

	

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String multiLineString(Function<N, ?> fn) {
		String out = "";
		for (N n : this ) out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}
	
	public void printOperation(Function<N, ?> fn) {
		for (N n : this ) System.out.println( createPrintFn(fn).apply(n).toString() );
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
		return multiLineString( n -> "node" );
	}

	public <E> void printList(List<E> inputList) {
		System.out.println( multiLineString( n -> ( n.getElem(inputList) != null ? "node: " + n.getElem(inputList) : "node: (null)") ) );
	}
	
	
	


}
