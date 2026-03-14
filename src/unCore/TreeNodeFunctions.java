package unCore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

import processing.core.PApplet;
import processing.data.*;
import utTypes.DataNode;

public final class TreeNodeFunctions {
	private TreeNodeFunctions() {}
	
	
	// DATA LIST FUNCTIONS //////////////////////////////////////////////////////////////
	
	public static <N extends TreeNodeObject<N> & Iterable<N>,E> List<E> initDataList( N node, E value ){
		return IntStream.range(0, node.getTotalSize()).mapToObj(i -> value ).collect(Collectors.toList() );
	}

	
	// NEW METHODS ////////////////////////////////////////////////////////////////////////
	
	
	
	
	// TREE BUILDER FUNCTIONS /////////////////////////////////////////////////////////////
	
	/*
	 * builder class created by buildTree() method in TreeNodeObject
	 * takes in node instance in constructor, used for make method
	 * bullder pattern chains methods to cusomize output
	 * make() method creates tree from root and function parameters	 * 
	 */
	
	
	public static class TreeBuilder<N extends TreeNodeObject<N> & Iterable<N>> {
		BiFunction<N,Integer,Integer> childCtModFn = (n,i) -> i;
		BiFunction<N,Integer,Integer> maxDepthModFn = (n,i) -> i;
		Consumer<N> preGenModFn = n -> {};
		Consumer<N> postGenModFn = n -> {};
		BiFunction<N,Integer,Integer> leafCtModFn = (n,i) -> i;
		N root;
		int myChildCt = -1;
		int myMaxDepth = -1;
		int myLeafCt = -1;
		
		
		public TreeBuilder( N root ) {
			this.root = root;
		}
		
		public TreeBuilder<N> setChildCtMod( BiFunction<N,Integer,Integer> fn ) {
			this.childCtModFn = fn;
			return this;
		}
		public TreeBuilder<N> setMaxDepthMod( BiFunction<N,Integer,Integer> fn ) {
			this.maxDepthModFn = fn;
			return this;
		}
		public TreeBuilder<N> setLeafCtMod( BiFunction<N,Integer,Integer> fn ) {
			this.leafCtModFn = fn;
			return this;
		}
		
		public TreeBuilder<N> setPreGenMod( Consumer<N> modFn ) {
			this.preGenModFn = modFn;
			return this;
		}
		public TreeBuilder<N> setPostGenMod( Consumer<N> modFn ) {
			this.postGenModFn = modFn;
			return this;
		}
		
		public TreeBuilder<N> setLeafCt( int leafCt ) {
			this.myLeafCt = leafCt;
			return this;
		}
		public TreeBuilder<N> setChildCt( int childCt ) {
			this.myChildCt = childCt;
			return this;
		}
		public TreeBuilder<N> setMaxDepth( int maxDepth ) {
			this.myMaxDepth = maxDepth;
			return this;
		}
		
		public TreeBuilder<N> setChildCtByDepth( int...depthCts ) {
			this.myMaxDepth = depthCts.length;
			this.myChildCt = depthCts[0];
			setChildCtMod( (n,i) -> {
				int d = n.getDepth();
				if( d < depthCts.length-1 ) return depthCts[d+1];
				return i;
			});
			return this;
		}
		
				
		public void make() {
			if( myLeafCt < 0 && myChildCt < 0 ) throw new UnsupportedOperationException("need to have either leaf or child ct assigned");
			preGenModFn.accept(root);
			make( root, new int[] {myLeafCt,myChildCt,myMaxDepth});
			postGenModFn.accept(root);
		}
		
