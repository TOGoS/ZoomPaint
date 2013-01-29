package togos.zoompaint;

public final class Node
{
	public static final Object SELF = new Object();
	public static final Node EMPTY = new Node(SELF,SELF,SELF,SELF);
		
	public final Node n0, n1, n2, n3;
	// Hold precalculated functions of the node for supposed efficiency:
	public final int color, opCode;
	
	protected int v1(Node n) {
		return n == this ? 0 : 1;
	}
	
	protected int v4() {
		return (v1(n0)<< 3) | (v1(n1)<<2) | (v1(n2)<<1) | (v1(n3)<<0);
	}
	
	protected int v16() {
		return (n0.v4()<<12) | (n1.v4()<<8) | (n2.v4()<<4) | (n3.v4()<<0);
	}
	
	protected boolean isOpNode() {
		return n0 == this;
	}
	
	/*
	 * Opcodes
	 * 0x00000000 - transparent, do-nothing
	 * 0x00000001 - use color of n2, function of n3
	 */
	
	protected static int calculateColor( Node n ) {
		if( n.isOpNode() ) {
			return Color.from4To32Bits(n.n2.v4());
		} else {
			return Color.average( n.n0.color, n.n1.color, n.n2.color, n.n3.color );
		}
	}
	
	protected Node parseSubNode( Object o ) {
		if( o == SELF ) return this;
		if( o instanceof Node ) return (Node)o;
		throw new RuntimeException("Invalid node argument: "+o.getClass());
	}
	
	public Node( Object n0, Object n1, Object n2, Object n3 ) {
		this.n0 = parseSubNode(n0);
		this.n1 = parseSubNode(n1);
		this.n2 = parseSubNode(n2);
		this.n3 = parseSubNode(n3);
		this.color = calculateColor(this);
		this.opCode = this.n1.v16();
	}
}
