package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;
import ugCore.Grid;
import utCore.*;
import utTypes.*;
import pFns_baseObjects.*;
import pFns_general.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class UT_SortingFunctions extends PApplet{
	BasicNode bn;
	Grid g;
	
	public static void main(String... args) {
		UT_SortingFunctions pt = new UT_SortingFunctions();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		noLoop();
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		textAlign(CENTER,CENTER );
		textSize( 16 );
		
		g = new Grid( this, 3,3 ).toMulti();
		g.get(4).divide().cr( 2,2 );
		g.get(4).get(0).divide().cr(2,2);
//		g.get(0).divide().cr( 3,4 );
//		g.get(0).get(0).divide().cr( 3,4 );
		g.tree.printOperation( n -> "node myLoc: " + n.myLoc + ", parentLoc: " + ( n.hasParent() ? n.parent().myLoc : "") );
		
		g.shuffle();
//		g.get(0).shuffle();
//		g.sort( Comparator.comparing( Grid::size ) );
//		g.reverse();
		println();
		g.tree.printOperation( n -> "node myLoc: " + n.myLoc + ", parentLoc: " + ( n.hasParent() ? n.parent().myLoc : "") + "fc: " + n.firstChild);
//		Collections.shuffle( g.getChildren() );
	}

	@Override
	public void draw() {
		background(255);
		
		int tSize = g.treeSize();
		println( tSize );
		List<Integer> colors = IntStream.range(0, tSize ).mapToObj( i -> floor( i * (255 / tSize ) ) ).collect(Collectors.toList() );
//		g.sort( Comparator.comparing( Grid::x).reversed(), colors );
//		g.displayOperation().setFill(colors).display(this);
		g.display(this);
		
		fill(0);
		
		
		for( int i : g.cr ) text( g.get(i).indexInParent(), g.cellXC(i), g.cellYC(i) );

	}
	
	

}
