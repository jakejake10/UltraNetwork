package utTypes;

import pFns_general.PFns;
import utCore.*;

public class ARNode extends AbstractNode<ARTree, ARNode> {
	public String splitDir = "";
	public float ar;

	public ARNode(ARTree myTree, String... mode) {
		super(myTree, mode);
	}
	
	public void addChild( float ar ) {
		super.addChild();
		lastChild().ar = ar;
	}
	
	public void addChild( float ar, String splitDir ) {
		super.addChild();
		lastChild().ar = ar;
		lastChild().splitDir = splitDir;
	}


	// ABSTRACT FNS //////////////////////////////////////////////////

	public ARNode defaultConstructor(ARTree myTree, String... mode) {
		return new ARNode(myTree);
	}

	public ARNode getInstance() {
		return this;
	}
	
	// GET FNS /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	
	public static float ar(float w, float h) {
		return w / h;
	}

	public float hFromW(float wDim ) {
		return wDim / ar;
	}

	public float wFromH(float hDim ) {
		return hDim * ar;
	}

	public float areaFromW( float wDim ) {
		return wDim * ( wDim / ar );
	}

	public float areaFromH( float hDim ) {
		return hDim * ar * hDim;
	}

	public float hFromArea( float area ) {
		return (float) Math.sqrt(area / ar);
	}

	public float wFromArea( float area ) {
		return area / hFromArea( area );
	}
	
	public float changeVal( float constantVal, float parAR ) {
		if( splitDir.equals("v")) return constantVal * ( ar / parAR );	// cv = width
		else                      return constantVal * ( ( 1 / ar ) / ( 1 / parAR ) ); // cv = height
	}

	// NEW FNS ///////////////////////////////////////////////////////
	
	public void subdivideEqually( String splitDirIn, int count) {
		float[] arr = new float[count];
		for (int i = 0; i < count; i++) arr[i] = 1;
		subdivide( splitDirIn, arr );
	}
	
	public void subdivide( String splitDir, float... percents ) {
		if ( hasChildren() || percents.length == 0 ) return;
		this.splitDir = splitDir;
		if (percents.length == 1)								// splits node at percentage
			percents = new float[] { percents[0], 1 - percents[0] };
		percents = PFns.makeSumTo1(percents);
		float[] newArs = new float[percents.length];
		for (int i = 0; i < percents.length; i++) {
			if ( splitDir.equals("v") )
				newArs[i] = ar * percents[i];
			else
				newArs[i] = 1 / (1 / ar * percents[i]);
		}
		for (int i = 0; i < newArs.length; i++)
			addChild( newArs[i] );
	}

	///////////////////////////////////////////////////////

	public float calcAR() {
		if ( hasChildren() ) {
			float arSum = 0;
			for (ARNode child : children())
				arSum += splitDir == "v" ? child.calcAR() : 1 / child.calcAR();
			ar = splitDir == "v" ? arSum : 1 / arSum;
		}
		return ar;
	}
	
	
	
}
