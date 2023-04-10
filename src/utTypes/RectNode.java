package utTypes;

import uvCore.CRObject;
import pFns_general.*;
import utCore.*;
import usPrimitives.*;

import processing.core.PApplet;

import java.util.List;
import java.util.function.*;

public class RectNode extends AbstractNode<RectTree, RectNode> implements Rectangular {
	List<CRObject> myCrList;
	
	float x, y, w, h;
	float ar;
	public String splitDir = "";

	// CONSTRUCTOR ///////////////////////////////////////

	public RectNode( List<CRObject> crObjs ) {
		this.myCrList = crObjs;
	}

	// ABSTRACT FNS //////////////////////////////////////
	public RectNode defaultConstructor() {
		return new RectNode( myCrList );
	}

	public RectNode getInstance() {
		return this;
	}
	
	public RectTree treeDefaultConstructor() {
		return new RectTree(  );
	}

	// RECTANGULAR INTERFACE ///////////////////////////////////

	public float x() {
		return x;
	};

	public float y() {
		return y;
	}

	public float w() {
		return w;
	}

	public float h() {
		return h;
	};

	public RectNode setW(float wIn) {
		w = wIn;
		return this;
	}

	public RectNode setH(float hIn) {
		h = hIn;
		return this;
	}

	public RectNode setX(float xIn) {
		x = xIn;
		return this;
	}

	public RectNode setY(float yIn) {
		y = yIn;
		return this;
	}

	public float ar() {
		return ar;
	}

	public void setAR(float ar) {
		this.ar = ar;
	}

	public float area() {
		return w * h;
	}

	// FNS /////////////////////////////////////////////////////////////////////////
	
	
	// TOP DOWN ARRANGEMENT ////////////////////////////////////////////////////////


	public void subdivide(String dir, float... percentages) {
		if( hasChildren() ) return;
		if( percentages.length == 1 && percentages[0] % 1 == 0 ) {
			percentages = new float[(int)percentages[0]];
			for( int i = 0; i < percentages.length; i++ ) percentages[i] = 1;				
		}
		splitDir = dir;
		float[] ars = ARObject.subdivideAR(this, splitDir, percentages);
		for (int i = 0; i < ars.length; i++) addChild();
		for (int i = 0; i < ars.length; i++) get(i).ar = ars[i];
		setElem( splitDir.equals("v") ? new CRObject( getChildCount(), 1 ) : new CRObject( 1, getChildCount() ), myCrList );
		if (Rectangular.hasDims(this))
			transferDims();
	}

	public void subdivideNoise(String dir, int ct, float nScl, PApplet pa ) {
		float[] divs = dir.equals("v") ? noiseList(x(), y(), x() + w(), y(), ct, nScl, pa)
				: noiseList(x(), y(), x(), y() + h(), ct, nScl, pa);
		subdivide(dir, divs);
	}

	
	public float[] getChildARs() {
		float[] out = new float[getChildCount()];
		for (int i = 0; i < out.length; i++)
			out[i] = get(i).ar();
		return out;
	}

	public float[] noiseList(float stX, float stY, float edX, float edY, int ct, float nScl, PApplet pa ) {
		float[] out = new float[ct];
		float xStep = Math.abs(stX - edX) / (float) ct;
		float yStep = Math.abs(stY - edY) / (float) ct;
		for (int i = 0; i < ct; i++)
			out[i] = PFns.parametricBlend( pa.noise((stX + i * xStep) * nScl, (stY + i * yStep) * nScl), 6);
		return out;
	}

	public void transferDims() {
		if (!hasChildren())
			return;
		float[][] childDims = Rectangular.getSubdividedDims(this, splitDir, getChildARs());
		for (int i = 0; i < getChildCount(); i++)
			Rectangular.position(get(i), childDims[i]);
		for (RectNode n : children())
			n.transferDims();
	}
	
	// BOTTOM UP ARRANGEMENT ////////////////////////////////////////////////////

	public void display(PApplet pa) {
		Rectangular.display(this, pa);
	}

}