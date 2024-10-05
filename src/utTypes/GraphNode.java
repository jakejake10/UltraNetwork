package utTypes;

import java.util.Iterator;

import unCore.*;


public class GraphNode<D extends Copyable<D>> extends GraphNodeStruct<GraphNode<D>, D> implements Iterable<GraphNode<D>>{

	//NODE CONSTRUCTORS ////////////////////////////////////////////////
		public GraphNode( BaseNodeCommand...initType ){	// root/null constructor
			super(initType);
		}
		public GraphNode( GraphNode<D> input ){	// node constructor
			NodeObj.nodeInitParChild( input, getInstance() );
		}
		public GraphNode( D data ){	// root data constructor
			this();
			setData(data);
		}
		public GraphNode( GraphNode<D> input, D data ){	// node data constructor
			this(input);
			setData(data);
		}

	// NodeObjInterface
	@Override
	public GraphNode<D> getInstance() {
		return this;
	}
	

	@Override
	public GraphNode<D> defaultConstructor() {
		return new GraphNode<D>( new BaseNodeCommand() );
	}
	
	@Override
	public Iterator<GraphNode<D>> iterator() {
		return nodeIterator();
	}
	
}
