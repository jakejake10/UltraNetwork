Node r31,r32,r33,r34;
TreeNodeObject.BFSTraversal<Node> bf;
TreeNodeObject.DFSTraversal<Node> df;
TreeNodeFunctions.TreeRenderBuilder<Node> renderBF,renderDF;
//500x500

void buildTree3(){
  r31 = new Node();
      r31.addChild(new Node());
        r31.get(0).addChild(new Node());
          r31.get(0).get(0).addChild(new Node());
          r31.get(0).get(0).addChild(new Node());
        r31.get(0).addChild(new Node());
          r31.get(0).get(1).addChild(new Node());
          r31.get(0).get(1).addChild(new Node());
      r31.addChild(new Node());
        r31.get(1).addChild(new Node());
          r31.get(1).get(0).addChild(new Node());
          r31.get(1).get(0).addChild(new Node());
        r31.get(1).addChild(new Node());
          r31.get(1).get(1).addChild(new Node());
          r31.get(1).get(1).addChild(new Node());
   
  r33 = r31.copyNodeSubtree();
    
  bf = new TreeNodeObject.BFSTraversal<>( r31 );
  df = new TreeNodeObject.DFSTraversal<>( r33 );
   
  r32 = bf.next();
  r34 = df.next();
  //root3 = root2.copyNode(false);
   
  background(17);
  fill(200);
  stroke(120);
  textAlign(CENTER,CENTER);
  textFont(createFont( "Courier New Bold", 18 ));
  text( "Breadth First Traversal", 250,50 );
  text( "Depth First Traversal", 750,50 );
  fill(120);
  
  renderBF = TreeNodeFunctions.renderTree( r31, this )
    .setSpacing(35,65)
    .setPosition( 250,120 )
    .setNodeDims(15,15)
    .setLineSpacing( 6 )
    .make()
    //.render();
    .renderLines()
    .renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) )
    ;
   renderDF = TreeNodeFunctions.renderTree( r33, this )
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

void drawTree3(){
  strokeWeight(1);
  renderBF.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), r32.getIndex() );
  renderDF.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), r34.getIndex() );
  strokeWeight(2);
  if( !r32.isRoot() ) renderBF.renderLineToParent( r32 );
  if( !r34.isRoot() ) renderDF.renderLineToParent( r34 );
  
  //save(frameCount + ".jpg" );
  
  if( bf.hasNext() ){
    r32 = bf.next();
    r34 = df.next();
  }
  else noLoop();
  
  
  
}
