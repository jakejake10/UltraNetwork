package utTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.*;

import pFns_baseObjects.DisplayOperation;
import pFns_general.PFns;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import ugCore.GridFunctions.PolarDisplay;
import ugCore.GridFunctions.QuadSample;
import usPrimitives.*;
import unCore.*;
import uvCore.*;


public class Grid extends TreeNodeStruct<Grid,Void> implements Iterable<Grid>, Rectangular<Grid> {
	float x, y, w, h;
	float ar;
	String type = "";
	public CRObject cr;
	public RangeObject colXs, rowYs; // null if regular grid, addtl value for right/bottom edge?
	public static String space = "CARTESIAN"; // POLAR
	

	//NODE ABSTRACT METHODS ////////////////////////////////////////////////
		public Grid( int...init ){	// root constructor
			super( init );
		}
		public Grid( Grid input ){	// root constructor
			super( input );
		}

		// NodeObjInterface
		@Override
		public Grid getInstance() {
			return this;
		}
		

		@Override
		public Grid defaultConstructor( int...init ) {
			return new Grid( init );
		}
		@Override
		public Grid defaultConstructor( Grid input ) {
			return new Grid( input );
		}
		@Override
		public Iterator<Grid> iterator() {
			return nodeIterator();
		}
		
	// NON MULTIGRID CONSTRUCTORS /////////////////////////////////////////
	
	public Grid( PApplet pa, int colsIn, int rowsIn ) {
		cr = new CRObject( 1, 1 );
		colXs = new RangeObject( 2 );
		rowYs = new RangeObject( 2 );
//		divide().setCR( colsIn, rowsIn );
		setCols( colsIn );
		setRows( rowsIn );
		positionGrid( pa );
	}
	
	// MULTI / NON MULTI CONSTRUCTORS /////////////////////////////////////
	
	public Grid() {	// multigrid constructor
		cr = new CRObject( 1, 1 );
		colXs = new RangeObject( 2 );
		rowYs = new RangeObject( 2 );
	}
	
	public Grid( PApplet pa ) {	// multigrid constructor
		this();
//		positionGrid( 0, 0, pa.width, pa.height );
		Rectangular.position(this, pa);
	}
	
//	
//	public Grid divideFn( Consumer<Grid> divideFn ) {
//		divideFn.accept(this );
//		return this;
//	}
	
	public Grid toMulti() {
		if( !inMultiGrid() ) {
			for( int i : cr ) {
				addChild();
				get(i).positionInParent();
			}			
		}
		return this;
	}
	
	
	// COL / ROW SETTERS /////////////////////////////////////////////////////
	
	/**
     * specify row / col number
     * other fns moved to divider class
     */
	
	public Grid setRows( int rowsIn ) {
		cr.setRows(rowsIn);
		rowYs = new RangeObject( rowsIn + 1 ).setRange(rowYs);
		return this;
	}
		
	public Grid setCols( int colsIn ) {
		cr.setCols(colsIn);
		colXs = new RangeObject( colsIn + 1 ).setRange(colXs);
		return this;
	}
	
	public Grid flipCR( boolean flipChildren ) {
		int savedRows = cr.rows();
		RangeObject savedRowYs = rowYs.copy();
		cr.setRows( cr.cols());
		cr.setCols( savedRows );
		rowYs = colXs.copy().setRange(y(),w()+h());
		colXs = savedRowYs.setRange(x(),x()+w());
		if( hasChildren() ) {
			for( Grid gc : getChildren() ) {
				if( flipChildren ) gc.flipCR( flipChildren );
				gc.positionInParent();
			}
		}
		
		return this;
	}

	

	
	// OVERRIDE FNS /////////////////////////////////////
		@Override
		public Grid get( int index ) {
			if( inMultiGrid() ) {
				if( !hasChildren() ) {
					throw new UnsupportedOperationException( "cannot get index of grid with no cols / rows");
				}
				return super.get( index );
			}
			return null;
		}
	

	
	
	
	
	

	// POSITION ////////////////////////////////////////////////////

	
	
	
	public Grid positionGrid( float x, float y, float w, float h ) {	// also available in rectangular interface,for convenience and chainable fns
		Rectangular.position( this, x, y, w, h );
		return this;
	}
	
