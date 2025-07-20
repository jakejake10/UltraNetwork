/*
  - subclasses of MoneyObject
  - inherit node functionality along with any unique fields and methods
*/

class Expense extends MoneyObject{
  int timesPerYear = 12; 
  float cost = 0;
  
  Expense( String name, float cost ){
    super( name, "expense" );
    this.cost = cost;
  }
  
  float getValue(){
    return cost * -1;
  }
  
  public Expense defaultConstructor(){
    return new Expense( "", 0 );
  }
  public Expense getInstance(){
    return this;
  }
  
  @Override 
  public Expense copyNode( boolean transferNodeData ){
    return (Expense)super.copyNode( transferNodeData );
  }
  @Override
  public void transferSubclassFieldsTo( MoneyObject newNode ){
    super.transferSubclassFieldsTo( newNode );
    ((Expense)newNode).timesPerYear = timesPerYear;
    ((Expense)newNode).cost = cost;
  }
  
  @Override
  public String toString(){
    return super.toString() + getValue();
  };
  
  @Override
  public boolean allowChildren(){
    return false;
  }
  
}


class Salary extends MoneyObject{
  float salary;
  
  Salary( String name, float salary ){
    super( name, "salary" );
    this.salary = salary;
  }
  
  float getValue(){
    return salary / 12;
  }
  public Salary defaultConstructor(){
    return new Salary( "", 0 );
  }
  public Salary getInstance(){
    return this;
  }
  
  @Override 
  public Salary copyNode( boolean transferNodeData ){
    return (Salary)super.copyNode( transferNodeData );
  }
  @Override
  public void transferSubclassFieldsTo( MoneyObject newNode ){
    super.transferSubclassFieldsTo( newNode );
    ((Salary)newNode).salary = salary;
  }
  
  @Override
  public String toString(){
    return super.toString() + getValue();
  };
  
  @Override
  public boolean allowChildren(){
    return false;
  }
  
}

/*
  - used as group node for non leaf nodes
*/

class AssetGroup extends MoneyObject{
  
  AssetGroup( String name ){
    super( name,"assetGroup" );
  }
  
  float getValue(){
    float out = 0;
    if( hasChildren() ) for( MoneyObject c : getChildren() ) out += c.getValue();
    return out;
  }
  
  public AssetGroup defaultConstructor(){
    return new AssetGroup( "" );
  }
  public AssetGroup getInstance(){
    return this;
  }
  
  @Override 
  public AssetGroup copyNode( boolean transferNodeData ){
    return (AssetGroup)super.copyNode( transferNodeData );
  }
  @Override
  public void transferSubclassFieldsTo( MoneyObject newNode ){
    super.transferSubclassFieldsTo( newNode );
  }
  
  @Override
  public String toString(){
    return super.toString() + ": " + getValue();
  };
  
}
