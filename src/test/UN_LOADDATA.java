package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import unCore.*;
import utTypes.*;
//import utTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.*;
import java.util.stream.*;





public class UN_LOADDATA extends PApplet{
	DataNode<String> root;
	int curLeaf = 0;
	
	public static void main(String... args) {
		UN_LOADDATA pt = new UN_LOADDATA();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		List<String> data = new ArrayList<>();
		data.add("one");
		data.add("two");
		data.add("three");
		data.add("four");
		data.add("five");
		data.add("six");
		root = new DataNode<>("root");
		root.loadData(data, 3);
		
//		root.shuffleAll();
		
		
		root.printOperation();
		
		
	}

	@Override
	public void draw() {
		
	}
	
	
	
	
	
	
	
	
//
//	
//	public class Recursive<I>{
//		public I func;
//		public Recursive<I> setFunc( I input ){
//			this.func = input;
//			return this;
//		}
//	}
	
	
	public void addLeaf( DataNode<Integer> node ) {
		node.addChild( curLeaf );
		curLeaf++;
	}
	
	

}
