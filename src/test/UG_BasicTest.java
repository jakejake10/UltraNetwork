package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import unCore.*;
import utTypes.BGNode;



public class UG_BasicTest extends PApplet {
	
	Graph<BGNode> g;
	
	public static void main(String... args) {
		UG_BasicTest pt = new UG_BasicTest();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(512, 512);
	}

	
	
	@Override
	public void setup() {
		g = new Graph<BGNode>();
		g.addNodes( 5, () -> BGNode.staticConstructor(g) );
//		g.addEdge(0, 1);
//		
//		g.addEdge(0, 5);
//		g.addEdge(5,3);
//		g.makeCircular();
//		println( g.get(0).par.nodes.size() );
		g.addEdge( 1, 4);
//		g.addEdge( 1,  2);
//		g.addEdge( 3,0);
		g.display(this);
//		for( BGNode n : g.get(0).bfs() ) println( n.index() );
//		println();
//		for( BGNode n : g.get(0).dfs() ) println( n.index() );
		
		println( g.countDisconnectedSubgraphs() + " disconnected subgraphs");
	}
	
	
	
	

	@Override
	public void draw() {
		
	}
	
	class ARNode{
		
	}


}
