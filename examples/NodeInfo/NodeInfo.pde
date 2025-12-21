import unCore.*;
import utTypes.*;

import java.util.function.*;
import java.util.Random;
import java.util.List;
import java.util.stream.*;

Node root;


void setup(){
  size( 1000,600 );
  frameRate( .5 );
  
  buildTree();
}

void draw(){
  drawTree();
  if( frameCount == 5 ) noLoop();
}
