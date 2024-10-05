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
public abstract class SubclassableTreeNodeTemplate implements TreeNodeObject<SubclassableTreeNodeTemplate>, Iterable<SubclassableTreeNodeTemplate> {
	
	// TREENODE INTERFACE FIELDS ///////////////////
	SingularTreeData<SubclassableTreeNodeTemplate> core;
	public int index;
	public int parentIndex = -1; // index in tree.nodes
	public int firstChild = -1;
	public int childCt = 0;
	public int depth = 0;

	// OTHER FIELDS ////////////////////////////////
	String type = "";
	String name = "";

	
	public SubclassableTreeNodeTemplate(String name, String type) {
		initNodeFields();
		this.name = name;
		this.type = type;
	}

	// ABSTRACT METHODS //////////////////////

//	abstract float getValue();

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

	public SingularTreeData<SubclassableTreeNodeTemplate> getCore() {
		return core;
	}

	public void setCore(SingularTreeData<SubclassableTreeNodeTemplate> input) {
		this.core = input;
	}

	// public SubclassableTreeNode defaultConstructor(){
	// return new SubclassableTreeNode( "", 0 );
	// }
	public SubclassableTreeNodeTemplate getInstance() {
		return this;
	}

	public Iterator<SubclassableTreeNodeTemplate> iterator() {
		return nodeIterator();
	}

	// OPTIONAL METHODS //////////////////////////

	@Override
	public void transferSubclassFieldsTo(SubclassableTreeNodeTemplate newNode) {
		newNode.name = name;
	}

	// OTHER METHODS ///////////////////////////////////////

	@Override
	public String toString() {
		return name + ": ";
	};

}
