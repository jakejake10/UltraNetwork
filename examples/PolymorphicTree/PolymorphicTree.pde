import unCore.*;
import utTypes.*;


import java.util.Iterator;

AssetGroup root;
PFont font;


void setup(){
  size( 1000,500 );
  background(17);
  PFont font = createFont( "Courier New Bold", 16 );
  textFont(font);
  fill(200);
  stroke(200);
  
  root = new AssetGroup( "monthly income" );
    root.addChild( new AssetGroup( "monthly gross income" ) );
      root.get(0).addChild( new Salary( "sal1", 50000 ) );
      root.get(0).addChild( new Salary( "sal2", 20000 ) );
    root.addChild( new AssetGroup( "monthly expenses" ) );
      root.get(1).addChild( new Expense( "internet", 100 ) );
      root.get(1).addChild( new Expense( "phone", 150 ) );
  
  // deep copy of original root not affected by field change
  AssetGroup copy = (AssetGroup)root.copyNodeSubtree();
  //((Salary)root.findFirst( n -> n.name.equals("sal1"))).salary = 200000; // modify original tree
  
  println("original tree:");
  root.printOperation( n -> n );
  println();
  println("copied tree:");
  copy.printOperation( n -> n );
  
  textAlign(CENTER,CENTER);
  
  TreeNodeFunctions.TreeRenderBuilder<MoneyObject> render = 
  TreeNodeFunctions.renderTree( root, this )
    .setNodeDims(240, 50)   
    .setSpacing(15,65)
    .setPosition( 500,130 )
    .make()
    .renderLines()
    .renderNodes( (n,p) -> {
      text(n.type + ": " + n.name, p[0],p[1]-10 );
      text("monthly delta: $" + nf(n.getMonthlyDelta(),0,2), p[0],p[1]+10 );
      if( n.type.equals("salary") ) text("salary: $" + ((Salary)n).salary, p[0],p[1]+30 );
      if( n.type.equals("expense") ) text("cost: $" + ((Expense)n).cost, p[0],p[1]+30 );
    })
    ;
  save("out.jpg");
}
