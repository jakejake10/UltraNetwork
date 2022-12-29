package utCore;

import java.util.List;
import java.util.function.*;

import pFns_baseObjects.Boundary;



public class TreeFns<T extends AbstractTree<T,N>,N extends AbstractNode<T,N>> {
	public BiConsumer<N,?> makeBiConsumer( Consumer<N> input ){
	    BiConsumer<N,?> out = (c,d) -> input.accept(c);
	    return out;
	  }
	  
	  // NODE/DATA FNS /////////////////////////////////////////////////////
	  //////////////////////////////////////////////////////////////////////
	  
	public BiFunction<N,Void,Integer> getLeafCount = (n,v) -> {
	    if( !n.hasChildren() ) return 1;
	      int leafs = 0;
	      for( N nChild : n ) leafs += this.getLeafCount.apply( nChild, null );
	      return leafs;
	  };
	  
	  
	  
	  
	  
	  // NODE/DATA CONSUMERS ///////////////////////////////////////////////////////
	  //////////////////////////////////////////////////////////////////////////////
	  
	  // 
	  
	  
	  
	  // NODE ARRAY ////////////////////////////////////////////////////////////////
	  
	  public BiConsumer<N,List<N>> getLeafs = (n,d) -> {
	    if( !n.hasChildren() ){
	      d.add( n );
	      return;
	    }
	    for( N nChild : n ) this.getLeafs.accept( nChild, d );
	  };
	  
	  
	  // Integer Array /////////////////////////////////////////////////////////////
	  
	  public BiConsumer<N,Integer[]> makeLeafs = (n,d) ->{        // [count,maxChildren]
	    int count = d[0];
	    int maxChildren = d[1];
	    if( count == 1 ) return;
	    int[] childGroups =  splitValue( count, maxChildren );
	    for( int ct : childGroups ){
	      if( ct > 0 ) {
	        n.addChild();
	        this.makeLeafs.accept( n.lastChild(), new Integer[]{ ct, maxChildren } );
	      }
	    }
	  };
	  
	  public int[] splitValue( int inputSize, int bucketCt ){
	    int[] out = new int[bucketCt];
	    int baseVal = inputSize / bucketCt;
	    int remainder = inputSize - baseVal * bucketCt;
	    for( int i = 0; i < out.length; i++ ) out[i] = baseVal;
	    if( remainder > 0 ){
	      for( int i = 0; i < remainder; i++ ) out[i]++;
	    }
	    return out;
	  }
	  
	  // predicate //////////////
	  
//	  Predicate<Node> hasChildren = n -> n.hasChildren();
	 
	  
}
