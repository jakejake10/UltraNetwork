
public class ColorNode implements TreeNodeObject<ColorNode>, Iterable<ColorNode> {
  //TreeNodeObject fields
  SingularTreeData<ColorNode> core;
  
  public int index;
  public int parentIndex = -1; // index in tree.nodes
  public int firstChild = -1;
  public int childCt = 0;
  public int depth = 0;
  
  //Unique Fields
  public String myName;
  public Integer myColor;
  
  
  public ColorNode() {
    initNodeFields();
  }
  
  public ColorNode( String myName ) {
    this();
    this.myName = myName;
  }
  public ColorNode( String myName, Integer myColor ) {
    this();
    this.myName = myName;
    this.myColor = myColor;
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
    
  public SingularTreeData<ColorNode> getCore(){
    return core;
  }
  public void setCore( SingularTreeData<ColorNode> input ){
    this.core = input;
  }
  
  
  public ColorNode getInstance(){
    return this;
  }
  
  public ColorNode defaultConstructor(){
    return new ColorNode();
  }
  
  public Iterator<ColorNode> iterator() {
    return nodeIterator();
  }
  
  // OPTIONAL METHODS //////////////////////////
  
  public void transferSubclassFieldsTo( ColorNode newNode ){
    newNode.myName = myName;
    newNode.myColor = myColor;
  }
  
    
  
  public String toString(){
    return myName + ": " + (myColor == null ? "" : myColor );
  }
  
  // UNIQUE METHODS /////////////////////////////
  
  
  
}
