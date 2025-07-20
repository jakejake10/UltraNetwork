import unCore.*;
import utTypes.*;


import java.util.Iterator;


void setup(){
  //pa = this;
  
  AssetGroup root = new AssetGroup( "monthly income" );
    root.addChild( new AssetGroup( "monthly gross income" ) );
      root.get(0).addChild( new Salary( "sal1", 50000 ) );
      root.get(0).addChild( new Salary( "sal2", 20000 ) );
    root.addChild( new AssetGroup( "monthly expenses" ) );
      root.get(1).addChild( new Expense( "internet", 100 ) );
      root.get(1).addChild( new Expense( "phone", 150 ) );
  
  // deep copy of original root not affected by field change
  AssetGroup copy = (AssetGroup)root.copyNodeSubtree();
  ((Salary)root.findFirst( n -> n.name.equals("sal1"))).salary = 200000; // modify original tree
  
  println("original tree:");
  root.printOperation( n -> n );
  println();
  println("copied tree:");
  copy.printOperation( n -> n );
  
}