	public Grid positionGrid( PApplet paIn ) {
		Rectangular.position( this, 0,0,paIn.width,paIn.height );
		return this;
	}

	public Grid positionInParent() {
		if( inMultiGrid() && !isRoot() ) {
			int index = indexInParent();
			positionGrid( parent().cellX(index), parent().cellY(index), parent().cellW(index), parent().cellH(index) );
			colXs.setRange( x(), x() + w() );
			rowYs.setRange( y(), y() + h() );
		}
		return this;
	}
	
	public void updatePositions() {
		if( !inMultiGrid() ) return;
		if( hasChildren() ) for( Grid g : getChildren() ) {
			g.positionInParent();
			g.updatePositions();
		}
	}
	
	
	/**
	 * create ar recursively from leaf ars?
	 */
		
	public Grid setARFromLeafs( DoublePredicate vSplitConditon ) {
		setARFromChildrenRecursive( vSplitConditon );
		if( Rectangular.hasDims(this) ) setW(0);
		return this;
	}
	
	public float setARFromChildrenRecursive( DoublePredicate vSplitConditon ) {
		if( hasChildren() ) {
			for( Grid gC : getChildren() ) gC.setARFromChildrenRecursive( vSplitConditon );
			List<Float> childARs = getChildData( g -> g.ar() );
			float vSplitAR = ARObject.sumHorizontalARList( childARs );
			if( vSplitConditon.test( vSplitAR ) ) {
				setAR( vSplitAR );
				if( cr.rows() != 1 ) this.flipCR( false );
			}
			else {
				setAR( ARObject.sumVerticalARList( childARs ) );
				if( cr.cols() != 1 ) this.flipCR( false );
				// update rowYs with reversed child ars?
			}
			// else... add condition for grid with rows and cols
		}
		return ar();
	}
	
	public void makeDimsFromARs() {
		if( hasChildren() ) {
			if( !hasParent() ) {
				if( Rectangular.hasDims(this ) ) setW(0);
				Rectangular.solve( this );
			}
			List<Float> ars = getChildData( g -> g.ar() );
			if( cr.rows() == 1 ) colXs = RangeObject.makeFromDeltas( ars ).setRange( colXs );
			else {
				Collections.reverse(ars);
				rowYs = RangeObject.makeFromDeltas( ars ).setRange( rowYs );
			}
			for( Grid gChild : getChildren() ) {
				gChild.positionInParent();
				gChild.makeDimsFromARs();
			}
		}
	}
	

	
	

	
	// STYLEABLE INTERFACE ////////////////////////////////////////
	
	public Function<Grid,List<PShape>> getStyleableShapes(){
		if( inMultiGrid() ) return s -> s.getLeafs().stream().map( n -> n.getShape() ).collect(Collectors.toList());
		else return s -> IntStream.range(0,size() ).mapToObj( i -> getCellShape(i) ).collect(Collectors.toList());
	}
	public Function<Grid,List<Grid>> getStyleables(){
		if( inMultiGrid() ) return s -> s.getLeafs();
		else return s -> Arrays.asList(this);
	}
	public PShape getShape() {
		return Rectangular.getShape(this);
	};

	// RECTANGULAR INTERFACE ///////////////////////////////////////

	public float x() {
		return x;
	};
	
	public float xc() {
		return x + w / 2;
	};

	public float yc() {
		return y + h / 2;
	}
	
	public float y() {
		return y;
	}

	public float w() {
		return w;
	}

	public float h() {
		return h;
	};

	public Grid setW(float wIn) {
		w = wIn;
		colXs.setRange( x(), x() + w() );
		if( inMultiGrid() && hasChildren() ) 
			for( int i = 0; i < getChildCount(); i++ ) {
				get(i).setW( cellW(i) );
				get(i).setX( cellX(i) );
			}
		return this;
	}

	public Grid setH(float hIn) {
		h = hIn;
		rowYs.setRange( y(), y() + h() );
		if( inMultiGrid() && hasChildren() ) 
			for( int i = 0; i < getChildCount(); i++ ) {
				get(i).setH( cellH(i) );
				get(i).setY( cellY(i) );
			}
		return this;
	}

