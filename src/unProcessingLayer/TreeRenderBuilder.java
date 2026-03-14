package unProcessingLayer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import processing.core.PApplet;
import unCore.TreeNodeObject;

public class TreeRenderBuilder<E extends TreeNodeObject<E> & Iterable<E>> {
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
	public float[] nodeWidth, nodeHeight;
	public List<float[]> positions;
	
	
	
	public static <E extends TreeNodeObject<E> & Iterable<E>> TreeRenderBuilder<E> renderTree( E node, PApplet pa ) {
		return new TreeRenderBuilder<E>( node, pa );
	}
	

	public TreeRenderBuilder(E root, PApplet pa) {
		this.root = root;
		this.pa = pa;
		nodeWidth = new float[root.getTotalSize()];
		nodeHeight = new float[root.getMaxDepth() + 1];
//			for( int i = 0; i < nodeHeight.length; i++ ) nodeHeight[i] = 0;
		setNodeDims(10, 10);
	}

	public TreeRenderBuilder<E> setNodeDims(float w, float h) {
		for (int i = 0; i < nodeWidth.length; i++)
			nodeWidth[i] = w;
		for (int i = 0; i < nodeHeight.length; i++)
			nodeHeight[i] = h;
		return this;
	}

	public <M> TreeRenderBuilder<E> setNodeDims(Function<M, float[]> dimFn, List<M> data) {
		for (E node : root) {
			float[] dims = dimFn.apply(node.getData(data));
			nodeWidth[node.getIndex()] = dims[0];
			if (dims[1] > nodeHeight[node.getDepth()]) // want only tallest node at depth
				nodeHeight[node.getDepth()] = dims[1];
		}
		return this;
	}

	public TreeRenderBuilder<E> setNodeHeight(float ht) {
		for (int i = 0; i < nodeHeight.length; i++)
			nodeHeight[i] = ht;
		return this;
	}

	public TreeRenderBuilder<E> setNodeWidth(float wth) {
		for (E node : root)
			nodeWidth[node.getIndex()] = wth;
		return this;
	}

	public TreeRenderBuilder<E> setNodeWidth(List<String> data) {
		for (E node : root) {
			nodeWidth[node.getIndex()] = pa.textWidth(node.getData(data));
		}
		return this;
	}

	public TreeRenderBuilder<E> setPosition(float x, float y) {
		this.posX = x;
		this.posY = y;
		return this;
	}

	public TreeRenderBuilder<E> setSpacing(float x, float y) {
		this.horizontalSpacing = x;
		this.verticalSpacing = y;
		return this;
	}

	public TreeRenderBuilder<E> setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
		return this;
	}

	public TreeRenderBuilder<E> make() {
		getRenderTreePositions(posX, posY);
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

	public TreeRenderBuilder<E> render() {
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
					pa.line(nx, ny + nodeHeight[n.getDepth()] / 2 + lineSpacing, nx,
							cy - nodeHeight[n.getDepth()] / 2 - lineSpacing);
				else {
					pa.line(nx, ny + nodeHeight[n.getDepth()] / 2 + lineSpacing, nx, ny + (cy - ny) / 2);
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

	public TreeRenderBuilder<E> renderNodes(BiConsumer<E, float[]> drawFn) {
		for (E n : root) {
			drawFn.accept(n, new float[] { n.getData(positions)[0], n.getData(positions)[1] });
		}
		return this;
	}

	public TreeRenderBuilder<E> renderNodes(BiConsumer<E, float[]> drawFn, int index) {
		E n = root.getCore().nodeList.get(index);
		drawFn.accept(n, new float[] { n.getData(positions)[0], n.getData(positions)[1] });
		return this;
	}

	public TreeRenderBuilder<E> renderConnectionLine(E node1, E node2) {
		E start = null;
		E end = null;
		if (node1.getDepth() > node2.getDepth()) {
			start = node1;
			end = node2;
		} else {
			start = node2;
			end = node1;
		}
		E cur = start;
		while (true) {
			float nx = cur.getData(positions)[0];
			float ny = cur.getData(positions)[1];
			float px = cur.getParent().getData(positions)[0];
			float py = cur.getParent().getData(positions)[1];
			pa.line(nx, ny - nodeHeight[cur.getDepth()] / 2 - lineSpacing, nx, ny - (ny - py) / 2);
			pa.line(nx, ny - (ny - py) / 2, px, ny - (ny - py) / 2);
			pa.line(px, ny - (ny - py) / 2, px, py + nodeHeight[cur.getDepth()] / 2 + lineSpacing);
			cur = cur.getParent();
			if (cur.equals(end))
				break;
			if (cur.isRoot())
				break;
		}
		return this;
	}

	public void renderLineToParent(E node) {
		if (node.isRoot())
			throw new UnsupportedOperationException("cannot render line, root has no parent");
		float nx = node.getData(positions)[0];
		float ny = node.getData(positions)[1];
		float px = node.getParent().getData(positions)[0];
		float py = node.getParent().getData(positions)[1];
		pa.line(nx, ny - nodeHeight[node.getDepth()] / 2 - lineSpacing, nx, ny - (ny - py) / 2);
		pa.line(nx, ny - (ny - py) / 2, px, ny - (ny - py) / 2);
		pa.line(px, ny - (ny - py) / 2, px, py + nodeHeight[node.getDepth()] / 2 + lineSpacing);
	}

	public void getRenderTreePositions(float x, float y) {
		positions = root.createDataList(new float[] { -1, -1 });
		next_leaf_x = 0;

		setLeafPosition(root);
		setNonLeafPosition(root);

		float xOff = x - positions.get(0)[0];
		float yOff = y - positions.get(0)[1];

		for (E n : root) {
			float[] pos = n.getData(positions);
			n.setData(new float[] { pos[0] + xOff, pos[1] + yOff }, positions);
		}

//			return nodePos;
	}

	void setLeafPosition(E node) {
//			System.out.println("xPos: " + next_leaf_x );
		float xPos = 0;
		float yPos = 0; // node.getDepth() * (nodeHeight[node.getDepth()] + verticalSpacing);
		for (int i = 0; i < node.getDepth(); i++)
			yPos += nodeHeight[node.getDepth()] + verticalSpacing;
		if (node.isLeaf()) {
			xPos = next_leaf_x;
//				System.out.println("nodew: " + nodeWidth[node.getIndex()] );
			next_leaf_x += nodeWidth[node.getIndex()] + horizontalSpacing;
//				next_leaf_x += nodeWidth[node.getIndex()] / 2 + horizontalSpacing
//						+ nodeWidth[node.getIndex()] / 2;
			node.setData(new float[] { xPos + nodeWidth[node.getIndex()] / 2, yPos }, positions);
		} else {
			node.setData(new float[] { -1, yPos }, positions);
			for (E nc : node.getChildren())
				setLeafPosition(nc);
		}
	}

	void setNonLeafPosition(E node) {
		if (node.isLeaf())
			return; // only encountered if root has no children
		for (E nc : node.getChildren())
			if (nc.getData(positions)[0] == -1)
				setNonLeafPosition(nc);
		float left = node.get(0).getData(positions)[0];
		float right = node.getLastChild().getData(positions)[0];
		float value = (left + right) / 2;
		node.setData(new float[] { value, node.getData(positions)[1] }, positions);
	}
}
