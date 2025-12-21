Node bfRoot,dfRoot,bfCur,dfCur;
TreeNodeObject.BFSTraversal<Node> bf;
TreeNodeObject.DFSTraversal<Node> df;
TreeNodeFunctions.TreeRenderBuilder<Node> renderBF,renderDF;

void buildTree(){
  bfRoot = new Node();
      bfRoot.addChild(new Node());
        bfRoot.get(0).addChild(new Node());
          bfRoot.get(0).get(0).addChild(new Node());
          bfRoot.get(0).get(0).addChild(new Node());
        bfRoot.get(0).addChild(new Node());
          bfRoot.get(0).get(1).addChild(new Node());
          bfRoot.get(0).get(1).addChild(new Node());
      bfRoot.addChild(new Node());
        bfRoot.get(1).addChild(new Node());
          bfRoot.get(1).get(0).addChild(new Node());
          bfRoot.get(1).get(0).addChild(new Node());
        bfRoot.get(1).addChild(new Node());
          bfRoot.get(1).get(1).addChild(new Node());
          bfRoot.get(1).get(1).addChild(new Node());
   
  dfRoot = bfRoot.copyNodeSubtree();
    
  bf = new TreeNodeObject.BFSTraversal<>( bfRoot );
  df = new TreeNodeObject.DFSTraversal<>( dfRoot );
   
  bfCur = bf.next();
  dfCur = df.next();
   
  background(17);
  fill(200);
  stroke(120);
  textAlign(CENTER,CENTER);
  textFont(createFont( "Courier New Bold", 18 ));
  text( "Breadth First Traversal", 250,50 );
  text( "Depth First Traversal", 750,50 );
  fill(120);
  
  renderBF = TreeNodeFunctions.renderTree( bfRoot, this )
    .setSpacing(35,65)
    .setPosition( 250,120 )
    .setNodeDims(15,15)
    .setLineSpacing( 6 )
    .make()
    //.render();
    .renderLines()
    .renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) )
    ;
   renderDF = TreeNodeFunctions.renderTree( dfRoot, this )
    .setSpacing(35,65)
    .setPosition( 750,120 )
    .setNodeDims(15,15)
    .setLineSpacing( 6 )
    .make()
    //.render();
    .renderLines()
    .renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) )
    ;
   fill(52, 235, 171);
   stroke(52, 235, 171);
   strokeCap(SQUARE);
   strokeWeight(4);
}

void drawTree(){
  strokeWeight(1);
  renderBF.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), bfCur.getIndex() );
  renderDF.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), dfCur.getIndex() );
  strokeWeight(2);
  if( !bfCur.isRoot() ) renderBF.renderLineToParent( bfCur );
  if( !dfCur.isRoot() ) renderDF.renderLineToParent( dfCur );
  
  //save(frameCount + ".jpg" );
  
  if( bf.hasNext() ){
    bfCur = bf.next();
    dfCur = df.next();
  }
  else noLoop();
  
  
  
}
