/*
  - superclass that stores common methods and fields for all subclasses
  - implements TreeNodeObject functionality
  - defining abstract methods shared by any subclass
*/

abstract class MoneyObject implements TreeNodeObject<MoneyObject>, Iterable<MoneyObject>{
  SingularTreeData<MoneyObject> core;
  
  public int index;
  public int parentIndex = -1; // index in tree.nodes
  public int firstChild = -1;
  public int childCt = 0;
  public int depth = 0;
  
  
  String type = "";
  String name = "";
  
  
  
  MoneyObject( String name, String type ){
    initNodeFields();
    this.name = name;
    this.type = type;
  }
  
  // ABSTRACT METHODS //////////////////////
  
  abstract float getValue();
  
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
    
  public SingularTreeData<MoneyObject> getCore(){
    return core;
  }
  public void setCore( SingularTreeData<MoneyObject> input ){
    this.core = input;
  }
  
  
  
  public Iterator<MoneyObject> iterator() {
    return nodeIterator();
  }
  
  // OPTIONAL METHODS //////////////////////////
  
  @Override
  public void transferSubclassFieldsTo( MoneyObject newNode ){
    newNode.name = name;
  }
    
  
  
  // OTHER METHODS /////////////////////////////
  
  
  
  @Override
  public String toString(){
    return name + ": ";
  };
  
}
