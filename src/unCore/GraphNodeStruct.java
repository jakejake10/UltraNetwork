package unCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import processing.core.PApplet;
import processing.core.PVector;



public abstract class GraphNodeStruct<N extends GraphNodeStruct<N, D>, D> implements NodeObj<N, D>, Iterable<N> {
	// COMMON
	NodeObj.CoreData<N, D> core;
	D data;
	int index;
	UUID id = UUID.randomUUID();
	//OTHER
	public List<Edge> edges = new ArrayList<>();	// list of int indexes instead? easier for copying
	
	// GRAPH/TREE CONSTRUCTORS
	public GraphNodeStruct( BaseNodeCommand...initType ){	// root constructor
		if( initType == null || initType.length == 0 ) NodeObj.nodeInitRoot(getInstance());
	}
	
//	public GraphNodeStruct( int...init ){	// root constructor
//		if( init == null || init.length == 0 ) {
//			core = makeCore();	// constructor() is root constructor
//			core.attach( getInstance() );
//			generateData();
//		}
//		// else constructor(0) means it is a null node
//	}
//	public GraphNodeStruct( N input ){	// root constructor
//		core.attach( input );
//		generateData();
//	}
//	
//	public GraphNodeStruct( D input ){	// root constructor
//		if( nodeList() == null ) core = makeCore();	// constructor() is root constructor
//		core.attach( getInstance() );
//		setData( input );
//	}
	
	// NODEOBJ INTERFACE /////////////////////////////////

	/**
	 * methods for final class:
	 * defaultConstructor()
	 * getInstance()
	 */
	
	// COMMON /////////////////

	public int index() {
		return index;
	}
	public void setIndex( int index ) {
		this.index = index;
	}
	public CoreData<N, D> getCore() {
		return core;
	}
	public void setCore(CoreData<N, D> input) {
		this.core = input;
	}
	public CoreData<N, D> makeCore() {
		return new CoreData<>( (n,i) -> i );
	}

	public D getData() {
		return data;
	}

	public N setData(D data) {
		this.data = data;
		return getInstance();
	}


	public int size() {
		int out = 0;
		for (N n : getInstance() )
			out++;
		return out;
	}

	public Function<N, List<N>> dfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getAdjacentNodes();
			Collections.reverse(nc);
			return nc;
		};
	}

	public Function<N, List<N>> bfsNodeGatherFn() {
		return n -> {
			List<N> nc = n.getAdjacentNodes();
			return nc;
		};
	}
	
	@Override
	public void insertNodeFn( N input ) { 
		//TODO add code
	}
	@Override
	public N copyNode() {
		//TODO addCode
		return getInstance();
	}
	
	
	
	// GETTER METHODS ////////////////////////////////////////////////////////
	
		public int degree() {
			return edges.size();
		}
		
		public List<Edge> getEdges(){
			return edges;
		}
		
		public List<N> getAdjacentNodes(){
			return edges.stream().map( e -> nodeList().get( e.dest) ).collect( Collectors.toList());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			GraphNodeStruct<N,D> that = (GraphNodeStruct<N,D>) obj;
			return this.id == that.id;
		}
		
		// GRAPH METHODS /////////////////////////////////////////////////////
		
		public void addNodes(int size ) {
			for( int i = 0; i < size; i++ ) insertNodeFn(defaultConstructor()); //addNode();
		}
		
		public void addEdgeDirectional( int to) {
			edges.add( new Edge( index(), to ) );
		}
		
		public void addEdge( int to ) {
			addEdgeDirectional( to );
			get(to).addEdgeDirectional( index() );			
		}
		
		public void addEdge( N to ) {
			addEdgeDirectional( to.index() );
			to.addEdgeDirectional( index() );			
		}

		public void addEdge( int from, int to ) {
			get(from).addEdge( to );
		}
		
		@Override
		public <R extends NodeObj<?,?>> void transferNodeDataTo( R input ) {
			if( input instanceof GraphNodeStruct ) {
				@SuppressWarnings("unchecked")
				GraphNodeStruct<N,?> tnInput = (GraphNodeStruct<N,?>) input;
				tnInput.index = index;
			}
		}

		
		
//		public void display( PApplet pa ) {
//			double step = ( Math.PI*2 ) / (double)totalSize();
//			List<PVector> pvs = IntStream.range(0,totalSize())
//					.mapToObj( i -> Circular.pvDistAngle( 250f,250f, 150f, (float) step*i ) ).collect( Collectors.toList() );
//			Set<Edge> edges = new HashSet<>();
//			for( N node : nodeList() ) edges.addAll( node.edges );
//			pa.stroke(0);
//			pa.fill(255);
//			pa.strokeWeight(1);
//			
//			for( Edge e : edges ) {
//				PVector st = pvs.get( e.src );
//				PVector ed = pvs.get( e.dest );
//				pa.line( st.x,st.y,ed.x,ed.y );
//			}
//			for( PVector pv : pvs ) pa.ellipse( pv.x,pv.y, 35, 35 );
//			
//			pa.fill(0);
//			pa.textSize( 20 );
//			pa.textAlign(processing.core.PApplet.CENTER, processing.core.PApplet.CENTER);
//			for( int i = 0; i < totalSize(); i++ ) pa.text( i, pvs.get(i).x,pvs.get(i).y -3 );
//		}
		
		// EDGE FNS ////////////////////////////////
		
		public void makeCircular() {
			for( N n : nodeList() ) 
				addEdge(n.index(), ( n.index() + 1 ) % totalSize() );
		}
		
				
		
		public void makeComplete() {
			for( N n : nodeList() ) 
				for( N n2 : nodeList() ) 
					if( n2 != n ) n.addEdge( n2 );
		}
		
		
		// GET METHODS ///////////////////////////////
		
//		public int countDisconnectedSubgraphs() {
//			int count = 0;
//			List<Boolean> visited = NodeFunctions.makeList( nodeList(), false);
//			System.out.println( visited.get(1) );
//			for( int i = 0; i < size(); i++ ) {
//				if( !get(i).getElem(visited) ) {
//					count++;
//					visitRecursive( get(i), visited );
//				}
//			}
//			return count;
//		}
//		
//		public void visitRecursive( N node, List<Boolean> visited ) {
//			if( !node.getElem(visited) ) {
//				for( N nc : node ) nc.setElem( true, visited);
//			}
//		}

		public String toString() {
			return "index: " + index() + ", adjacentNodes: " + edges.size();
		}

}
