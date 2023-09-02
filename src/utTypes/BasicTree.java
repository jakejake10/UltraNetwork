package utTypes;

import utCore.AbstractTree;

/*
 * most basic implementation of abstract tree / node
 */

public class BasicTree extends AbstractTree<BasicTree,BasicNode> {

	public BasicTree(){}
	
	// abstract inherited methods //////////////////////////////////
		public BasicTree defaultConstructior() {
			return new BasicTree();
		}
		
		public BasicTree getInstance() {
			return this;
		}
		
		public BasicNode nodeDefaultConstructor() {
			return new BasicNode();
		}
}
