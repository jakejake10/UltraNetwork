package utTypes;

import java.util.Iterator;

import unCore.*;

/*
 * copy this template and customize for needs, methods will need to be added, different constructor, etc.
 * this template allows multiple sublasses that all share tree node functionality
 * typically need a subclass for a group node and sublcasses for different leaf node types
 * override the allowChildren() method to return false on non group type nodes
 * add abstract methods in this class, shared among subclasses
 * 	- allows you to perform recursive operations with these commmon methods
 * 	- for example, a getValue() method that sums children getValue() returns
 */
public abstract class TreeFunctionObject<I,O> implements TreeNodeObject<TreeFunctionObject<I,O>>, Iterable<TreeFunctionObject<I,O>> {
	
	// TREENODE INTERFACE FIELDS ///////////////////
	SingularTreeData<TreeFunctionObject<I,O>> core;
	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int childCt = 0;
	public int depth = 0;

	// OTHER FIELDS ////////////////////////////////
	String description = "no description";

	
	public TreeFunctionObject() {
		initNodeFields();
	}

	// ABSTRACT METHODS //////////////////////

	abstract O runFn( I input );

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

	public SingularTreeData<TreeFunctionObject<I,O>> getCoreFn() {
		return core;
	}

	public void setCore(SingularTreeData<TreeFunctionObject<I,O>> input) {
		this.core = input;
	}

	// public SubclassableTreeNode defaultConstructor(){
	// return new SubclassableTreeNode( "", 0 );
	// }
	public TreeFunctionObject<I,O> getInstance() {
		return this;
	}

	public Iterator<TreeFunctionObject<I,O>> iterator() {
		return nodeIterator();
	}

	// OPTIONAL METHODS //////////////////////////

	@Override
	public void transferSubclassFieldsTo(TreeFunctionObject<I,O> newNode) {
	}

	// OTHER METHODS ///////////////////////////////////////

	@Override
	public String toString() {
		return description;
	};

}
