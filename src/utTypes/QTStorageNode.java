package utTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import processing.core.PApplet;
import processing.core.PVector;
import unCore.TreeNodeStruct;

/*
 * treenode implementation of a quadtree for spatial indexing and storing points
 */

public class QTStorageNode extends TreeNodeStruct<QTStorageNode,Integer> {
	public float x,y,w,h;

	
	public QTStorageNode( float x, float y, float w, float h ) {
		super();
		this.x = x; this.y = y; this.w = w; this.h = h;
		setData( -1 );
	}
	
	
	// Treenode methods ///////////////////////////////////
	
	@Override
	public QTStorageNode getInstance() {
		return this;
	}

	// Default constructor should not be used in this subclass
	@Override
	public QTStorageNode defaultConstructor() {
//		return null;
		return new QTStorageNode( 0,0,0,0 );
	}

	@Override
	public Iterator<QTStorageNode> iterator() {
		return null;
	}
	
	// SPLIT METHODS //////////////////////////////////////
	
	public void splitQuads() {
		addChild( new QTStorageNode( x,    y,    w/2, h/2 ) );
		addChild( new QTStorageNode( cx(), y,    w/2, h/2 ) );
		addChild( new QTStorageNode( x,    cy(), w/2, h/2 ) );
		addChild( new QTStorageNode( cx(), cy(), w/2, h/2 ) );
	}
	
	public <E extends PVector> List<List<E>> storeObjs ( List<E> objsIn, int maxCapacity ){
		resetTree();	// needed for updating moving particle systems
		List<List<E>> out = new ArrayList<>();
		recursiveSplitPts(  objsIn, maxCapacity, out );
		return out;
	}
	
	private <E extends PVector> void recursiveSplitPts( List<E> objsIn, int maxCapacity, List<List<E>> storage ) {
		if( objsIn.size() > maxCapacity ) {
			splitQuads();
			List<List<E>> quadElems = new ArrayList<>();
			quadElems.add(null);
			quadElems.add(null);
			quadElems.add(null);
			quadElems.add(null);
			for( int i = 0; i < objsIn.size(); i++ ) {
				int quadIndex = getQuad( objsIn.get(i).x, objsIn.get(i).y );
				if( quadElems.get(quadIndex) == null ) quadElems.set(quadIndex, new ArrayList<>() );
				quadElems.get(quadIndex).add(objsIn.get(i));
			}
			for( int i = 0; i < 4; i++ ) {
				if( quadElems.get(i) != null ) 
					get(i).recursiveSplitPts( quadElems.get(i), maxCapacity,  storage ); 
			}
		}
		else {
			storage.add(objsIn);
			setData( storage.size() - 1 );
		}
	}
	
	
	// GETTERS /////////////////////////////////////////////////////
	
	public int quadToRow( int quadIn ) {
		return (int)Math.floor(quadIn/2);
	}
	public int quadToCol( int quadIn ) {
		return quadIn % 2;
	}
	
	public float cx() {
		return x+w/2;
	}
	public float cy() {
		return y+h/2;
	}
	
	
	public float getW( float rootW ) {
		return depth == 0 ? rootW : rootW / (float)Math.pow(2, depth);
	}
	public float getH( float rootH ) {
		return depth == 0 ? rootH : rootH / (float)Math.pow(2, depth);
	}
	
	public int getQuad( float x, float y ) {
		if(      x < cx() && y < cy() ) return 0;
		else if( x > cx() && y < cy() ) return 1;
		else if( x < cx() && y > cy() ) return 2;
		else return 3;
	}
	
	public QTStorageNode getXY( float x, float y ) {
		if( !hasParent() && !contains( x,y, this.x,this.y,this.w,this.h ) ) return null;
		return childSelectionOperation( n -> n.hasChildren(), n -> n.getQuad(x, y));
	}
	
	public <E extends PVector> List<E> getPts( float x, float y, List<List<E>> ptList ){
		int dataIndex = getXY( x, y ).getData();
		return dataIndex != -1 ? ptList.get( dataIndex ) : new ArrayList<>(); 
	}
	
	public <E extends PVector> List<E> getPts( float x, float y, float radius, List<List<E>> ptList ){
		List<E> out = new ArrayList<>();
		List<Integer> dataIndexes = new ArrayList<>();
		getOverlapRecursive( x-radius,y-radius,radius*2,radius*2, dataIndexes );
		for( int i : dataIndexes ) {
			if( i != -1 ) out.addAll( ptList.get(i) );
		}
		return out; 
	}
	
	
	// UTILITY FNS ////////////////////////////////////////////////
	
	private void resetTree() {
		getRoot().removeChildren();
	}
	public List<PVector> generateRandomPVs( PApplet pa, int number ){
		return IntStream.range( 0,number).mapToObj( i -> new PVector( (float)Math.random()*pa.width, (float)Math.random()*pa.height) )
				.collect( Collectors.toList() );
	}
	
	public <E extends PVector> List<E> generateRandomPVObjs( PApplet pa, int number, Function<PVector,E> convert ){
		return IntStream.range( 0,number).mapToObj( i -> new PVector( (float)Math.random()*pa.width, (float)Math.random()*pa.height) )
				.map( pv -> convert.apply(pv) )
				.collect( Collectors.toList() );
	}
	
	public static boolean contains( float xIn, float yIn, float rx, float ry, float rw, float rh ) {
		return xIn > rx && xIn < rx + rw && yIn > ry && yIn < ry + rh;
	}
	
	public static boolean overlaps(float r1x, float r1y, float r1w, float r1h, float r2x, float r2y, float r2w,
			float r2h) {
		return r1x < r2x + r2w && r1x + r1w > r2x && r1y < r2y + r2h && r1y + r1h > r2y;
	}
	
	private void getOverlapRecursive( float x, float y, float w, float h, List<Integer> foundLeafs ){
		if( !hasChildren() ) foundLeafs.add( getInstance().getData() );
		else for( QTStorageNode qc : getChildren() ) 
			if( overlaps( x,y,w,h, qc.x,qc.y,qc.w,qc.h ) ) qc.getOverlapRecursive( x,y,w,h, foundLeafs );
	}
	
	// DISPLAY FNS ////////////////////////////////////////////////
	
	public void display(PApplet pa) {
		for( QTStorageNode ql : getLeafs() ) pa.rect( ql.x, ql.y, ql.w, ql.h );
	}
		
	
}