		public void make( N node, int[] lcd ) {
			if( lcd[2] > 0 && node.getDepth() >= maxDepthModFn.apply(node, lcd[2] )) return;
			int[] cldCts = null;
			// cldCts become leaf counts used for subdivision
			if( lcd[0] > 0 ) cldCts = partitionNumber( leafCtModFn.apply(node, lcd[0]), childCtModFn.apply(node, lcd[1] ) ); 
			else {
				//cldCts become number of children
				cldCts = new int[lcd[1]];
				for( int i = 0; i < cldCts.length; i++ ) cldCts[i] = childCtModFn.apply(node, lcd[1] );
			}
			for( int i : cldCts ) {
				if( i == 0 ) return;
				N child =  node.defaultConstructor();
				preGenModFn.accept(child);
				node.addChild();
				int[] newData = null;
				if( lcd[0]> 0) newData = new int[] {i,lcd[1],lcd[2]};
				else           newData = new int[] {lcd[0],i,lcd[2]};
				if( myLeafCt < 0 || i > 1 ) 
					make( node.getLastChild(), newData );
				postGenModFn.accept(node.getLastChild());
			}
		}
		
	}
	
	
	
	
	
	
	
	// IO FUNCTIONS ////////////////////////////////////////////////////////////////////
	
	// STATIC METHODS TO BE USED WITH NODES //////////////////////////////////////////
	
	public static DataNode<File> buildFromDirectory(String dir) {
		File file = new File(dir);
		DataNode<File> out = new DataNode<>(file);
		buildRecursive(out);
		return out;
	}

