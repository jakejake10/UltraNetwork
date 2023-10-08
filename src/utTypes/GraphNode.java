package utTypes;

import java.util.Iterator;

import unCore.*;


public class GraphNode<D> extends GraphNodeStruct<GraphNode<D>, D> implements Iterable<GraphNode<D>>{

	//NODE ABSTRACT METHODS ////////////////////////////////////////////////
	public GraphNode( int...init ){	// root constructor
		super( init );
	}
	public GraphNode( GraphNode<D> input ){	// root constructor
		super( input );
	}
	public GraphNode( D data ){	// root constructor
		super( data );
	}

	// NodeObjInterface
	@Override
	public GraphNode<D> getInstance() {
		return this;
	}
	

	@Override
	public GraphNode<D> defaultConstructor( int...init ) {
		return new GraphNode<>( init );
	}
	@Override
	public GraphNode<D> defaultConstructor( GraphNode<D> input ) {
		return new GraphNode<>( input );
	}
	@Override
	public GraphNode<D> defaultConstructor( D data ) {
		return new GraphNode<>( data );
	}
	@Override
	public Iterator<GraphNode<D>> iterator() {
		return nodeIterator();
	}
	
}
