package utTypes;

import java.util.Iterator;
import java.util.List;

import utCore.AbstractNode;
import utCore.AbstractTree;

/*
 * most basic implementation of abstract tree / node
 */

public class BasicNode extends AbstractNode<BasicTree,BasicNode> {

	public BasicNode() {}
	
	
	// abstract inherited methods //////////////////////////////////
	public BasicNode defaultConstructor() {
		return new BasicNode();
	}
	
	public BasicNode getInstance() {
		return this;
	}
	
	public BasicTree treeDefaultConstructor() {
		return new BasicTree();
	}


		
	// NODE FNS INTERFACE /////////////////////////////////////////
	
	

	
	
	
}
