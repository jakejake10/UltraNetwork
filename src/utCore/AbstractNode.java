package utCore;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.function.*;


import java.util.Iterator;

public abstract class AbstractNode<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> implements Iterable<N>{
//	private T myTree;
	List<N> myTree;
	public int parentLoc = -1;
	public int myLoc = -1;
	public int firstChild = -1;
	public int size = 0;
	// int dataLoc = -1;
	public int depth = -1;
	
	
	// CONSTRUCTORS ////////////////////////////////////////////////////////

//	public AbstractNode(AbstractTree system) {
//		this.system = system;
//	}

	public AbstractNode(List<N> parList, String... mode) {
		this.myTree = parList;
		if( mode.length == 0 ) return;	// not needed?
		switch (mode[0]) {
		case "root":
			if (myTree.size() > 0)
				throw new IllegalStateException("node list must be empty before adding root node");
			myLoc = 0;
			depth = 0;
			myTree.add( getInstance() );
			break;
		}
	}
	
	// ABSTRACT FNS /////////////////////////////////////////////////////////////////
	
	public abstract N defaultConstructor( List<N> parList, String... mode );
	public abstract N getInstance();
	
//	public <E> Node( E val, List<E> vals , UltraTree system, String... mode ){	// for subclasses working with parallel data lists
//		this( system, mode.length > 0 ? mode[0] : "");
//		vals.add(val);
//	}
	
	
	// CHILDREN ///////////////////////////////////////////////////
	
	// add at index, new int parameter
	public void addChild() {
		addChild( defaultConstructor( myTree ) );
	}

	public <E> void addChild(E input, List<E> inputList) {
		addChild();
		// lastChild().dataLoc = inputList.size();
		inputList.add(myLoc, input);
	}

	public void addChild( N child) {
		int childIndex = !hasChildren() ? myTree.size() : firstChild + size; // myLoc + 1 + size;
		if (!hasChildren())
			firstChild = childIndex;
		child.myLoc = childIndex;
		child.parentLoc = myLoc;
		child.depth = depth + 1;
		updateNodes(childIndex, 1);
		myTree.add(childIndex, child); // add to index after loc, and after any pre existing children
		size++;
	}
	

	public void updateNodes(int index, int amount) {
		if (index == myTree.size())
			return;
		for (int i = 0; i < myTree.size(); i++) {
			N curNode = myTree.get(i);
			if (curNode.myLoc >= index)
				curNode.myLoc += amount;
			if (curNode.firstChild >= index)
				curNode.firstChild += amount;
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

	
	public N get(int index) {
		return  myTree.get(firstChild + index);
	}
	
	public <E> E get(List<E> inputList) {
		try {
			return inputList.get(myLoc);
		} catch (Exception e) {
			return null;
		}
	}
	
	public N parent() {
		return myTree.get(parentLoc);
	}
	
	public int parentIndex() {
		if( !hasParent() ) return -1;
		else return parent().firstChild - myLoc;
	}
	
	public AbstractNode<T,N> getRoot() {
		return hasParent() ? parent() : this;
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
	
	public int getLeafSize() {
		return getLeafSize(0);
	}
	
	public int getLeafSize( int input ) {
		int out = 0;
		while( leafs().iterator().hasNext() ) out++ ;
		return out;
	}
	
	public int getTreeSize() {
		int out = 0;
		while( iterator().hasNext() ) out++;
		return out;
	}
	
	public int childIndex() {
		if( !hasParent() ) return -1;
		else return myLoc - parent().firstChild;
	}

	public int childCount() {
		return size;
	}

	public String toString() {
		return "parentLoc = " + parentLoc + ", myLoc = " + myLoc + ", size = " + size + ", depth = " + depth;
	}
	
	// SET FNS ////////////////////////////////////////////
		
	public <E> void setVal( E val, List<E> inputList ){
		if( myLoc != inputList.size() ) inputList.set(myLoc, val);
		else  inputList.add(val);
	}
	
	// LIST FNS ///////////////////////////////////////////
	
	public <E> void importData( E input, List<E> dataList, BiConsumer<N,List<E>> modifyFn ){
		if( dataList.size() < myTree.size() ) while( dataList.size() < myTree.size() ) dataList.add(null);
		addChild();
		lastChild().setVal(input, dataList);
		modifyFn.accept( lastChild(), dataList);
	}
	
	public <E> void importData( List<E> input, List<E> dataList, BiConsumer<N,List<E>> modifyFn ){
		if( dataList.size() < myTree.size() ) while( dataList.size() < myTree.size() ) dataList.add(null);
		int originalChildCt = size;
		for( int i = 0; i < input.size(); i++ )  addChild();
		for( int i = originalChildCt; i < size; i++ ) {
			get(i).setVal(input.get(i), dataList);
			modifyFn.accept( get(i), dataList);
		}
	}
	
	
	
	// LAMDAS /////////////////////////////////////////////

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

		
		public Iterator<N> iterator() {
			return new DFTTraversal<Void>();
		}
		
		public Iterable<N> leafs() {
			return getLeafs(  );
		}
		

		
		public Iterable<N> makeIterable( Iterator<N> type ) {
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
		
		
		public class DFTTraversal<V> implements Iterator<N> {
			public Stack<N> traversal = new Stack<>();
			public BiConsumer<N, V> traverseFn;
			public V data;
			
			
			public DFTTraversal() {
				traversal.push( getInstance() );
			}

			public DFTTraversal(V data, BiConsumer<N, V> traverseFn) {
				traversal.push( getInstance() );
				this.data = data;
				this.traverseFn = traverseFn;
			}

			public boolean hasNext() {
				return !traversal.isEmpty();
			}

			public N next() {
				N n = traversal.pop();
				if (traverseFn != null) traverseFn.accept(n, data);
				if (n.hasChildren()) {
					for (int i = n.size - 1; i >= 0; i--)
						traversal.push(n.get(i)); // reverse order
				}
				return n;
			}

			public void remove() {
				// Default throws UnsupportedOperationException.
			}
		}
	
	
	
	

	

}
