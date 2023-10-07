package unCore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractGraphNode<N extends AbstractGraphNode<N>> 
	implements Iterable<N>,Comparable<N>, NodeFunctions<N> {
	
	public Graph<N> graph;
	public Set<Edge> edges;
	public int myLoc;
	

	public AbstractGraphNode( Graph<N> parIn, int index ) {
		this.graph = parIn;
		this.myLoc = index;
		edges = new HashSet<Edge>();
	}
	
	// GETTER METHODS ////////////////////////////////////////////////////////
	
	public int degree() {
		return edges.size();
	}
	
	public Set<Edge> getEdges(){
		return edges;
	}
	
	public List<N> getAdjacentNodes(){
		return edges.stream().map( e -> graph.get( e.dest) ).collect( Collectors.toList());
	}
	
	
	// Interface Abstract Methods ////////////////////////////////////////////

	@Override
	public Iterator<N> iterator() {	// make default?
		return nodeIterator();
	}

	@Override
	public int compareTo(N o) {
		return Integer.compare(this.index(), o.index());
	}

	@Override
	public int index() {
		return myLoc;
	}

	@Override
	public void updateIndex() {
		// TODO Auto-generated method stub
	}

	@Override
	public int size() {
		return edges.size();
	}

	@Override
	public int treeSize() {
		return graph.nodes.size();
	}

	@Override
	public List<N> nodeList() {
		return graph.nodes;
	}

	@Override
	public Function<N, List<N>> dfsNodeGatherFn() {
		return n -> { 
			List<N> ns = n.getAdjacentNodes();
			Collections.sort(ns, (n1,n2) -> n2.index() - n1.index() );	// so dfs goes in order for certain graphs
			return ns;
		};
		
	}

	@Override
	public Function<N, List<N>> bfsNodeGatherFn() {
		return n -> n.getAdjacentNodes();
	}
	
//	public int index() {
//		return par.nodes.indexOf(this);
//	}
//
//	public int compareTo( N nIn ) {
//		return edges.size() - nIn.edges.size();
//	}
//	
	public void addEdge( N adjacentNode ) {
		edges.add( new Edge( index(), adjacentNode.index() ));
	}
	
	public void addEdge( int adjacentNodeIndex ) {
		edges.add( new Edge( index(), adjacentNodeIndex ));
	}
//	
//	public void addEdge( N nodeIn ) {
//		par.addEdge(index(), nodeIn.index());
//	}
//	
//	public abstract N getInstance();
//	public abstract N defaultConstructor( Graph<N> input );
//	
//	@Override
//	public boolean equals( Object obj ) {
//		 if (obj.getClass() != this.getClass()) return false;
//		 N input = (N) obj;
//		 return myLoc == input.myLoc;
//	}
//
//	






	

}
