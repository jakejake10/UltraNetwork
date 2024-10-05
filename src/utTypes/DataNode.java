package utTypes;

import java.util.Iterator;
import java.util.function.*;

import unCore.*;


public class DataNode<D> implements TreeNodeObject<DataNode<D>>, Iterable<DataNode<D>> {
  SingularTreeData<DataNode<D>> core;
  D data;
  
  public int index;
  public int parentIndex = -1; // index in tree.nodes
  public int firstChild = -1;
  public int childCt = 0;
  public int depth = 0;
  
  public DataNode( D data ){
    initNodeFields();
    setData( data );
  }
  
  // TREENODE Interface ////////////////////
  public int getIndex(){
    return index;
  }
  public void setIndex( int index ){
    this.index = index;
  }
  public int getParentIndex(){
    return parentIndex;
  }
  public void setParentIndex( int index ){
    this.parentIndex = index;
  }
  public int getFirstChildIndex(){
    return firstChild;
  }
  public void setFirstChildIndex( int index ){
    this.firstChild = index;
  }
  public int getDepth(){
    return depth;
  }
  public void setDepth( int depth ){
    this.depth = depth;
  }
  public int getChildCount(){
    return childCt;
  }
  public void setChildCount( int count ){
    this.childCt = count;
  }
    
  public SingularTreeData<DataNode<D>> getCore(){
    return core;
  }
  public void setCore( SingularTreeData<DataNode<D>> input ){
    this.core = input;
  }
  
  
  public DataNode<D> getInstance(){
    return this;
  }
  
  public DataNode<D> defaultConstructor(){
    return new DataNode<>( null );
  }
  
  public Iterator<DataNode<D>> iterator() {
    return nodeIterator();
  }
  
  // OPTIONAL METHODS //////////////////////////
  
  public void transferSubclassFieldsTo( DataNode<D> newNode ){
    newNode.setData( getData() );
  }
  
  
  // DATA METHODS //////////////////////////////
  public D getData(){
    return data;
  }
  public void setData( D input ){
    this.data = input;
  }
  public boolean hasData(){
    return getData() != null; 
  }
  
  public boolean equalsData( D dataIn ){
    return getData().equals( dataIn );
  }
  
  public void addChild( D data ){
    addChild( n -> n.setData( data ) );
  }
  
  // OTHER METHODS ///////////////////////////////////////
  
  public <E> DataNode<E> convertData( Function<D,E> convertFn ){
    return convertNodeSubtree( n -> new DataNode<>( convertFn.apply( n.getData() ) ) );
  }
  
  public String toString(){
    return "node: " + ( getData() != null ? getData().toString() : "null" );
  }
  
}
