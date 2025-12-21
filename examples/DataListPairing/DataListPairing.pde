import unCore.*;
import utTypes.*;
import java.util.List;

Node root;
List<String> nodeNames;

void setup(){
  root = new Node();
    root.addChild();
      root.get(0).addChild();
    root.addChild();
  
  nodeNames = root.initDataList("");
  for( Node n : root ) n.setData("node: ",nodeNames);
  for( Node n : root ) n.modifyData( s -> s + " depth=" + n.getDepth()+", ", nodeNames );
  for( Node n : root ) if( n.hasChildren() ) n.modifyData( s -> s + "childCount=" + n.getChildCount(), nodeNames );
  for( Node n : root ) if( n.isLeaf() ) n.modifyData( s -> s + "leaf", nodeNames );
  
  
  root.printOperation( n -> n.getData(nodeNames) );
  
}


void draw(){}
