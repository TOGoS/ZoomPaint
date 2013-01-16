package togos.zoompaint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter
{
	public static void main( String[] args ) throws Exception {
		ArrayList stack = new ArrayList();
		HashMap<Object,Object> saved = new HashMap<Object,Object>();
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while( (line = lineReader.readLine()) != null ) {
			String[] parts = line.split("\\s+");
			for( String p : parts ) {
				if( saved.containsKey(p) ) {
					stack.add( saved.get(p) );
				} else if( "t".equals(p) ) {
					stack.add(Boolean.TRUE);
				} else if( "f".equals(p) ) {
					stack.add(Boolean.FALSE);
				} else if( "node".equals(p) ) {
					int argsAt = stack.size()-4;
					stack.add(new Node(
						stack.remove(argsAt),
						stack.remove(argsAt),
						stack.remove(argsAt),
						stack.remove(argsAt)
					));
				} else if( "dup".equals(p) ) {
					stack.add(stack.get(stack.size()-1));
				} else if( "save".equals(p) ) {
					saved.put( stack.remove(stack.size()-1), stack.remove(stack.size()-1) );
				} else if( p.startsWith("'") ) {
					stack.add( p.substring(1) );
				}
			}
		}
	}
}
