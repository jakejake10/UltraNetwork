package utTypes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import unCore.TreeNodeStruct;


public class SelectionNode<T> extends TreeNodeStruct<SelectionNode<T>,SelectionNode<T>.SelectionData> {
	
	public Function<SelectionNode<T>,SelectionNode<T>> defaultTraverseFn = 
			n -> n.get( indexByWeight(n) );
	
	
	public SelectionNode(){
		super();
		setData( new SelectionData() );
	}
	
	public SelectionNode( SelectionData data ){
		super();
		setData( data );
	}
	
	
	public T get() {
		return topDownReturnOperation(n -> n.hasChildren() ?  n.getData().nextNodeFn.apply(n) : null,
				n -> n.getData().value );
	}
	
	public T get( Function<SelectionNode<T>,SelectionNode<T>> nextNodeFn ) {
		return topDownReturnOperation(n -> n.hasChildren() ?  nextNodeFn.apply(n) : null,
				n -> n.getData().value );
	}
	
	
	public SelectionNode<T> getNode() {
		return topDownReturnOperation(n -> n.hasChildren() ?  n.getData().nextNodeFn.apply(n) : null,
				n -> n );
	}
	
	public Supplier<T> convertToSupplier(){
		return () -> convertToFn().apply( getRoot());
	}
	
	public Function<SelectionNode<T>,T> convertToFn(){
		return n -> {
			while(n.hasChildren() ) {
				n = n.getData().nextNodeFn.apply(n);
			}
			return n.getData().value;
		};
	}
	
	
	/*
	 * new fn
	 * adds list of values to a parent node with weighted probability
	 */
	public void addSelectionNodeGroup( float prob, List<T> input ) {
		getRoot().addChild( new SelectionNode<T>( new SelectionData( null, prob ) ) );
		for( T val : input ) getRoot().lastChild().addChild( new SelectionNode<T>( new SelectionData( val ) ) );
	}
	
	public void addValues(T[] values, float[] weights) {
		List<SelectionData> input = IntStream.range(0, values.length )
				.mapToObj( i -> new SelectionData( values[i],weights[i]) )
				.collect(Collectors.toList());
		for( SelectionData sd : input ) getRoot().addChild( new SelectionNode<T>( sd ) );
	}
	public void addValues(List<T> values) {
		for( SelectionData sd : values.stream().map(t -> new SelectionData(t) ).collect(Collectors.toList()) )
			getRoot().addChild( new SelectionNode<T>( sd ) );
	}
	
	public static <E> void addValues( SelectionNode<E> node, List<E> values) {
		for( SelectionNode<E>.SelectionData sd : 
			values.stream().map( t -> node.new SelectionData(t) )
				.collect(Collectors.toList()) )
			node.addChild( new SelectionNode<E>( sd ) );
	}
	
	public float sumOfWeight( SelectionNode<T> node ) {
		if( node.isLeaf() ) return node.getData().weight;	
		else 
			return (float)node.getChildren().stream().mapToDouble(n -> n.getData().weight ).sum();
	}
	
	public int indexByWeight( SelectionNode<T> node ) {
		if( !node.checkNonLeafErrorMSG("sumOfWeight()")) return -1;
		float sumOfWeight = sumOfWeight(node);
		float rnd = (float)Math.random() * sumOfWeight;
		float cumulativeProbability = 0;
		for( SelectionNode<T> nc : node.getChildren() ) {
			cumulativeProbability += nc.getData().weight;
			if( rnd < cumulativeProbability )  return nc.indexInParent();
		}
		return node.childCt - 1;
	}
	
	
	public <E> void selectFn( List<E> applyObjs, BiConsumer<T,E> applyFn ) {
		for( E obj : applyObjs ) applyFn.accept( get(), obj );
	}
	
	
	
	public class SelectionData{
		public Function<SelectionNode<T>,SelectionNode<T>> nextNodeFn = defaultTraverseFn;
		public float weight = 1;
		public T value;
		
		public SelectionData(){}
		public SelectionData( T value ){
			this.value = value;
		}
		public SelectionData( T value, float weight ){
			this.value = value;
			this.weight = weight;
		}
	}



	@Override
	public SelectionNode<T> getInstance() {
		return this;
	}

	@Override
	public SelectionNode<T> defaultConstructor() {
		return new SelectionNode<T>();
	}

	@Override
	public Iterator<SelectionNode<T>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
