package unCore;

import java.util.ArrayList;
import java.util.List;

/*
  - class to contain data shared among all tree nodes
  - idea to optimize leaf iteration:
    - store a int[leafSize()] array of leaf indexes in nodelist
    - instead of recursively getting leafs everytime
    - first getLeaf() call populates that index array
    - if no change to tree, get leafs will just return indexes from that array
    - some kind of internal update() fn? to reabuild leaf list?
*/

public class SingularTreeData<N extends TreeNodeObject<?>> {
    public List<N> nodeList;
    
    
    SingularTreeData(){
      nodeList = new ArrayList<>();
    }
    
    @Override
    public String toString() {
      String out =  "SingularTreeData: size = \n";
      for( TreeNodeObject<?> n : nodeList ) out+= "  - " + n.nodeString() + "\n";
      return out;
    }
    
    
  
  }
