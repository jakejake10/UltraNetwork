import unCore.*;
import utTypes.*;

Node root;


void setup(){
  frameRate( 1 );
  root = new Node();
    root.addChild();
      root.get(0).addChild();
    root.addChild();
  
  root.printOperation();
  
  println("dfs traversal:");
  for( Node n : root ) println( n.getIndex() ); //iterates depth first by default
  println("dfs traversal:");
  for( Node n : root.bft() ) println( n.getIndex() );
  
}


void draw(){}
