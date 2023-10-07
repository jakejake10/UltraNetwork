package utTypes;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import unCore.*;
import uvCore.RangeObject;

public class SelectionNode<T> extends AbstractNode<SelectionTree<T>,SelectionNode<T>> {
	public T data;
	public List<Float> probVals = new ArrayList<>();
	public RangeObject probability;
	public String name;
//	Class<?> myClass;
//	public Function<?,Float> customSelectionFn; // normalized value;
	IndexMaker<?> customSelectionFn;
	
	
	
	public SelectionNode() {	// selection group constructor
	}
	
	public SelectionNode( T data ) {
		this.data = data;
	}
	
	// ABSTRACT FNS //////////////////////////////////////
	
	@Override
	public SelectionNode<T> defaultConstructor() {
		return new SelectionNode<T>();
	}

	@Override
	public SelectionNode<T> getInstance() {
		return this;
	}
	
	@Override
	public SelectionTree<T> treeDefaultConstructor() {
		return new SelectionTree<T>();
	}
	
	// GET FNS ///////////////////////////////////////////
	
	public T getData() {
		return data;
	}
	
//	public int getRandomIndex() {
//		if( probability == null && probVals.size() == getChildCount() )
//			probability = new RangeObject( getChildCount() ).setValues( probVals ).sumSequenceAddStart();
//		if( probability != null )  return probability.getContainingDelta( (float)Math.random() ) - 1;
//		return (int) ( Math.random() * getChildCount() );
//	}
	
	public <E> int getIndex( E selectionInput ) {
		float normSelectionVal = 0;
		
		if( customSelectionFn != null ) {
			if( selectionInput.getClass().equals( customSelectionFn.type ) )
				normSelectionVal = customSelectionFn.getNormalizedValue( selectionInput  );
		}
		else normSelectionVal = (float)Math.random();
		
//		if( probability == null && probVals.size() == getChildCount() )
//			probability = new RangeObject( getChildCount() ).setValues( probVals ).sumSequenceAddStart();
//		if( probability != null )  return probability.getContainingDelta( normSelectionVal ) - 1;	//modify ro.gtd() to sub 1
		
		return (int) ( normSelectionVal * getChildCount() );
	}
	
	// OUTPUT FNS ////////////////////////////////
	
	public <E> T selectValue() {
		return selectValue( null );
	}
	
	public <E> T selectValue( E selectionInput ) {
		if( hasChildren() ) return get( getIndex( selectionInput ) ).selectValue( selectionInput );
		else return data;
	}
	
	/*
	 * runs operation like rect.display(this) with a selected value
	 */
	public <E> void selectFn( List<E> applyObjs, BiConsumer<T,E> applyFn ) {
		for( E obj : applyObjs ) applyFn.accept( selectValue( obj ), obj );
	}
	
	
	// ADD FNS ///////////////////////////////////////////
	
	public void addSelectionNodes( List<T> input ) {
		data = null;
		for( T t : input ) addChild( new SelectionNode<T>( t ) );
	}
	public void addSelectionNode( T input ) {
		addChild( new SelectionNode<T>( input ) );
	}
	
	public SelectionNode<T> addSelectionNodeGroup( float prob, List<T> input ) {
		addChild();
		SelectionNode<T> tarNode = get( getChildCount() - 1 );
		tarNode.addSelectionNodes( input );
		probVals.add( prob );
		return this;
	}
	
	public <E> SelectionNode<T> addSelectionNodeGroup( float prob, List<T> input, Class<E> clazz, Function<E,Float> indexValueFn ) {
		addChild();
		SelectionNode<T> tarNode = get( getChildCount() - 1 );
		tarNode.addSelectionNodes( input );
		tarNode.setSelectionFn(clazz, indexValueFn );
		probVals.add( prob );
		return this;
	}
	
	// PROBABILITY ////////////////////////////////
	
	public void setProbability( float...probs ) {
		if( probs.length != getChildCount() ) return;
//		probability = new RangeObject( getChildCount() ).setValues(probs).sumSequenceAddStart();
	}
	
	//////
	
//	public void setCustomIndexFn(String clazz, Function<?,Float> indexFn ) {
//		try {
//			this.myClass = Class.forName(clazz);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		this.customSelectionFn = indexFn;
//	}
	
	public <E> void setSelectionFn( Class<E> clazz, Function<E,Float> fn ) {
		this.customSelectionFn = new IndexMaker<E>( clazz, fn );
	}
	
	
	public class IndexMaker<I>{
		Function<I,Float> fn;
		Class<I> type;
		
		public IndexMaker( Class<I> clazz, Function<I,Float> fn ){
			this.fn = fn;
			this.type = clazz;
//			try {
////				this.type = (Class<I>)Class.forName(clazz);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
		}
		
		public Class returnedClass() {
		     ParameterizedType parameterizedType = (ParameterizedType)getClass()
		                                                 .getGenericSuperclass();
		     return (Class) parameterizedType.getActualTypeArguments()[0];
		}
		
		
		public <B> float getNormalizedValue( B input ) {
//			return fn.apply( type.cast(input) );
			if( input.getClass() == type )
				return fn.apply( (I)(input) );
			else return 0;
		}
	}


	
	


}
