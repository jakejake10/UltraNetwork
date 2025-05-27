package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import unCore.*;
import utTypes.*;
//import utTypes.*;


import java.util.List;
import java.util.function.Function;





public class UN_DATANODE extends PApplet{
	DataNode<String> root;
	List<Integer> dataList;
	
	public static void main(String... args) {
		UN_DATANODE pt = new UN_DATANODE();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		root = new DataNode<>("root");
			root.addChildWithData( "c0");
				root.get(0).addChildWithData("c00");
			root.addChildWithData( "c1");
			root.addChildWithData( "c2");
			root.get(2).addChildWithData("c20");
		root.printOperation();
		println();
		
		dataList = TreeNodeFunctions.convertDataList( root.exportDataList(), s -> s.length() );
		DataNode<Integer> newRoot = DataNode.buildFromDataList(root, dataList );
		
		println();
		newRoot.printOperation();
		
		
		
//		TreeNodeFunctions.applyDataList( newRoot, dataList, (n,d) -> n.setData( n.getData() + d ));
//		newRoot.printOperation();
//		
		
		
	}

	@Override
	public void draw() {
		
		
	}
	
	

}
