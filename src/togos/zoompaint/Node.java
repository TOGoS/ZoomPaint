package togos.zoompaint;

public final class Node
{
	public final Node n0, n1, n2, n3;
	public final int color;
	
	public static final Node EMPTY = new Node(true,true,true,true);
	
	protected static boolean isAtom( Node n ) {
		return
			(n.n0 == n || n.n0 == EMPTY) &&
			(n.n1 == n || n.n1 == EMPTY) &&
			(n.n2 == n || n.n2 == EMPTY) &&
			(n.n3 == n || n.n3 == EMPTY);
	}
	
	protected static int calculateColor( Node n ) {
		if( isAtom(n) ) {
			return Color.color(
				n.n0 == EMPTY ? 0xFF : 0,
				n.n1 == EMPTY ? 0xFF : 0,
				n.n2 == EMPTY ? 0xFF : 0,
				n.n3 == EMPTY ? 0xFF : 0
			);
		} else {
			return Color.average( n.n0.color, n.n1.color, n.n2.color, n.n3.color );
		}
	}
	
	protected Node parseSubNode( Object o ) {
		if( o == Boolean.TRUE ) return this;
		if( o == Boolean.FALSE || o == null ) return EMPTY;
		if( o instanceof Node ) return (Node)o;
		throw new RuntimeException("Invalid node argument: "+o.getClass());
	}
	
	public Node( Object n0, Object n1, Object n2, Object n3 ) {
		this.n0 = parseSubNode(n0);
		this.n1 = parseSubNode(n1);
		this.n2 = parseSubNode(n2);
		this.n3 = parseSubNode(n3);
		this.color = calculateColor(this);
	}
	
	public Node( boolean n0, boolean n1, boolean n2, boolean n3 ) {
		this( Boolean.valueOf(n0), Boolean.valueOf(n1), Boolean.valueOf(n2), Boolean.valueOf(n3) );
	}
}
