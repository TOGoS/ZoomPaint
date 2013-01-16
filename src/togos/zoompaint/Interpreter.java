package togos.zoompaint;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	interface Word {
		public boolean isImmediate();
		public void run( List stack ) throws Exception;
	}
	
	abstract class RuntimeWord implements Word {
		@Override public boolean isImmediate() { return false; }
	}
	
	abstract class CompileWord implements Word {
		@Override public boolean isImmediate() { return true; }
	}
	
	class ValueWord extends RuntimeWord {
		Object value;
		public ValueWord( Object value ) {
			this.value = value;
		}
		@Override public void run(List stack) {
			stack.add( value );
		}
	}
	
	NodeImageCache nodeImageCache = new NodeImageCache();
	
	protected final int MODE_INTERP  = 0; // evaluate words immediately
	protected final int MODE_DEFWORD = 1; // next word = name to define
	protected final int MODE_COMPILE = 2; // defining a word into compileTo
	
	HashMap<String, Word> dict = new HashMap();
	List stack = new ArrayList();
	int mode = MODE_INTERP;
	List<Word> compileTo = null;
	
	public void initStackWords() {
		dict.put("dup", new RuntimeWord() {
			@Override public void run(List stack) {
				stack.add(stack.get(stack.size()-1));
			}
		});
		dict.put("drop", new RuntimeWord() {
			@Override public void run(List stack) {
				stack.remove(stack.size()-1);
			}
		});
	}
	
	public void initWordWords() {
		dict.put("set", new RuntimeWord() {
			@Override public void run(List stack) {
				dict.put(
					(String)stack.remove(stack.size()-1),
					new ValueWord(stack.remove(stack.size()-1))
				);
			}
		});
		dict.put(":", new CompileWord() {
			@Override public void run(List stack) throws Exception {
				mode = MODE_DEFWORD;
			}
		});
		dict.put(";", new CompileWord() {
			@Override public void run(List stack) throws Exception {
				mode = MODE_INTERP;
			}
		});
	}

	public void initNodeWords() {
		dict.put("empty", new ValueWord(Node.EMPTY));
		dict.put("self", new ValueWord(Node.SELF));
		dict.put("make-node", new RuntimeWord() {
			@Override public void run(List stack) {
				int argsAt = stack.size()-4;
				stack.add(new Node(
					stack.remove(argsAt),
					stack.remove(argsAt),
					stack.remove(argsAt),
					stack.remove(argsAt)
				));
			}
		});
		dict.put("to-image", new RuntimeWord() {
			@Override public void run(List stack) {
				stack.add(nodeImageCache.getNodeImage( (Node)stack.remove(stack.size()-1) ));
			}
		});
	}
	
	public void initEnvWords() {
		dict.put("save-image", new RuntimeWord() {
			@Override public void run(List stack) throws IOException {
				String name = (String)stack.remove(stack.size()-1);
				BufferedImage img = (BufferedImage)stack.remove(stack.size()-1);
				ImageIO.write( img, "png", new File(name) );
			}
		});
		dict.put("exit", new RuntimeWord() {
			@Override public void run(List stack) throws Exception {
				System.exit(0);
			}
		});
	}
	
	public void doWord( String p ) throws Exception {
		if( mode == MODE_DEFWORD ) {
			final List<Word> wordList = compileTo = new ArrayList();
			mode = MODE_COMPILE;
			dict.put( p, new RuntimeWord() {
				@Override public void run(List stack) throws Exception {
					for( Word w : wordList ) w.run(stack);
				}
			});
			mode = MODE_COMPILE;
			return;
		}
		
		Word w;
		if( p.startsWith("'") ) {
			w = new ValueWord( p.substring(1) );
		} else {
			w = dict.get(p);
		}
		if( w == null ) {
			throw new RuntimeException("Undefined word: "+p);
		}
		if( mode == MODE_COMPILE && !w.isImmediate() ) {
			compileTo.add(w);
		} else {
			w.run( stack );
		}
	}
	
	public static void main( String[] args ) throws Exception {
		Interpreter interp = new Interpreter();
		interp.initStackWords();
		interp.initNodeWords();
		interp.initWordWords();
		interp.initEnvWords();
		
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		ZoomApplet app = new ZoomApplet();
		app.setPreferredSize(new Dimension(64,64));
		app.runWindowed();
		while( (line = lineReader.readLine()) != null ) {
			String[] parts = line.split("\\s+");
			for( String p : parts ) {
				interp.doWord(p);
			}
			
			Node topNode = null;
			for( int i=interp.stack.size()-1; i>=0; --i ) {
				if( interp.stack.get(i) instanceof Node ) {
					topNode = (Node)interp.stack.get(i);
					break;
				}
			}
			app.setImage(topNode == null ? null : interp.nodeImageCache.getNodeImage(topNode));
		}
	}
}
