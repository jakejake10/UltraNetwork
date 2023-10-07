package utTypes;

import java.util.Iterator;

import unCore.*;


public class TreeNode<D> extends TreeNodeStruct<TreeNode<D>, D> implements Iterable<TreeNode<D>>{

	//NODE ABSTRACT METHODS ////////////////////////////////////////////////
	public TreeNode( int...init ){	// root constructor
		super( init );
	}
	public TreeNode( TreeNode<D> input ){	// root constructor
		super( input );
	}

	// NodeObjInterface
	@Override
	public TreeNode<D> getInstance() {
		return this;
	}
	

	@Override
	public TreeNode<D> defaultConstructor( int...init ) {
		return new TreeNode<>( init );
	}
	@Override
	public TreeNode<D> defaultConstructor( TreeNode<D> input ) {
		return new TreeNode<>( input );
	}
	@Override
	public Iterator<TreeNode<D>> iterator() {
		return nodeIterator();
	}
	
}
