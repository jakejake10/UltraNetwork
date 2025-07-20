import unCore.*;
import utTypes.*;

Node root;

void setup(){
  size( 500,500 );
  frameRate( 20 );
  root = new Node();
    root.addChild();
    root.addChild();
  
}

void draw(){
  root.getRandomNode().addChild();
  background(255);
  TreeNodeFunctions.renderTree( root, this ).renderTree( 250, 100 );
}
