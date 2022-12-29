package utCore;

import java.util.List;
import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.*;

//import processing.core.*;

public abstract class AbstractTree<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N> {
	public List<N> nodes = new ArrayList<>();
	public TreeFns<T,N> fns = new TreeFns<>();
		
	
	public AbstractTree() {
		setRoot(); // root node always at 0;
	}
	
	// ABSTRACT FNS ///////////////////////////////////////////////////
	
	abstract void setRoot();

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
	
	
	public <E> List<E> makeList( Function<N, E> fn ) {	// for recursive lambdas, just make new lambda
		List<E> out = new ArrayList<E>();
		for (N n : this)
			out.add(fn.apply(n));
		return out;
	}
	
//	public <E> List<E> makeListRecursive( Function<N, E> fn, N... node ){
//		return makeListRecursive( new ArrayList<E>(), fn, node );
//	}
//	public <E> List<E> makeListRecursive( List<E> data, Function<N, E> fn, N... node ) {	// for recursive lambdas, just 
//		N targetNode = node.length > 0 ? node[0] : root();
//		data.add( fn.apply( targetNode ) );
//		if( targetNode.hasChildren() ) for( N child : node ) data.addAll( makeListRecursive( data, fn, child ) );
//		return data;
//	}
//
//	public <E, R> List<R> mapList(List<E> inputList, BiFunction<N, E, R> mapFn) {
//		List<R> out = new ArrayList<R>();
//		for (int i = 0; i < inputList.size(); i++)
//			out.add(null);
//
//		for ( N n : this ) {
//			int index = n.myLoc;
//			out.set(index, mapFn.apply(n, inputList.get(index)));
//		}
//		return out;
//	}

	

	// ITERATORS ////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public Iterator<N> iterator() {
		return new TreeDFTIterator<Void>();
	}
	
	
	public <E> Iterable<N> iterableMaker(String mode, E data, BiConsumer<N,E> fn ) {
		return new Iterable<N>() {
			public Iterator<N> iterator() {
				switch( mode ) {
				case "dft":
					if( fn == null ) return new TreeDFTIterator<Void>();
					else             return new TreeDFTIterator<E>(data, fn);
				case "leafs":
					return new LeafIterator();
				default:
					return iterator();
				}
			}
		};
	}

	
	public Iterable<N> dft() {
		return iterableMaker("dft",null,null );
//		return new Iterable<Node>() {
//			public Iterator<Node> iterator() {
//				return new TreeDFTIterator<Void>();
//			}
//		};
	}

	public <E> Iterable<N> dft(E data, BiConsumer<N,E> fn) {
		return iterableMaker("dft", data, fn );
//		return new Iterable<Node>() {
//			public Iterator<Node> iterator() {
//				return new TreeDFTIterator<E>(data, fn);
//			}
//		};
	}
	
	public Iterable<N> leafs() {
		return iterableMaker( "leafs", null, null );
	}

	public class TreeDFTIterator<V> implements Iterator<N> {
		public Stack<N> traversal = new Stack<>();
		public BiConsumer<N, V> traverseFn;
		public V data;

		// constructor
		public TreeDFTIterator() {
			traversal.push(nodes.get(0));
		}

		public TreeDFTIterator(V data, BiConsumer<N, V> traverseFn) {
			traversal.push(nodes.get(0));
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
			if (n.hasChildren()) {
				for (int i = n.size - 1; i >= 0; i--)
					traversal.push(n.get(i)); // reverse order
			}
			return n;
		}

		// Used to remove an element. Implement only if needed
		public void remove() {
			// Default throws UnsupportedOperationException.
		}
	}
	
	
	public class LeafIterator implements Iterator<N> {
		public List<Integer> leafs;
		int pos = 0;

		public LeafIterator() {
			this.leafs = getLeafs().stream().map( n -> n.myLoc ).collect(Collectors.toList());
			pos = 0;
		}

		public boolean hasNext() {
			return pos < leafs.size();
		}

		public N next() {
			N n = nodes.get( leafs.get(pos) );
			pos++;
			return n;
		}

		// Used to remove an element. Implement only if needed
		public void remove() {
			// Default throws UnsupportedOperationException.
		}
	}

	// TREE BUILDER
	// //////////////////////////////////////////////////////////////////////////

	public TreeBuilder<Void> build() {
		return new TreeBuilder<Void>();
	}


	public <E>  TreeBuilder<E> build(E data, BiConsumer<N, E> buildFn) {
		return new TreeBuilder<E>(data, buildFn);
	}

	
	public class TreeBuilder<V> {
		public N targetNode;
		public BiConsumer<N, V> buildFn;
		public V data;
		public Iterable<N> traversalOrder = dft();
		
//		Function<Node,String> splitType; // need subclass?
		Function<N,Integer> depthFn;
		Function<N,Integer> childCountFn;
		BiConsumer<N,N> addChildFn = (n,c) -> n.addChild(c);
		

		public TreeBuilder() {
			this.targetNode = root();
		}

		public TreeBuilder(V data, BiConsumer<N, V> buildFn) {
			this.data = data;
			this.buildFn = buildFn;
			this.targetNode = root();
		}

		// SETTER FNS
		// ///////////////////////////////////////////////////////////////////

		public TreeBuilder<V> setFn(BiConsumer<N, V> buildFnIn) {
			this.buildFn = buildFnIn;
			return this;
		}

		public TreeBuilder<V> setData(V data) {
			this.data = data;
			return this;
		}

		public TreeBuilder<V> setNode(N targetNode) {
			this.targetNode = targetNode;
			return this;
		}

//		public TreeBuilder<T> makeTraversalFn() {	// traversal iterator runs buildfn before add children to queue
//			if (buildFn == null)
//				throw new IllegalStateException("build fn must be assigned before setting as traversal");
//			this.isTraversalFn = true;
//			traversalOrder = dft(data, buildFn);
//			return this;
//		}

		// TERMINAL FNs ///////////////////////////////////////////////////////////

		public void generate() {	// if already has children, move to children without running fn?
			buildFn.accept(targetNode, data);
		}
		public void generateForEach() {
			traversalOrder = dft(data, buildFn);
			for (N n : traversalOrder) continue;
		}

		
		// TYPE SPECIFIC FNS ///////////////////////////////////////////////////////

		public TreeBuilder<Integer[]> leafChildCount(int leafs, int maxChildren) {
			return new TreeBuilder<Integer[]>(new Integer[] { leafs, maxChildren }, fns.makeLeafs).setNode(targetNode);
		}
		
		BiConsumer<N,V> recursiveBuild = ( n, t ) -> {
			if( n.depth < depthFn.apply(n) ) {
				for( int i = 0; i < childCountFn.apply(n); i++ )
					n.addChild();
			}
				
		};
		
		public interface FunctionalBuilder{
			
		}
	}

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String indentStringLines(Function<N, ?> fn) {
		String out = "";
		for (N n : dft())
			out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}
	
	public void printOperation(Function<N, ?> fn) {
		for (N n : dft()) System.out.println( createPrintFn(fn).apply(n).toString() );
	}

	public Function<N, String> createPrintFn(Function<N, ?> addedString) {
		return n -> new String(new char[n.depth]).replace('\0', ' ') + addedString.apply(n).toString();
	}

	public String toString() {
		return indentStringLines(createPrintFn(n -> "node"));
	}

	public <E> void printList(List<E> inputList) {
		System.out.println( indentStringLines( createPrintFn( n -> "node"
				+ (n.getVal(inputList) != null && n.getVal(inputList).toString() != null ? " - " + n.getVal(inputList) : ""))));
	}

}