	public static void buildRecursive(DataNode<File> node) {
		for (File file : node.getData().listFiles()) {
			node.addChildWithData(file);
			if (file.isDirectory())
				buildRecursive(node.getLastChild()); // Calls same method again.
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// PROCESSING CANVAS RENDERING //////////////////////////////////
	
	public static <E extends TreeNodeObject<E> & Iterable<E>> TreeRenderBuilder<E> renderTree( E node, PApplet pa ) {
		return new TreeRenderBuilder<E>( node, pa );
	}
	
	public static class TreeRenderBuilder<E extends TreeNodeObject<E> & Iterable<E>> {
		PApplet pa;
		
		E root;
		
		float posX, posY;
		
		float defaultNodeHeight = 10;
		float defaultNodeWidth = 10;
//		float nodeHeight = 10;
//		float nodeWidth = 10;
		float verticalSpacing = 15;
		float horizontalSpacing = 10;
		float lineSpacing = 2;
		float lineWeight = 1;

		float next_leaf_x = 0;
		public float[] nodeWidth,nodeHeight;
		public List<float[]> positions;
		
		public TreeRenderBuilder( E root, PApplet pa ){
			this.root = root;
			this.pa = pa;
			nodeWidth = new float[root.getTotalSize()];
			nodeHeight = new float[root.getMaxDepth()+1];
//			for( int i = 0; i < nodeHeight.length; i++ ) nodeHeight[i] = 0;
			setNodeDims(10,10);
		}
		
		public TreeRenderBuilder<E> setNodeDims( float w, float h ) {
			for( int i = 0; i < nodeWidth.length; i++ ) nodeWidth[i] = w;
			for( int i = 0; i < nodeHeight.length; i++ ) nodeHeight[i] = h;
			return this;	
		}
		
		public <M> TreeRenderBuilder<E> setNodeDims( Function<M,float[]> dimFn, List<M> data ) {
			for( E node : root ) {
				float[] dims = dimFn.apply(node.getData(data) );
				nodeWidth[node.getIndex()] = dims[0];
				if( dims[1] > nodeHeight[node.getDepth()] ) // want only tallest node at depth
					nodeHeight[node.getDepth()] = dims[1];
			}
			return this;
		}
		
		public TreeRenderBuilder<E> setNodeHeight( float ht ) {
			for( int i = 0; i < nodeHeight.length; i++ ) nodeHeight[i] = ht;
			return this;
		}
		public TreeRenderBuilder<E> setNodeWidth( float wth ) {
			for( E node : root ) nodeWidth[node.getIndex()] = wth;
			return this;
		}
		
		public TreeRenderBuilder<E> setNodeWidth( List<String> data ) {
			for( E node : root ) {
				nodeWidth[node.getIndex()] = pa.textWidth(node.getData(data));
			}
			return this;
		}

		public TreeRenderBuilder<E> setPosition( float x, float y ) {
			this.posX = x;
			this.posY = y;
			return this;
		}
		
		public TreeRenderBuilder<E> setSpacing( float x, float y ) {
			this.horizontalSpacing = x;
			this.verticalSpacing = y;
			return this;
		}
		
		public TreeRenderBuilder<E> setLineSpacing( float lineSpacing ) {
			this.lineSpacing = lineSpacing;
			return this;
		}

		public TreeRenderBuilder<E> make() {
			getRenderTreePositions(posX,posY);
			return this;
		}
		
//		public void renderTree( float x, float y, Function<E,String> strFn ) {
//			List<String> strs = new ArrayList<>();
//			for( E n : root ) strs.add( strFn.apply(n));
//			List<float[]> nodePos = getRenderTreePositions(root, x, y);
//			if( nodeHeight[0] == 0 ) setNodeHeight( defaultNodeHeight );
////			renderTreeDefault(root, nodePos);
//		}
//		
//		public <M> void renderTree( float x, float y, Function<M,float[]> dimFn, List<M> data) {
//			setNodeDims( dimFn, data );
//			List<float[]> nodePos = getRenderTreePositions(root, x, y);
////			renderTreeDefault(root, nodePos);
//		}
//		
		// render methods /////////////
		
		public TreeRenderBuilder<E> render(){
			renderLines();
			renderNodes();
			return this;
		}
		
		public TreeRenderBuilder<E> renderLines() {
			pa.rectMode(processing.core.PApplet.CENTER);
			for (E n : root) {
				float nx = n.getData(positions)[0];
				float ny = n.getData(positions)[1];

				if (!n.isLeaf()) {
					float cy = n.get(0).getData(positions)[1];
					if (n.getChildCount() == 1)
						pa.line(nx, ny + nodeHeight[n.getDepth()] / 2 + lineSpacing,
								nx, cy - nodeHeight[n.getDepth()] / 2 - lineSpacing);
					else {
						pa.line(nx, ny + nodeHeight[n.getDepth()] / 2 + lineSpacing, 
								nx, ny + (cy - ny) / 2);
						pa.line(n.get(0).getData(positions)[0], ny + (cy - ny) / 2, n.getLastChild().getData(positions)[0],
								ny + (cy - ny) / 2);
						for (E nc : n.getChildren())
							pa.line(nc.getData(positions)[0], ny + (cy - ny) / 2, nc.getData(positions)[0],
									cy - nodeHeight[n.getDepth()] / 2 - lineSpacing);
					}
				}
			}
			return this;
		}
		
		public TreeRenderBuilder<E> renderNodes() {
			for (E n : root) {
				float nx = n.getData(positions)[0];
				float ny = n.getData(positions)[1];
				pa.rect(nx, ny, nodeWidth[n.getIndex()], nodeHeight[n.getDepth()]);
			}
			return this;
		}
		
		public TreeRenderBuilder<E> renderNodes( BiConsumer<E,float[]> drawFn ) {
			for (E n : root) {
				drawFn.accept( n, new float[] {n.getData(positions)[0], n.getData(positions)[1]} );
			}
			return this;
		}
		
		public TreeRenderBuilder<E> renderNodes( BiConsumer<E,float[]> drawFn, int index ) {
			E n = root.getCore().nodeList.get(index);
			drawFn.accept( n, new float[] {n.getData(positions)[0], n.getData(positions)[1]} );
			return this;
		}
		
		public TreeRenderBuilder<E> renderConnectionLine( E node1, E node2 ) {
			E start = null;
			E end = null;
			if( node1.getDepth() > node2.getDepth() ) {
				start = node1;
				end = node2;
			}
			else {
				start = node2;
				end = node1;
			}
			E cur = start;
			while(true) {
				float nx = cur.getData(positions)[0];
				float ny = cur.getData(positions)[1];
				float px = cur.getParent().getData(positions)[0];
				float py = cur.getParent().getData(positions)[1];
				pa.line(nx, ny - nodeHeight[cur.getDepth()] / 2 - lineSpacing, 
						nx, ny - (ny - py) / 2);
				pa.line(nx, ny - (ny - py) / 2, px, ny - (ny - py) / 2);
				pa.line(px, ny - (ny - py) / 2, 
						px, py + nodeHeight[cur.getDepth()] / 2 + lineSpacing );
				cur = cur.getParent();
				if(cur.equals(end)) break;
				if( cur.isRoot()) break;
			}
			return this;
		}
		
		public void renderLineToParent( E node ) {
			if( node.isRoot()) throw new UnsupportedOperationException("cannot render line, root has no parent");
			float nx = node.getData(positions)[0];
			float ny = node.getData(positions)[1];
			float px = node.getParent().getData(positions)[0];
			float py = node.getParent().getData(positions)[1];
			pa.line(nx, ny - nodeHeight[node.getDepth()] / 2 - lineSpacing, 
					nx, ny - (ny - py) / 2);
			pa.line(nx, ny - (ny - py) / 2, px, ny - (ny - py) / 2);
			pa.line(px, ny - (ny - py) / 2, 
					px, py + nodeHeight[node.getDepth()] / 2 + lineSpacing );
		}

		
		public void getRenderTreePositions(float x, float y) {
			positions = root.createDataList(new float[] {-1, -1});
			next_leaf_x = 0;
			
			setLeafPosition(root);
			setNonLeafPosition(root);

			float xOff = x - positions.get(0)[0];
			float yOff = y - positions.get(0)[1];

			for (E n : root) {
				float[] pos = n.getData(positions);
				n.setData(new float[] {pos[0] + xOff, pos[1] + yOff}, positions);
			}

//			return nodePos;
		}

		void setLeafPosition(E node ) {
//			System.out.println("xPos: " + next_leaf_x );
			float xPos = 0;
			float yPos = 0; //node.getDepth() * (nodeHeight[node.getDepth()] + verticalSpacing);
			for( int i = 0; i < node.getDepth(); i++ ) yPos += nodeHeight[node.getDepth()] + verticalSpacing;
			if (node.isLeaf()) {
				xPos = next_leaf_x;
//				System.out.println("nodew: " + nodeWidth[node.getIndex()] );
				next_leaf_x += nodeWidth[node.getIndex()] + horizontalSpacing;
//				next_leaf_x += nodeWidth[node.getIndex()] / 2 + horizontalSpacing
//						+ nodeWidth[node.getIndex()] / 2;
				node.setData(new float[] {xPos+nodeWidth[node.getIndex()]/2, yPos}, positions);
			} else {
				node.setData(new float[] {-1, yPos}, positions);
				for (E nc : node.getChildren())
					setLeafPosition(nc);
			}
		}

		void setNonLeafPosition(E node) {
			if(node.isLeaf()) return; // only encountered if root has no children
			for (E nc : node.getChildren())
				if (nc.getData(positions)[0] == -1)
					setNonLeafPosition(nc);
			float left = node.get(0).getData(positions)[0];
			float right = node.getLastChild().getData(positions)[0];
			float value = (left + right) / 2;
			node.setData(new float[] {value, node.getData(positions)[1]}, positions);
		}
	}

	// CALCULATIONS /////////////////////////////////////////////////
	
	public static int[] partitionNumber( int number, int partCt ) {
		int[] out = new int[partCt];
		for( int i = 0; i < number; i++ ) out[i % out.length]++; 
		return out;
	}
	
	public void makeTree( DataNode<Integer> node, int leafCt, int maxChildCt ) {
		int[] leafCts = partitionNumber( leafCt, maxChildCt );
		for( int i : leafCts )
			if( i == 0 ) return;
			else if( i == 1 ) node.addChild( 0 );
			else {
				node.addChild();
				makeTree( node.getLastChild(), i, maxChildCt );
			}
		}
	
	
	// PREDICATES ///////////////////////////////////////////////
	
	public static <N extends TreeNodeObject<N> & Iterable<N>>
	Predicate<N> withParentInversion(
	        Predicate<N> basePredicate,
	        boolean invertOnOddParent,
	        boolean rootValue
	) {
	    return node -> {
	        if (node == null) return false;
	        if (node.isRoot()) return rootValue;

	        boolean value = basePredicate.test(node);

	        if (!invertOnOddParent) return value;

	        boolean invert = false;
	        N cur = node.getParent();

	        while (cur != null && !cur.isRoot()) {
	            if ( (cur.getIndexInParent() & 1) == 1 )
	                invert = !invert;
	            cur = cur.getParent();
	        }

	        return invert ? !value : value;
	    };
	}


	
}
