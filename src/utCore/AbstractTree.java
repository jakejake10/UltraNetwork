package utCore;

import java.util.List;
import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.*;

import utCore.AbstractNode.DFTTraversal;


//import processing.core.*;

public abstract class AbstractTree<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N> {
	public List<N> nodes = new ArrayList<>();
	public TreeFns<T,N> fns = new TreeFns<>();
		
	
	public AbstractTree() {
		setRoot(); // root node always at 0;
	}
	
	// ABSTRACT FNS ///////////////////////////////////////////////////
	
	public abstract void setRoot();
	

	// GET FNS ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	public N root() {
		return nodes.get(0);
	}

	public int size() {
		return nodes.size();
	}

	public int leafSize() {
		return leafSize(root());
	}

	public int leafSize(N node) {
		return fns.getLeafCount.apply(node, null);
	}

	public N get(int index) {
		return nodes.get(index);
	}

	public List<N> getLeafs() {
		return getLeafs(root());
	}

	public List<N> getLeafs(N node) {
		List<N> out = new ArrayList<N>();
		fns.getLeafs.accept(node, out);
		return out;
	}
	
	


	
	// LIST FUNCTIONS /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	
//	public <E> List<E> makeList( Function<N, E> fn ) {	// for recursive lambdas, just make new lambda
//		List<E> out = new ArrayList<E>();
//		for (N n : this)
//			out.add(fn.apply(n));
//		return out;
//	}
	
	
	public <E> List<E> makeList( Function<N, E> fn ) {	// for recursive lambdas, just make new lambda
		List<E> out = new ArrayList<E>();
		for ( N node : this ) {
			E val = fn.apply( node );
			if( val != null )out.add( val );
		}
		return out;
	}
	
	public <E> List<E> makeOrderedList( E rootData, BiConsumer<N,List<E>> fn ) {	// for recursive lambdas, just make new lambda
		List<E> out = new ArrayList<E>();
		out.add(rootData);
		while( out.size() < size() ) out.add(null);
		for ( N node : this ) fn.accept( node, out ); // dft
 		return out;
	}
	
	
	


	

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

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String indentStringLines(Function<N, ?> fn) {
		String out = "";
		for (N n : this )
			out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}
	
	public void printOperation(Function<N, ?> fn) {
		for (N n : this ) System.out.println( createPrintFn(fn).apply(n).toString() );
	}

	public Function<N, String> createPrintFn(Function<N, ?> addedString) {
		return n -> new String(new char[n.depth]).replace('\0', ' ') + addedString.apply(n).toString();
	}

	public String toString() {
		return indentStringLines(createPrintFn(n -> "node"));
	}

	public <E> void printList(List<E> inputList) {
		System.out.println( indentStringLines( createPrintFn( n -> "node"
				+ (n.get(inputList) != null ? " - " + n.get(inputList) : " (null)"))));
	}
	
	
	


}
