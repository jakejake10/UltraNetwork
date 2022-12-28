package utCore;

import java.util.List;
import java.util.ArrayList;
import java.util.function.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.*;

//import processing.core.*;

public class UltraTree implements Iterable<Node> {
	public List<Node> nodes = new ArrayList<>();
	public TreeFns fns = new TreeFns();
	
	
	
	
	public UltraTree() {
		new Node(this, "root"); // root node always at 0;
	}

	// GET FNS ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////

	public Node root() {
		return nodes.get(0);
	}

	public int size() {
		return nodes.size();
	}

	public int leafSize() {
		return leafSize(root());
	}

	public int leafSize(Node node) {
		return fns.getLeafCount.apply(node, null);
	}

	public Node get(int index) {
		return nodes.get(index);
	}

	public List<Node> getLeafs() {
		return getLeafs(root());
	}

	public List<Node> getLeafs(Node node) {
		List<Node> out = new ArrayList<Node>();
		fns.getLeafs.accept(node, out);
		return out;
	}
	
	
	public <E> List<E> makeList( Function<Node, E> fn ) {	// for recursive lambdas, just make new lambda
		List<E> out = new ArrayList<E>();
		for (Node n : this)
			out.add(fn.apply(n));
		return out;
	}
	
	public <E> List<E> makeListRecursive( Function<Node, E> fn, Node... node ){
		return makeListRecursive( new ArrayList<E>(), fn, node );
	}
	public <E> List<E> makeListRecursive( List<E> data, Function<Node, E> fn, Node... node ) {	// for recursive lambdas, just 
		Node targetNode = node.length > 0 ? node[0] : root();
		data.add( fn.apply( targetNode ) );
		if( targetNode.hasChildren() ) for( Node child : node ) data.addAll( makeListRecursive( data, fn, child ) );
		return data;
	}

	public <E, R> List<R> mapList(List<E> inputList, BiFunction<Node, E, R> mapFn) {
		List<R> out = new ArrayList<R>();
		for (int i = 0; i < inputList.size(); i++)
			out.add(null);

		for (Node n : this) {
			int index = n.myLoc;
			out.set(index, mapFn.apply(n, inputList.get(index)));
		}
		return out;
	}

	

	// ITERATORS ////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public Iterator<Node> iterator() {
		return new TreeDFTIterator<Void>();
	}
	
	
	public <E> Iterable<Node> iterableMaker(String mode, E data, BiConsumer<Node,E> fn ) {
		return new Iterable<Node>() {
			public Iterator<Node> iterator() {
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

	
	public Iterable<Node> dft() {
		return iterableMaker("dft",null,null );
//		return new Iterable<Node>() {
//			public Iterator<Node> iterator() {
//				return new TreeDFTIterator<Void>();
//			}
//		};
	}

	public <E> Iterable<Node> dft(E data, BiConsumer<Node,E> fn) {
		return iterableMaker("dft", data, fn );
//		return new Iterable<Node>() {
//			public Iterator<Node> iterator() {
//				return new TreeDFTIterator<E>(data, fn);
//			}
//		};
	}
	
	public Iterable<Node> leafs() {
		return iterableMaker( "leafs", null, null );
	}

	public class TreeDFTIterator<T> implements Iterator<Node> {
		public Stack<Node> traversal = new Stack<>();
		public BiConsumer<Node, T> traverseFn;
		public T data;

		// constructor
		public TreeDFTIterator() {
			traversal.push(nodes.get(0));
		}

		public TreeDFTIterator(T data, BiConsumer<Node, T> traverseFn) {
			traversal.push(nodes.get(0));
			this.data = data;
			this.traverseFn = traverseFn;
		}

		public boolean hasNext() {
			return !traversal.isEmpty();
		}

		public Node next() {
			Node n = traversal.pop();
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
	
	
	public class LeafIterator implements Iterator<Node> {
		public List<Integer> leafs;
		int pos = 0;

		public LeafIterator() {
			this.leafs = getLeafs().stream().map( n -> n.myLoc ).collect(Collectors.toList());
			pos = 0;
		}

		public boolean hasNext() {
			return pos < leafs.size();
		}

		public Node next() {
			Node n = nodes.get( leafs.get(pos) );
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


	public <E>  TreeBuilder<E> build(E data, BiConsumer<Node, E> buildFn) {
		return new TreeBuilder<E>(data, buildFn);
	}

	
	public class TreeBuilder<T> {
		public Node targetNode;
		public BiConsumer<Node, T> buildFn;
		public T data;
		public Iterable<Node> traversalOrder = dft();
		public boolean isTraversalFn = false;
		
		Function<Node,String> splitType;
		Function<Node,Integer> depth;
		Function<Node,Integer> childCount;
		

		public TreeBuilder() {
			this.targetNode = root();
		}

		public TreeBuilder(T data, BiConsumer<Node, T> buildFn) {
			this.data = data;
			this.buildFn = buildFn;
			this.targetNode = root();
		}

		// SETTER FNS
		// ///////////////////////////////////////////////////////////////////

		public TreeBuilder<T> setFn(BiConsumer<Node, T> buildFnIn) {
			this.buildFn = buildFnIn;
			return this;
		}

		public TreeBuilder<T> setData(T data) {
			this.data = data;
			return this;
		}

		public TreeBuilder<T> setNode(Node targetNode) {
			this.targetNode = targetNode;
			return this;
		}

		public TreeBuilder<T> makeTraversalFn() {	// traversal iterator runs buildfn before add children to queue
			if (buildFn == null)
				throw new IllegalStateException("build fn must be assigned before setting as traversal");
			this.isTraversalFn = true;
			traversalOrder = dft(data, buildFn);
			return this;
		}

		// TERMINAL FNs ///////////////////////////////////////////////////////////

		public void generate() {	// if already has children, move to children without running fn?
			if (isTraversalFn)
				runTraverse();
			else
				buildFn.accept(targetNode, data);
		}

		public void runTraverse() { // builds as it traverses
			for (Node n : traversalOrder)
				continue;
		}

		// TYPE SPECIFIC FNS ///////////////////////////////////////////////////////

		public TreeBuilder<Integer[]> leafChildCount(int leafs, int maxChildren) {
			return new TreeBuilder<Integer[]>(new Integer[] { leafs, maxChildren }, fns.makeLeafs).setNode(targetNode);
		}
	}

	// PRINT OPERATIONS
	// //////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	public String indentStringLines(Function<Node, ?> fn) {
		String out = "";
		for (Node n : dft())
			out += createPrintFn(fn).apply(n).toString() + "\n";
		return out;
	}
	
	public void printOperation(Function<Node, ?> fn) {
		for (Node n : dft()) System.out.println( createPrintFn(fn).apply(n).toString() );
	}

	public Function<Node, String> createPrintFn(Function<Node, ?> addedString) {
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
