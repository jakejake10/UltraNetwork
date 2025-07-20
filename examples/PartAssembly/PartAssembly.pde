import unCore.*;
import utTypes.*;
import java.util.stream.*;

import java.util.Iterator;

DataNode<Part> tla;
TreeNodeFunctions.TreeRenderBuilder<DataNode<Part>> render;


void setup(){
  size( 1000, 700 );
  frameRate(4);
  noLoop();
  
 
  
  DataNode<Part> leftShroudAsm = new DataNode<>( new Part("Left Shroud Asm", "assembly", 1 ) );
    leftShroudAsm.addChild( new DataNode<>( new Part("L Shroud", "plastics",  10.40, 1 ) ) ); 
    leftShroudAsm.addChild( new DataNode<>( new Part("M3 screws", "fastener", 0.13, 6 ) ) );
    leftShroudAsm.addChild( new DataNode<>( new Part("Logo","decal", 1.40, 1 ) ) );
    //leftShroudAsm.addChild( new DataNode<>( new Part("Safety Lab","decal", 0.75, 1 ) ) );
  DataNode<Part> rightShroudAsm = new DataNode<>( new Part("Right Shroud Asm", "assembly", 1 ) );
    rightShroudAsm.addChild( new DataNode<>( new Part("R Shroud", "plastics", 10.40, 1 ) ) ); 
    rightShroudAsm.addChild( new DataNode<>( new Part("M3 Screws", "fastener", 0.13, 6 ) ) );
    rightShroudAsm.addChild( new DataNode<>( new Part("Logo","decal", 1.40, 1 ) ) );
  DataNode<Part> chassisAsm = new DataNode<>( new Part("Chassis Asm", "assembly", 1 ) );
    chassisAsm.addChild( new DataNode<>( new Part("1/2 Blt", "fastener", 0.24, 12 ) ) );
    //chassisAsm.addChild( new DataNode<>( new Part("1/2 Nut","fastener", 0.11, 12 ) ) );
    chassisAsm.addChild( new DataNode<>( new Part("Main Tube","tube", 4.55, 1 ) ) );
    chassisAsm.addChild( new DataNode<>( new Part("Cmbr Tube","tube", 3.45, 3 ) ) );
    
   tla = new DataNode<>( new Part("Top Level Assembly", "assembly", 1 ) );
     tla.addChild( new DataNode<>( new Part("Shroud Assembly", "assembly", 1 ) ) );
       tla.get(0).addChild( leftShroudAsm );
       tla.get(0).addChild( rightShroudAsm );
     tla.addChild( chassisAsm );
  
  
  tla.printOperation();
 
  
  rectMode(CENTER);
  
  PFont arial = createFont( "Courier New Bold", 19 );
  textFont(arial);
  
  render = TreeNodeFunctions.renderTree( tla, this )
    .setNodeDims(100,75)
    .setSpacing(10,30)
    .setPosition(580, 100)
    .make()
   ;
}

void draw(){
  background(50);
  strokeWeight(1.5);
  stroke( 200 );
  render.renderLines()
    .renderNodes( (n,p) -> {
      textAlign(CENTER,CENTER);
      fill(200);
        text( n.getData().name, p[0],p[1]-30 );
        text( n.getData().type, p[0],p[1]-10 );
        if( n.getData().cost > 0 ){
          text( "$"+n.getData().cost, p[0],p[1]+10 );
          text( n.getData().count, p[0],p[1]+30 );
        }
        else text( n.getData().count, p[0],p[1]+14 );
     
    });
    
    float textHt = 500;
    textAlign(LEFT,TOP);
    fill(200);
    text("total parts---------> " + tla.getLeafs().stream().mapToInt(n -> n.getData().count ).sum(), 50, textHt );
    text("total subassemblies-> " + (tla.getTotalSize()-tla.getLeafCount()-1), 50, textHt+30 );
    text("total fasteners-----> " + tla.findAll( n -> n.getData().type.equals("fastener")).stream().mapToInt(n -> n.getData().count ).sum(), 50, textHt+60 );
    text("total labels--------> " + tla.findAll( n -> n.getData().type.equals("decal")).size(), 50, textHt+90 );
    text("low level asm count-> " + tla.findAll( n -> n.getMaxDepth() == 1 ).size(), 50, textHt+120 );
    
    text("total cost----------> $" + nf( getTotalCost(tla),0,2), 450, textHt );
    text("chassis asm cost----> $" + nf( getTotalCost(tla.findFirst(n -> n.getData().name.equals("Chassis Asm") ) ),0,2), 450, textHt+30 );
    text("plastics cost-------> $" + nf( (float)tla.findAll( n -> n.getData().type.equals("plastics")).stream().mapToDouble( n -> getTotalCost(n) ).sum(),0,2), 450, textHt+60 );
    text("fastener cost-------> $" + nf( (float)tla.findAll( n -> n.getData().type.equals("fastener")).stream().mapToDouble( n -> getTotalCost(n) ).sum(),0,2), 450, textHt+90 );
    text("non-metal part cost-> $" + nf( (float)tla.findAll( n ->{
      if( !n.isLeaf() ) return false;
      if( n.getData().type.equals("fastener") || n.getData().type.equals("tube") ) return false;
      else return true;
    }).stream().mapToDouble( n -> getTotalCost(n) ).sum(),0,2), 450, textHt+120 );
    
    //save("out.jpg");
}
