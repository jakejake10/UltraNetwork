package test;

//package processingFnTest;

//import processing.core.PApplet;
//import processing.core.PVector;
import processing.core.*;

import unCore.*;
import utTypes.*;
//import utTypes.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;





public class UN_SubTypes extends PApplet{
	NumWrapper root,root2;
	
	public static void main(String... args) {
		UN_SubTypes pt = new UN_SubTypes();
		PApplet.runSketch(new String[] { "Test" }, pt);
	}

	@Override
	public void settings() {
		size(500, 500);
	}

	@Override
	public void setup() {
		noLoop();
		NumWrapper f = new FloatWrapper(0);
		NumWrapper i = new IntWrapper(5);
		root = new IntWrapper( 0 );
			root.addChild(i);
		
		root2 = root.copyNodeSubtree();
//		root.get(0).replaceIndividualNode(f);
		
//		root = root.get(0).replaceNodeSubtree(f);
		root2.get(0).replaceNodeSubtree(f);
//		root = root.replaceIndividualNode(f);
//		println( root );
		root.printOperation();
		println();
		root2.printOperation();

	}

	@Override
	public void draw() {
		
		
	}
	
	public abstract class NumWrapper implements TreeNodeObject<NumWrapper>, Iterable<NumWrapper> {
		// TREENODE INTERFACE FIELDS ///////////////////
		SingularTreeData<NumWrapper> core;
		public int index;
		public int parentIndex = -1; // index in tree.nodes
		public int firstChild = -1;
		public int childCt = 0;
		public int depth = 0;
		// OTHER FIELDS ////////////////////////////////
		String type = "";
		
		public NumWrapper( String type ) {
			initNodeFields();
			this.type = type;
		}
		// ABSTRACT METHODS //////////////////////
		public abstract Number getValue();
		// TREENODE Interface ////////////////////
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public int getParentIndex() {
			return parentIndex;
		}
		public void setParentIndex(int index) {
			this.parentIndex = index;
		}
		public int getFirstChildIndex() {
			return firstChild;
		}
		public void setFirstChildIndex(int index) {
			this.firstChild = index;
		}
		public int getDepth() {
			return depth;
		}
		public void setDepth(int depth) {
			this.depth = depth;
		}
		public int getChildCount() {
			return childCt;
		}
		public void setChildCount(int count) {
			this.childCt = count;
		}
		public SingularTreeData<NumWrapper> getCore() {
			return core;
		}
		public void setCore(SingularTreeData<NumWrapper> input) {
			this.core = input;
		}
		public NumWrapper getInstance() {
			return this;
		}
		public Iterator<NumWrapper> iterator() {
			return nodeIterator();
		}
		// OPTIONAL METHODS //////////////////////////
		@Override
		public void transferSubclassFieldsTo(NumWrapper newNode) {
		}
		// OTHER METHODS ///////////////////////////////////////
		@Override
		public String toString() {
			return "class: " + getClass().getName() + ", value: " + getValue();
		}
	}
	
	public class FloatWrapper extends NumWrapper {
		public Float value;
		public FloatWrapper( float value ) {
			super("float");
			this.value = value;
		}
		
		// ABSTRACT METHODS /////////////////////////////////
		public Float getValue() {
			return value;
		}

		public FloatWrapper defaultConstructor() {
			return new FloatWrapper(0);
		}

		public FloatWrapper getInstance() {
			return this;
		}

		@Override
		public FloatWrapper copyNode(boolean transferNodeData) {
			return (FloatWrapper) super.copyNode(transferNodeData);
		}

		@Override
		public void transferSubclassFieldsTo( NumWrapper newNode) {
//			super.transferSubclassFieldsTo(newNode);
			((FloatWrapper)newNode).value = value;
		}

		@Override
		public String toString() {
			return super.toString() + ": ";
		};

	}
	
	public class IntWrapper extends NumWrapper {
		public Integer value;
		
		public IntWrapper(int value ) {
			super("int");
			this.value = value;
		}
		
		// ABSTRACT METHODS /////////////////////////////////
		public Integer getValue() {
			return value;
		}

		public IntWrapper defaultConstructor() {
			return new IntWrapper(0);
		}

		public IntWrapper getInstance() {
			return this;
		}

		@Override
		public IntWrapper copyNode(boolean transferNodeData) {
			return (IntWrapper) super.copyNode(transferNodeData);
		}

		@Override
		public void transferSubclassFieldsTo( NumWrapper newNode) {
//			super.transferSubclassFieldsTo(newNode);
			((IntWrapper)newNode).value = value;
		}

		@Override
		public String toString() {
			return super.toString() + ": ";
		};

	}
	

}
