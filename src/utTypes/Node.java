package utTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

import processing.data.JSONObject;
import unCore.*;


public class Node implements TreeNodeObject<Node>, Iterable<Node> {
  SingularTreeData<Node> core;
  
  public int index;
  public int parentIndex = -1; // index in tree.nodes
  public int firstChild = -1;
  public int childCt = 0;
  public int depth = 0;
  
  public Node() {
	  initNodeFields();
  }
  
//  public Node( D data ){
//    initNodeFields();
//    setData( data );
//  }
  
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
    
  public SingularTreeData<Node> getCoreFn(){
    return core;
  }
  public void setCore( SingularTreeData<Node> input ){
    this.core = input;
  }
  
  
  public Node getInstance(){
    return this;
  }
  
  public Node defaultConstructor(){
    return new Node();
  }
  
  public Iterator<Node> iterator() {
    return nodeIterator();
  }
  
  // OPTIONAL METHODS //////////////////////////
  
//  public void transferSubclassFieldsTo( Node newNode ){
//    newNode.setData( getData() );
//  }
  
  
 
  
  

  
  
  public String toString(){
    return "node: " + getIndex();
  }
  
}




