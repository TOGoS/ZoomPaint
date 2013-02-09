package togos.zoompaint;

public final class Node
{
	public static final Object SELF = new Object();
	public static final Node FULL = new Node(SELF,SELF,SELF,SELF,SELF,SELF,SELF,SELF);
		
	private final Node[] subNodes = new Node[8];
	
	// Hold precalculated functions of the node for supposed efficiency:
	public final boolean isSelfColored;
	public final int[] colorCycle;
	public final byte v8;
	public final long v64;
	
	protected static int v1(Node p, Node n) {
		return n == p ? 1 : 0;
	}
	
	protected static byte v8(Node n) {
		return (byte)(
			(v1(n, n.subNodes[0]) << 7) |
			(v1(n, n.subNodes[1]) << 6) |
			(v1(n, n.subNodes[2]) << 5) |
			(v1(n, n.subNodes[3]) << 4) |
			(v1(n, n.subNodes[4]) << 3) |
			(v1(n, n.subNodes[5]) << 2) |
			(v1(n, n.subNodes[6]) << 1) |
			(v1(n, n.subNodes[7]) << 0)
		);
	}
	
	protected static long v64(Node n) {
		return
			((long)v8(n.subNodes[0]) << 56) |
			((long)v8(n.subNodes[1]) << 48) |
			((long)v8(n.subNodes[2]) << 40) |
			((long)v8(n.subNodes[3]) << 32) |
			((long)v8(n.subNodes[4]) << 24) |
			((long)v8(n.subNodes[5]) << 16) |
			((long)v8(n.subNodes[6]) <<  8) |
			((long)v8(n.subNodes[7]) <<  0);
	}
	
	/*
	 * Opcodes
	 * 0x00000000 - transparent, do-nothing
	 * 0x00000001 - use color of n2, function of n3
	 */
	
	protected static int[] calculateColorCycle( Node n ) {
		switch( ((n.v8>>4) & 0x0F) ) {
		case 1:
			// Solid color defined by bottom 4 numbers
			return Color.cycle( n.v8&0xF );
		default:
			return Color.average(
				Color.average(
					Color.average( n.subNodes[0].colorCycle, n.subNodes[1].colorCycle ),
					Color.average( n.subNodes[2].colorCycle, n.subNodes[3].colorCycle )
				), Color.average(
					Color.average( n.subNodes[4].colorCycle, n.subNodes[5].colorCycle ),
					Color.average( n.subNodes[6].colorCycle, n.subNodes[7].colorCycle )
				)
			);
		}
	}
	
	protected Node parseSubNode( Object o ) {
		if( o == SELF ) return this;
		if( o instanceof Node ) return (Node)o;
		throw new RuntimeException("Invalid node argument: "+o.getClass());
	}
	
	public Node( Object[] subNodeParams ) {
		assert subNodeParams.length == 8;
		for( int i=0; i<8; ++i ) {
			this.subNodes[i] = parseSubNode(subNodeParams[i]);
		}
		this.v8  =  v8(this);
		this.v64 = v64(this);
		this.isSelfColored = v8 != 0;
		this.colorCycle    = calculateColorCycle(this);
	}
	
	public Node( Object o0, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7 ) {
		this( new Object[]{o0,o1,o2,o3,o4,o5,o6,o7} );
	}
	
	public Node subNode(int idx) { return subNodes[idx]; }
}
