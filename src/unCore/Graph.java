package unCore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.*;

import processing.core.PApplet;
import processing.core.PVector;
import usPrimitives.Circular;


public class Graph<N extends AbstractGraphNode<N>> implements Iterable<N> {
	public List<N> nodes = new ArrayList<>();
	
	public Graph() {
	}
	
	// Iterable Interface /////////////////////////////
	
	public Iterator<N> iterator(){
		if( nodes.size() > 0 ) return nodes.iterator();
		return null;
	}
	
//	public Graph( int size, Supplier<T> make ) {
//		for( int i = 0; i < size; i++ ) nodes.add( make.get() );
//	}
	
	public void addNodes(int size, Supplier<N> make) {
		for( int i = 0; i < size; i++ ) nodes.add( make.get() );
	}

	public void addEdge(int from, int to) {
//		Edge e = new Edge( from, to );
		get(from).addEdge( to );
		get(to).addEdge( from );
	}

	public N get(int index) {
		return nodes.get(index);
	}
	
	public void display( PApplet pa ) {
		double step = ( Math.PI*2 ) / (double)nodes.size();
		List<PVector> pvs = IntStream.range(0,nodes.size())
				.mapToObj( i -> Circular.pvDistAngle( 250f,250f, 150f, (float) step*i ) ).collect( Collectors.toList() );
		Set<Edge> edges = new HashSet<>();
		for( N node : nodes ) edges.addAll( node.edges );
		pa.stroke(0);
		pa.fill(255);
		pa.strokeWeight(1);
		
		for( Edge e : edges ) {
			PVector st = pvs.get( e.src );
			PVector ed = pvs.get( e.dest );
			pa.line( st.x,st.y,ed.x,ed.y );
		}
		for( PVector pv : pvs ) pa.ellipse( pv.x,pv.y, 35, 35 );
		
		pa.fill(0);
		pa.textSize( 20 );
		pa.textAlign(processing.core.PApplet.CENTER, processing.core.PApplet.CENTER);
		for( int i = 0; i < nodes.size(); i++ ) pa.text( i, pvs.get(i).x,pvs.get(i).y -3 );
	}
	
	// EDGE FNS ////////////////////////////////
	
	public void makeCircular() {
		for( N n : nodes ) 
			addEdge(n.index(), ( n.index() + 1 ) % nodes.size() );
	}
	
	public int size() {
		return nodes.size();
	}
	
	
	public void makeComplete() {
		for( N n : nodes ) 
			for( N n2 : nodes ) 
				if( n2 != n ) n.addEdge( n2 );
	}
	
	
	// GET METHODS ///////////////////////////////
	
	public int countDisconnectedSubgraphs() {
		int count = 0;
		List<Boolean> visited = NodeFunctions.makeList( nodes, false);
		System.out.println( visited.get(1) );
		for( int i = 0; i < size(); i++ ) {
			if( !get(i).getElem(visited) ) {
				count++;
				visitRecursive( get(i), visited );
			}
		}
		return count;
	}
	
	public void visitRecursive( N node, List<Boolean> visited ) {
		if( !node.getElem(visited) ) {
			for( N nc : node ) nc.setElem( true, visited);
		}
	}
	
	

}
