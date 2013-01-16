package togos.zoompaint;

public class Color
{
	public static final int component( int c, int shift ) {
		return (c >> shift) & 0xFF;
	}
	
	public static final int color( int a, int r, int g, int b ) {
		return
			((a&0xFF) << 24) | ((r&0xFF) << 16) |
			((g&0xFF) <<  8) | ((b&0xFF) <<  0);
	}
	
	public static final int average( int c0, int c1, int c2, int c3 ) {
		return color(
			component(c0,24) + component(c1,24) + component(c2,24) + component(c3,24), 
			component(c0,16) + component(c1,16) + component(c2,16) + component(c3,16),
			component(c0, 8) + component(c1, 8) + component(c2, 8) + component(c3, 8),
			component(c0, 0) + component(c1, 0) + component(c2, 0) + component(c3, 0)
		);
	}
}