	public Grid setX(float xIn) {
		x = xIn;
		colXs.setRange( x(), x() + w() );
		if( inMultiGrid() && hasChildren() ) 
			for( int i = 0; i < getChildCount(); i++ ) get(i).setX( cellX(i) );
		return this;
	}

	public Grid setY(float yIn) {
		y = yIn;
		rowYs.setRange( y(), y() + h() );
		if( inMultiGrid() && hasChildren() ) 
			for( int i = 0; i < getChildCount(); i++ ) get(i).setY( cellY(i) );
		return this;
	}
	
	public Grid setAR( float arIn ) {
		ar = arIn;
		return this;
	}

	public float ar() {
		if( ar != 0 ) return ar;	// for solver methods
		else if( Rectangular.hasDims( this ) )
			return w() / h();
		return 0;
	}

	
	
	
	
	
	// GET FNS /////////////////////////////////////////////

	public int size() {
		if( !inMultiGrid() ) return cr.size();
		else {
			int sizeOut = 0;
			for( Grid gChild : getLeafs() ) sizeOut+= gChild.cr.size();
			return sizeOut;
		}
	}
	
	public float cellX(int index) {
		if( type.equals("pw_cc") ) return cellX_pwcc(index);
		return colXs.getValue( cr.col( index ) );
	}
	
	public float cellXFromPolar(int index) {
		return x() + UVConstants.ANGLE_DIST_TO_X.apply( colXs.getValue( cr.col( index ) ), rowYs.getValue( cr.row( index ) ) );
	}
	
	public float cellXFromPolar( float angle, float dist ) {
		return y() + UVConstants.ANGLE_DIST_TO_X.apply( angle , dist  );
	}
	
	public float cellX_pwcc(int index) {	// use an input object for cases, so many situations can be handled with this method? need to store?
		switch( index ) {
		case 0:
			return colXs.getValue( 1 ); // center x
		case 1:
			return colXs.getValue( 1 ); // top x
		case 2:
			return colXs.getValue( 2 ); // right x
		case 3:
			return colXs.getValue( 0 ); // bottom x
		case 4:
			return colXs.getValue( 0 ); // left x
		default:
			System.out.println("index must be 0 to 5 for pinwheel division" );
			return -1f;
		}
	}
	
	public float cellXC( int index ) {
		return cellX( index ) + cellW( index ) / 2;
	}

	public float cellY( int index ) {
		if( type.equals("pw_cc") ) return cellY_pwcc(index);
		return rowYs.getValue( cr.row( index ));
	}
	
	public float cellYFromPolar(int index) {
		return y() + UVConstants.ANGLE_DIST_TO_Y.apply( colXs.getValue( cr.col( index ) ), rowYs.getValue( cr.row( index ) ) );
	}
	
	public float cellYFromPolar( float angle, float dist ) {
		return y() + UVConstants.ANGLE_DIST_TO_Y.apply( angle , dist  );
	}
	
	public float cellY_pwcc(int index) {
		switch( index ) {
		case 0:
			return rowYs.getValue( 1 ); // center y
		case 1:
			return rowYs.getValue( 0 ); // top y
		case 2:
			return rowYs.getValue( 1 ); // right y
		case 3:
			return rowYs.getValue( 2 ); // bottom y
		case 4:
			return rowYs.getValue( 0 ); // left y
		default:
			System.out.println("index must be 0 to 5 for pinwheel division" );
			return -1f;
		}
	}
	
	public float cellYC( int index ) {
		return cellY( index ) + cellH( index ) / 2;
	}

	public float cellW(int index ) {
		if( type.equals("pw_cc") ) return cellW_pwcc(index);
		return colXs.getValueDelta( cr.col( index ) );
	}
	
	public float cellW_pwcc(int index) {
		switch( index ) {
		case 0:
			return colXs.getValueDelta( 1 ); // center w
		case 1:
			return colXs.getValueDelta( 1 ) + colXs.getValueDelta( 2 ); // top w
		case 2:
			return colXs.getValueDelta( 2 ); // right w
		case 3:
			return colXs.getValueDelta( 0 ) + colXs.getValueDelta( 1 ); // bottom w
		case 4:
			return colXs.getValueDelta( 0 ); // left w
		default:
			System.out.println("index must be 0 to 5 for pinwheel division" );
			return -1f;
		}
	}

