import unCore.*;
import utTypes.*;

import java.util.Random;
import java.util.List;
import java.util.stream.*;

Node root;


void setup(){
  size( 1000,500 );
  frameRate( 2 );
  
  buildTree();
}

void draw(){
  //drawTree1();
  //drawTree2();
  drawTree3();
  //println("done");
}
