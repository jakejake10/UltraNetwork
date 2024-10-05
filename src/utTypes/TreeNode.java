package utTypes;

import java.util.Iterator;

import unCore.*;


public class TreeNode<D> extends TreeNodeStruct<TreeNode<D>, D> implements Iterable<TreeNode<D>>{

	//NODE CONSTRUCTORS ////////////////////////////////////////////////
	public TreeNode( BaseNodeCommand...initType ){	// root/null constructor
		super(initType);
	}
	public TreeNode( TreeNode<D> input ){	// node constructor
		NodeObj.nodeInitParChild( input, getInstance() );
	}
	public TreeNode( D data ){	// root data constructor
		this();
		setData(data);
	}
	public TreeNode( TreeNode<D> input, D data ){	// node data constructor
		this(input);
		setData(data);
	}

	// NodeObjInterface
	@Override
	public TreeNode<D> getInstance() {
		return this;
	}
	

	@Override
	public TreeNode<D> defaultConstructor() {
		return new TreeNode<D>( new BaseNodeCommand() );
	}

//	@Override
//	public TreeNode<D> defaultNodeConstructor( TreeNode<D> input ) {
//		return new TreeNode<>( input );
//	}
//	@Override
//	public TreeNode<D> defaultConstructor( D data ) {
//		return new TreeNode<>( data );
//	}
	
	@Override
	public Iterator<TreeNode<D>> iterator() {
		return nodeIterator();
	}
	@Override
	public TreeNode<D> copyNode() {
		// TODO Auto-generated method stub
		return null;
	}
//	
	
	
	
	
}
