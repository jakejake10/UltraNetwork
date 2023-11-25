package utTypes;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SelectionTree<T> extends TreeComposedClass<SelectionTree<T>.SelectionData> {
	
	public Function<TreeNode<SelectionData>,TreeNode<SelectionData>> defaultTraverseFn = 
			n -> n.get( indexByWeight(n) );
	
	
	public SelectionTree(){
		super();
		root.setData( new SelectionData() );
	}
	
	
	public T get() {
		return root.topDownReturnOperation(n -> n.hasChildren() ?  n.getData().nextNodeFn.apply(n) : null,
				n -> n.getData().value );
	}
	
	public T get( Function<TreeNode<SelectionData>,TreeNode<SelectionData>> nextNodeFn ) {
		return root.topDownReturnOperation(n -> n.hasChildren() ?  nextNodeFn.apply(n) : null,
				n -> n.getData().value );
	}
	
	
	public TreeNode<SelectionData> getNode() {
		return root.topDownReturnOperation(n -> n.hasChildren() ?  n.getData().nextNodeFn.apply(n) : null,
				n -> n );
	}
	
	public Supplier<T> convertToSupplier(){
		return () -> convertToFn().apply(root);
	}
	
	public Function<TreeNode<SelectionData>,T> convertToFn(){
		return n -> {
			while(n.hasChildren() ) {
				n = n.getData().nextNodeFn.apply(n);
			}
			return n.getData().value;
		};
	}
	
	
	public void addValues(T[] values, float[] weights) {
		root.addChild( IntStream.range(0, values.length )
				.mapToObj( i -> new SelectionData( values[i],weights[i]) )
				.collect(Collectors.toList()) );
	}
	public void addValues(List<T> values) {
		root.addChild( values.stream().map(t -> new SelectionData(t) ).collect(Collectors.toList()) );
	}
	
	public void addValues( TreeNode<SelectionData> node, List<T> values) {
		node.addChild( values.stream().map(t -> new SelectionData(t) ).collect(Collectors.toList()) );
	}
	
	public float sumOfWeight( TreeNode<SelectionData> node ) {
		if( node.isLeaf() ) return node.getData().weight;	
		else 
			return (float)node.getChildren().stream().mapToDouble(n -> n.getData().weight ).sum();
	}
	
	public int indexByWeight( TreeNode<SelectionData> node ) {
		if( !node.checkNonLeafErrorMSG("sumOfWeight()")) return -1;
		float sumOfWeight = sumOfWeight(node);
		float rnd = (float)Math.random() * sumOfWeight;
		float cumulativeProbability = 0;
		for( TreeNode<SelectionData> nc : node.getChildren() ) {
			cumulativeProbability += nc.getData().weight;
			if( rnd < cumulativeProbability )  return nc.indexInParent();
		}
		return node.size - 1;
	}
	
	
	public <E> void selectFn( List<E> applyObjs, BiConsumer<T,E> applyFn ) {
		for( E obj : applyObjs ) applyFn.accept( get(), obj );
	}
	
	
	
	public class SelectionData{
		public Function<TreeNode<SelectionData>,TreeNode<SelectionData>> nextNodeFn = defaultTraverseFn;
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
	
}
