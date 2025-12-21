import unCore.*;
import utTypes.*;

import java.util.Iterator;

DataNode<Integer> root, root2;


void setup(){
  size( 50, 50 );
  noLoop();
  
  root = new DataNode<>( 0 );
    root.addChild( new DataNode(1));
    root.addChild( new DataNode(5) );
  
    
  root.saveTreeJSON( "out.json", this );
  root2 = TreeNodeObject.loadTreeJSON( "out.json", s-> new DataNode(), this );
  
  
}

void draw(){
  root.printOperation();
  root2.printOperation();
}
