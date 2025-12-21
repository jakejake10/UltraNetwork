
TreeNodeFunctions.TreeRenderBuilder<Node> render;
Function<Node,Object> nodeInfo = null;
String[] names = new String[]{
  "index", "total size", "child count", "depth", "location code" };


void buildTree(){
  root = new Node();
  for( int i = 0; i < 3; i++ ){
    for( Node nl : root.getLeafs() ){
      nl.addChild();
      nl.addChild();
    }
  }  
  
  render = TreeNodeFunctions.renderTree( root, this )
    .setSpacing(40,80)
    .setPosition( 500,150 )
    .setNodeDims(30,30)
    .setLineSpacing( 10 )
    .make()
    //.render();
    ;
  
   strokeCap(SQUARE);
   strokeWeight(2);
}



void drawTree(){
  background(17);
  fill(200);
  stroke(200);
  textAlign(CENTER,CENTER);
  textFont(createFont( "Courier New Bold", 32 ));
  text( names[frameCount-1], 500,50 );
  fill(200);
  
  switch(frameCount){
    case 1:
      nodeInfo = n -> n.getIndex();
      break;
    case 2:
      nodeInfo = n -> n.getSize();
      break;
    case 3:
      nodeInfo = n -> n.getChildCount();
      break;
    case 4:
      nodeInfo = n -> n.getDepth();
      break;
    case 5:
      nodeInfo = n -> n.isRoot() ? "(root)" : n.getLocationCodeFromRoot();
      break;
  }
  
  render.renderLines()
    .renderNodes( (n,p)-> text( nodeInfo.apply(n).toString(), p[0],p[1] ) );
  
  //save(frameCount + ".jpg" );
 
  
}
