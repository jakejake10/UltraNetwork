//package utTypes;
//
//import java.util.List;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//import unCore.*;
//import uvCore.RangeObject;
//
//public class SelectionTree<T> extends AbstractTree<SelectionTree<T>,SelectionNode<T>>{
//
//	
//	public SelectionTree() {}
//	
//	
//	@Override
//	public SelectionTree<T> getInstance() {
//		return this;
//	}
//
//	@Override
//	public SelectionNode<T> nodeDefaultConstructor() {
//		return new SelectionNode<T>();
//	}
//	
//	
////	public SelectionTree<T> addSelectionNodes( List<T> input ) {
////		for( T t : input ) root().addChild( new SelectionNode<T>( t ) );
////		return this;
////	}
////	public void addSelectionNode( T input ) {
////		root().addChild( new SelectionNode<T>( input ) );
////	}
////	
////	public SelectionTree<T> addSelectionNodeGroup( float prob, List<T> input ) {
////		root().addSelectionNodeGroup( prob, input );
////		return this;
////	}
////	
////	public <E> SelectionTree<T> addSelectionNodeGroup( float prob, List<T> input, Class<E> clazz, Function<E,Float> indexValueFn ) {
////		root().addSelectionNodeGroup( prob, input, clazz, indexValueFn );
////		return this;
////	}
////	
////	public void addSelectionGroup() {
////		root().addChild( new SelectionNode<T>() );
////	}
////	
////	public void addSelectionGroup( List<T> input ) {
////		root().addChild();
////		for( T t : input ) root().lastChild().addChild( new SelectionNode<T>( t ) );
////	}
//	
//	
////	public T getRandom() {
////		return root().getRandom();
////	}
////	
////	public T getRandomDisplay() {
////		return root().getRandomDisplay();
////	}
//	
//	public T selectValue() {
//		return root().selectValue( null );
//	}
//	
//	public <E> T selectValue( E input ) {
//		return root().selectValue( input );
//	}
//	
//	
//	/*
//	 * example: select random color, for UShape, setFill(rColor), in each of list of US
//	 * 		- or just fill( rColor ) rect.display()
//	 */
////	public <E> void apply( Supplier<T> inputFn, List<E> applyObjs, BiConsumer<T,E> applyFn ) {
////		for( E obj : applyObjs ) applyFn.accept( inputFn.get(), obj );
////	}
////	public <E> void apply( List<E> applyObjs, BiConsumer<T,E> applyFn ) {
////		for( E obj : applyObjs ) applyFn.accept( root().selectValue( obj ), obj );
////	}
//	
//	
//	
//	// FUNCTIONS //////////////////////////////
//	
////	public Supplier<T> getRandom = () -> root().getRandom();
//	
////	public <E> Function<E,T> getRandom = e -> root().getRandom( e );
//
//	
//}
