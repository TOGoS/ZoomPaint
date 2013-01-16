package togos.zoompaint;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

public class Interpreter
{
	static class NodeImageCache {
		WeakHashMap<Node,BufferedImage> images = new WeakHashMap();
		
		protected void getPixels( Node n, int[] pix, int off, int scan, int depth ) {
			if( depth == 0 ) {
				pix[off] = n.color;
			} else {
				int size = (1 << depth);
				int subSize = size>>1;
				getPixels(n.n0, pix, off,                      scan, depth-1);
				getPixels(n.n1, pix, off+subSize,              scan, depth-1);
				getPixels(n.n2, pix, off+scan*subSize,         scan, depth-1);
				getPixels(n.n3, pix, off+scan*subSize+subSize, scan, depth-1);
			}
		}
		
		public BufferedImage getNodeImage( Node n ) {
			BufferedImage img = images.get(n);
			if( img == null ) {
				int[] pix = new int[64*64];
				getPixels( n, pix, 0, 64, 6 );
				img = new BufferedImage( 64, 64, BufferedImage.TYPE_INT_ARGB );
				img.setRGB(0,  0, 64, 64, pix, 0, 64);
				images.put(n, img);
			}
			return img;
		}
	}
	
	public static void main( String[] args ) throws Exception {
		NodeImageCache c = new NodeImageCache();
		
		ArrayList stack = new ArrayList();
		HashMap<Object,Object> saved = new HashMap<Object,Object>();
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while( (line = lineReader.readLine()) != null ) {
			String[] parts = line.split("\\s+");
			for( String p : parts ) {
				if( saved.containsKey(p) ) {
					stack.add( saved.get(p) );
				} else if( "empty".equals(p) ) {
					stack.add(Node.EMPTY);
				} else if( "self".equals(p) ) {
					stack.add(Node.SELF);
				} else if( "make-node".equals(p) ) {
					int argsAt = stack.size()-4;
					stack.add(new Node(
						stack.remove(argsAt),
						stack.remove(argsAt),
						stack.remove(argsAt),
						stack.remove(argsAt)
					));
				} else if( "dup".equals(p) ) {
					stack.add(stack.get(stack.size()-1));
				} else if( "set".equals(p) ) {
					saved.put( stack.remove(stack.size()-1), stack.remove(stack.size()-1) );
				} else if( "to-image".equals(p) ) {
					stack.add(c.getNodeImage( (Node)stack.remove(stack.size()-1) ));
				} else if( "save-image".equals(p) ) {
					String name = (String)stack.remove(stack.size()-1);
					BufferedImage img = (BufferedImage)stack.remove(stack.size()-1);
					ImageIO.write( img, "png", new File(name) );
				} else if( p.startsWith("'") ) {
					stack.add( p.substring(1) );
				} else {
					throw new RuntimeException("Unrecognised word: "+p);
				}
			}
		}
	}
}
