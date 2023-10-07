package utTypes;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import unCore.AbstractGraphNode;
import unCore.Graph;

public class BGNode extends AbstractGraphNode<BGNode> {
	
	public BGNode( Graph<BGNode> input, int index ){
		super( input, index );
	}
	
	// ABSTRACT METHODS ///////////////////////////
	
	public BGNode getInstance() {
		return this;
	}
	
	public  BGNode defaultConstructor( Graph<BGNode> input ) {
		return new BGNode( input, input.nodes.size() );
	}
	
	public static BGNode staticConstructor( Graph<BGNode> input ) {
		return new BGNode( input, input.nodes.size() );
	}

}
