import unCore.*;
import utTypes.*;

import java.util.Iterator;

ColorNode root,root2;
TreeNodeFunctions.TreeRenderBuilder<ColorNode> render;


void setup(){
  size( 500, 500 );
  frameRate(4);
  
  root = new ColorNode( "palettes" );
    root.addChild( new ColorNode( "warm" ) );
      root.get(0).addChild( new ColorNode( "warm1" ) );
        root.get(0).get(0).addChild( new ColorNode( "yellow", color( 255,255,0 ) ) );
        root.get(0).get(0).addChild( new ColorNode( "red", color( 255,0,0 ) ) );
      root.get(0).addChild( new ColorNode( "warm2" ) );
        root.get(0).get(1).addChild( new ColorNode( "lightyellow", color( 255,255,160 ) ) );
        root.get(0).get(1).addChild( new ColorNode( "salmon", color( 255,100,100 ) ) );
        root.get(0).get(1).addChild( new ColorNode( "olive", color( 120,120,10 ) ) );
    root.addChild( new ColorNode( "cool" ) );
      root.get(1).addChild( new ColorNode( "cyan", color( 0,255,255 ) ) );
      root.get(1).addChild( new ColorNode( "blue", color( 0,0,255 ) ) );
      root.get(1).addChild( new ColorNode( "purple", color( 140,0,255 ) ) );
    
    
    
  root.makeJSON( this )
      .addField("name", n -> n.myName )
      .addConditionField("color", n -> n.isLeaf(), n -> n.myColor )
      .export( "out.json");
      
  ColorNode root2 = TreeNodeFunctions.buildFromJSON( this, "import.json", ()-> new ColorNode(), (n,j) -> {
    n.myName = j.getString("name");
    if( n.isLeaf() ) n.myColor = j.getInt("color");
    
  });
    //(n,j) -> new ColorNode(j.getString("myName")));
  
  println("color palette:" );
  root.printOperation();
  println();
  println("imported color palette:" );
  root2.printOperation();
  
  rectMode(CENTER);
  
  PFont arial = createFont( "Courier New Bold", 14 );
  textFont(arial);
  render = TreeNodeFunctions.renderTree( root, this )
    .setNodeDims(20,20)
    .setSpacing(20,30)
    .setPosition(250, 60)
    .make()
   ;
}

void draw(){
  background(50);
  strokeWeight(1.5);
  stroke( 200 );
  render.renderLines()
    .renderNodes( (n,p) -> {
      if( n.isLeaf() ){
        fill( n.myColor );
        noStroke();
        rect(p[0],p[1]+4,20,20);
      }
      else{
        fill(200);
        textAlign(CENTER,CENTER);
        text( n.myName, p[0],p[1] );
      }
    });
    
    textAlign(LEFT,TOP);
    
    fill(200);
    text("random color--------->", 50, 300 );
    fill( root.getRandomLeaf().myColor );
    rect( 250, 305, 20, 20 );
    
    fill(200);
    text("random warm color---->", 50, 330 );
    fill( root.get(0).getRandomLeaf().myColor );
    rect( 250, 335, 20, 20 );
    
    fill(200);
    text("random \"warm2\" color->", 50, 360 );
    fill( root.get(0).get(1).getRandomLeaf().myColor );
    rect( 250, 365, 20, 20 );
    
    fill(200);
    text("random cool color---->", 50, 390 );
    fill( root.get(1).getRandomLeaf().myColor );
    rect( 250, 395, 20, 20 );
    
}
