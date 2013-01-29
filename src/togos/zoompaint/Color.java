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
	
	protected static final int roundDiv( int num, int den ) {
		return (int)Math.round((double)num / den);
	}
	
	public static final int average( int c0, int c1, int c2, int c3 ) {
		return color(
			roundDiv(component(c0,24) + component(c1,24) + component(c2,24) + component(c3,24), 4), 
			roundDiv(component(c0,16) + component(c1,16) + component(c2,16) + component(c3,16), 4),
			roundDiv(component(c0, 8) + component(c1, 8) + component(c2, 8) + component(c3, 8), 4),
			roundDiv(component(c0, 0) + component(c1, 0) + component(c2, 0) + component(c3, 0), 4)
		);
	}
	
	public static final int from4To32Bits( int v4 ) {
		return
			((v4 & 8) == 0 ? 0x00000000 : 0xFF000000) | 
			((v4 & 4) == 0 ? 0x00000000 : 0x00FF0000) |
			((v4 & 2) == 0 ? 0x00000000 : 0x0000FF00) |
			((v4 & 1) == 0 ? 0x00000000 : 0x000000FF);
	}
}
