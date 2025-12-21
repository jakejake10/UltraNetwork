import unCore.*;
import utTypes.*;

import java.util.Random;
import java.util.List;
import java.util.stream.*;

DataNode<Integer> root;
int[] colors;


void setup(){
  size( 750,500 );
  noLoop();
  
  noFill();
  stroke(0);
  strokeWeight(2);
  
  colors = new int[]{
    color(84,63,46),
    color(251,209,104),
    color(246,178,161),
    color(218,78,83),
    color(40,167,142),
    color(184,217,206),
    color(48,95,139),
    color(31,51,88),
    color(209,43,47),
    color(252,187,28),
    color(224,216,197),
    color(18,25,51)
  };
  
  background(colors[9]);
  
  
  root = new DataNode<>();
    root.buildTree()
      .setChildCt(2)
      .setChildCtMod( (n,i) -> floor(random( 2,4)) )
      .setMaxDepth( 3 )
      .setPostGenMod( n -> {
        n.setData(n.getDepth());
        if( n.isLeaf() ) for( int i = 0; i < floor(random(1,6)); i++) n.getLeafs().get(0).addChild();
      })
      //.setLeafCt(45)
    
//      .setChildCt(2)
//      .setMaxDepth(2)
//      .setMaxDepthMod( (n,i) -> i + floor(random(10)) )
//      .setPostGenMod( n -> { if(n.isLeaf()) n.setData(1); } )
      
//      .setChildCtByDepth( 3,2,1 )
//      .setPostGenMod( n -> n.setData( n.getChildCount() ) )
      
      .make();
    root.printOperation();
    
    rectMode(CENTER);
    
    TreeNodeFunctions.renderTree( root, this )
    .setNodeDims( 25,25 )
    .setSpacing(25,25)
    .setPosition( 375,50 )
    .make()
    .renderLines()
    .renderNodes( (n,p)->{
      rect(p[0],p[1],25,25);
      if( random(1) < 0.3 ){
        fill( colors[floor(random(colors.length))] );
        noStroke();
        rect(p[0],p[1],15,15);
        stroke(0);
        noFill();
      }
      if( n.isRoot()){
        fill( 200);
        noStroke();
        rect(p[0],p[1],15,15);
        stroke(0);
        noFill();
      }
    });
    
    save("out.jpg");
}

void draw(){
}
