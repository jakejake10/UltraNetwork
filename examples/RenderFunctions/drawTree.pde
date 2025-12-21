Node root1,root2,cur1,cur2,cur3;
//TreeNodeObject.BFSTraversal<Node> bf;
//TreeNodeObject.DFSTraversal<Node> df;
TreeNodeFunctions.TreeRenderBuilder<Node> render1,render2;

int green = color(52, 235, 171);
int orange = color(255, 144, 33);

void buildTree(){
  root1 = new Node();
  for( int i = 0; i < 3; i++ ){
    for( Node nl : root1.getLeafs() ){
      nl.addChild();
      nl.addChild();
    }
  }
   
  root2 = root1.copyNodeSubtree();
    
  //bf = new TreeNodeObject.BFSTraversal<>( bfRoot );
  //df = new TreeNodeObject.DFSTraversal<>( dfRoot );
   
  cur1 = root1;
  cur2 = root2.get(0).get(0).get(0);
   
  
  
  render1 = TreeNodeFunctions.renderTree( root1, this )
    .setSpacing(35,65)
    .setPosition( 250,120 )
    .setNodeDims(15,15)
    .setLineSpacing( 6 )
    .make()
    //.render();
    
    ;
   render2 = TreeNodeFunctions.renderTree( root2, this )
    .setSpacing(35,65)
    .setPosition( 750,120 )
    .setNodeDims(15,15)
    .setLineSpacing( 6 )
    .make()
    //.render();
    //.renderLines()
    //.renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) )
    ;
   
   strokeCap(SQUARE);
   //strokeWeight(4);
   
   
}

void drawTree(){
  background(17);
  fill(200);
  stroke(120);
  textAlign(CENTER,CENTER);
  textFont(createFont( "Courier New Bold", 18 ));
  text( "Leaves of a Node", 250,50 );
  text( "Lowest Common Ancestor", 750,50 );
  text( "of 2 Nodes", 750,80 );
  fill(120);
  
  switch(frameCount){
    case 1:
      cur3 = root2.get(0).get(0).get(1);
      break;
    case 2:
      cur3 = root2.get(0).get(1).get(0);
      break;
    case 3:
      cur3 = root2.get(1).get(1).get(1);
      break;
  }
  
  Node lca = TreeNodeObject.getLowestCommonAncestor( cur2,cur3 );
  strokeWeight(1);
  render1.renderLines()
    .renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) );
  render2.renderLines()
    .renderNodes( (n,p)-> rect(p[0],p[1],10,10 ) );
  
  fill(green);
  stroke(green);
   
  render1.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), cur1.getIndex() );
  render2.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), cur2.getIndex() );
  render2.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), cur3.getIndex() );
  
  fill(orange);
  stroke(orange);
  for( Node nl : cur1.getLeafs() )
    render1.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), nl.getIndex() );
  render2.renderNodes( (n,p) -> rect( p[0],p[1], 15,15 ), lca.getIndex() );
  strokeWeight(2);
  renderLineFromTo( cur2, lca, render2 );
  renderLineFromTo( cur3, lca, render2 );
  //if( !bfCur.isRoot() ) renderBF.renderLineToParent( bfCur );
  //if( !dfCur.isRoot() ) renderDF.renderLineToParent( dfCur );
  
  save(frameCount + ".jpg" );
  
  //if( bf.hasNext() ){
  //  bfCur = bf.next();
  //  dfCur = df.next();
  //}
  //else noLoop();
  
  cur1 = cur1.get(0);
  
}

void renderLineFromTo( Node from, Node to, TreeNodeFunctions.TreeRenderBuilder<Node> r ){
  r.renderLineToParent( from );
  if( from.getParent().equals( to ) ) return;
  r.renderNodes( (n,p) -> line( p[0],p[1]-20, p[0],p[1]+20 ), from.getParent().getIndex() );
  renderLineFromTo( from.getParent(), to, r );
}
