DataNode<String> root2,root3;
List<String> names = new ArrayList<>();
TreeNodeObject.BFSTraversal<DataNode<String>> tr; 

void buildTree2(){
  root2 = new DataNode<>( "animals" );
    root2.addChild(new DataNode<>("vertibrate"));
      root2.get(0).addChild(new DataNode<>("amphibians"));
      root2.get(0).addChild(new DataNode<>("birds"));
      root2.get(0).addChild(new DataNode<>("fish"));
        root2.get(0).get(2).addChild( new DataNode<>("bony") );
          root2.get(0).get(2).get(0).addChild( new DataNode<>("smelt") );
    root2.addChild(new DataNode<>("invertibrate"));
      root2.get(1).addChild(new DataNode<>("insects"));
      root2.get(1).addChild(new DataNode<>("echinoderms"));
      
   tr = new TreeNodeObject.BFSTraversal<>( root2 );
   tr.next();
   root3 = root2.copyNode(false);
    
   
   
   
   textFont(font);
}

void drawTree2(){
  background(17);
  
  names = root3.getCore().nodeList.stream()
   .map( n -> n.getData() )
   .collect(Collectors.toList());
  
  TreeNodeFunctions.TreeRenderBuilder<DataNode<String>> render = 
  TreeNodeFunctions.renderTree( root3, this )
    .setNodeWidth( names )
    .setNodeHeight( 20 )
    .setSpacing(15,55)
    .setPosition( 500,150 )
    .make()
    ;
    
  textAlign(CENTER,CENTER);
  fill(200);
  stroke(200);
  render.renderLines();
  noStroke();
  render.renderNodes( (n,p) -> text( n.getData(), p[0],p[1] ) );
  
  if( tr.hasNext() ){
    DataNode<String> cur = tr.next();
    DataNode<String> curPar = cur.getParent();
    root3.getFromLocationCode( curPar.getLocationCodeFromRoot() ).addChild( cur.copyNode(false) );
  }
  
  //save(frameCount + ".jpg" );
}
