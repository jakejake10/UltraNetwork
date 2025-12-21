
void buildTree1(){
  root = new Node();
  for( int i = 0; i < 3; i++ ) root.addChild();
  PFont font = createFont( "Courier New Bold", 18 );
  textFont(font);
}

void drawTree1(){
  background(17);
  stroke(200);
  noFill();
  
  text( "total nodes:  " + root.getTotalSize(), 50, 30 );
  text( "total leaves: " + root.getLeafCount(), 50, 60 );
  
  if( random(1) < 0.2)
    root.getRandomNode().addChild();
  else{
    root.getLeafs().get(new Random().nextInt(root.getLeafCount())).addChild();
  }
  TreeNodeFunctions.renderTree( root, this )
    .setNodeDims( 5,5 )
    .setSpacing(6,6)
    .setPosition( 500,150 )
    .make()
    .render()
    ;
  //save(frameCount + ".jpg");
}