	public float cellH(int index ) {
		if( type.equals("pw_cc") ) return cellH_pwcc(index);
		return rowYs.getValueDelta( cr.row( index ) );
	}
	
	public float cellH_pwcc(int index) {
		switch( index ) {
		case 0:
			return rowYs.getValueDelta( 1 ); // center h
		case 1:
			return rowYs.getValueDelta( 0 ); // top h
		case 2:
			return rowYs.getValueDelta( 1 ) + rowYs.getValueDelta( 2 ); // right h
		case 3:
			return rowYs.getValueDelta( 2 ); // bottom h
		case 4:
			return rowYs.getValueDelta( 0 ) + rowYs.getValueDelta( 1 ); // left h
		default:
			System.out.println("index must be 0 to 5 for pinwheel division" );
			return -1f;
		}
	}
	
	public int getColor( PImage img, int... index ) {	// no index, just samples from grid
		 if( index.length > 0 ) return img.get( (int)cellXC(index[0]) , (int)cellYC(index[0]) );
		 else return img.get( (int)xc() , (int)yc() );
	}
	
	public float getBrightness( PImage img, int... index ) {
		if( index.length > 0 ) return img.parent.brightness( img.get( (int)cellXC(index[0]) , (int)cellYC(index[0]) ) );
		else return img.parent.brightness( img.get( (int)xc(), (int)yc() ) );
	}
	
	public int regularGridContainingCell( float xVal, float yVal ) {	// make rangeobject version
		int myCol = (int)Math.floor( ( xVal - x() ) / cellW(0) );
		int myRow = (int)Math.floor( ( yVal - y() ) / cellH(0) );
		return cr.getIndex(myCol, myRow);
	}
	
//	public float getNoiseValue( PApplet pa, float nScl ) { ??
//		if( Rectangular.hasDims(this ) ) return pa.noise( xc() * nScl, yc() * nScl );
//		else if( hasParent() ) return pa.noise( parent().cellXC( indexInParent())* nScl,
//												parent().cellYC( indexInParent())* nScl ); 
//		else return 0;
//	}
//	
//	public float getImgBrValue( PApplet pa, PImage img ) {
//		if( Rectangular.hasDims(this ) ) return pa.brightness( img.get( (int)xc() , (int)yc() ) );
//		else return 0;
//	}
	
//	public List<RectObject> getRects(){
//		List<RectObject> out = new ArrayList<>();
//		for( int i = 0; i < cr.size(); i++ ) {
//			out.add( new RectObject( cellX(i),cellY(i),cellW(i),cellH(i) ) );
//		}
//		return out;
//	}
	// OUTPUT FNS ///////////////////////////////////////////////////////
	
	// public static Function<Integer,Float> noiseVal( )

	public float noiseVal(PApplet pa, float nScl) {
		return pa.noise(xc() * nScl, yc() * nScl);
	}

	public float noiseVal(PApplet pa, float nScl, int index) {
		return pa.noise(cellXC(index) * nScl, cellYC(index) * nScl);
	}

	public RectObject makeRect(int i) {
		return new RectObject(cellX(i), cellY(i), cellW(i), cellH(i));
	}
	
	public List<RectObject> getRects(){
//		List<RectObject> out = new ArrayList<>();
//		for( int i : cr ) out.add( makeRect(i) );
//		return out;
		return IntStream.range(0, cr.size() )
				.mapToObj( i -> new RectObject( cellX(i),cellY(i),cellW(i),cellH(i) ) )
				.collect(Collectors.toList());
	}

	public PShape getCellShape(int i) {
//		return new RectObject(cellX(i), cellY(i), cellW(i), cellH(i)).toShape();
		return Rectangular.getShape( new float[] { cellX(i), cellY(i), cellW(i), cellH(i) } );
	}
	

	// CHECKS ///////////////////////////////////////////////////////////

	public boolean isRegular() {
		return colXs.equalDeltas() && rowYs.equalDeltas();
	}
	
