package unCore;

public class Edge {
	int src, dest, weight;

	
	Edge( int src, int dest ) {
		this.src = src;
		this.dest = dest;
	}
	
	Edge(int src, int dest, int weight) {
		this.src = src;
		this.dest = dest;
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Edge)) return false;
	    Edge otherMyClass = (Edge)other;
	    if( ( src == otherMyClass.src && dest == otherMyClass.dest ) ||
	    		( src == otherMyClass.dest && dest == otherMyClass.src ) &&
	    		weight == otherMyClass.weight ) return true;
	    return false;
	}
	
	
}
