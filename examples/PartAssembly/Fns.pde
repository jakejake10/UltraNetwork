
float getTotalCost( DataNode<Part> node ){
    if( !node.isLeaf() ) 
      return (float) node.getChildren().stream()
        .mapToDouble( n -> (double)getTotalCost(n) )
        .sum();
    else return node.getData().cost * node.getData().count;
  }
  
  