	public boolean isDividedCell() {
		return colXs != null && rowYs != null;
	}
	
	public boolean isSingleCell() {
		return cr.cols() == 1 && cr.rows() == 1;
	}
	
	public boolean inMultiGrid() {
		return nodeList() != null;
	}
	
	public String splitDir() {
		return cr.cols() == 1 ? "h" : cr.rows() == 1 ? "v" : "n";
	}
	
	
	
	// BUILDER CLASSES //////////////////////////////////////////////////////

	/**
     * can gridsolver and divisionBuilder be 1 class, simply called divide?
     * has advanced functionality and simple row / col division moved here as well
     */
	
//	public Divider divide() {	// replaces solve and subdivide classes
//		return new Divider( this );
//	}
	
	
//	public DisplayOperation displayOperation() {
//		List<PShape> shapesOut;
//		if( !inMultiGrid() ) shapesOut = IntStream.range(0,size() ).mapToObj( i -> makePShape(i) ).collect(Collectors.toList() );
//		else  shapesOut = getLeafs().stream().map( g -> Rectangular.getShape( g ) ). collect( Collectors.toList() );
//		return new DisplayOperation( shapesOut );
//	}
//	
		
	public QuadSample<Grid> quadSample() {
		return new QuadSample<Grid>( this );
	}
	
	// POLAR FUNCTIONS ////////////////////////////////////
	
	/**
	 * returns polar display object
	 * for situations when cartesian grid just needs to be visualized as polar
	 */
//	public PolarDisplay polarDisplay() {
//		return new PolarDisplay( this );
//	}
	
	public static Grid polarGrid( float x, float y, float radius ) {
		Grid out = new Grid();
		out.setX( x );
		out.setY( y );
		out.colXs.setRange( 0f, (float)Math.PI*2 );
		out.rowYs.setRange( 0f, radius );
		out.space = "POLAR";
		return out;
	}
	
	public Grid setOuterRadius( float radius ) {
		rowYs.setRange( rowYs.start(), radius );
		return this;
	}
	public Grid setInnerRadius( float radius ) {
		rowYs.setRange( radius, rowYs.end() );
		return this;
	}
	public Grid setAngle( float angle ) {
		colXs.setRange( colXs.start(), colXs.start()+angle );
		return this;
	}
	public Grid setStartAngle( float angle ) {
		colXs.setRange( angle, angle+colXs.range() );
		return this;
	}
	
	
	
	// PREDICATES /////////////////////////////////////////////////////////
	
	/*
	 * for multigrid only
	 */
	public boolean checkerEven(){
		boolean out = parent().cr.checkerEven.test( indexInParent() );
		return out;
	}
	
	
	
	
	

	// DISPLAY FNS //////////////////////////////////////////////////////

	
	// being mixed up with display( PApplet pa )
//	public void display( PApplet pa, int...indexes ) {
//		if( inMultiGrid() && hasChildren() ) return;
//		if( indexes.length == 0 ) return;
//		else {
//			for( int i : indexes ) new RectObject( cellX( i ),cellY( i ),cellW( i ),cellH( i ) ).display( pa );
//		}
//	}
	
	
	public void display( PApplet pa ) {
		if( inMultiGrid() && hasChildren() )for( Grid ch : getLeafs() ) ch.display(pa);
		else  if( !inMultiGrid() && cr.size() > 1 ) for( int i : cr ) new RectObject( cellX(i),cellY(i),cellW(i),cellH(i) ).display( pa );
		else new RectObject( x(),y(),w(),h() ).display( pa );
	}
	
	public void display( PApplet pa, ColorPalette cpIn ) {
//		pa.fill( PFns.rColor() );
		if( !inMultiGrid() ) 
			cpIn.colorOperation( getRects(), (c,r) -> { pa.fill(c); r.display(pa); } );
		else {
			if( hasChildren() ) for( Grid ch : getChildren() ) ch.display( pa, cpIn );
			else {
				pa.fill( cpIn.selector.selectValue(this) );
				Rectangular.display(this, pa);
			}
		}
	}
	
	public void displayBoundary( PApplet pa) {
		Rectangular.display(this, pa );
	}

	
	
	
	


}
