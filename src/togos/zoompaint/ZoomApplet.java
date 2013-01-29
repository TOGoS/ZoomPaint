package togos.zoompaint;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class ZoomApplet extends Applet
{
	private static final long serialVersionUID = 3404775679722832785L;
	
	BufferedImage img = null;
	
	@Override public void paint( Graphics g ) {
		g.setColor(Color.BLACK);
		g.fillRect(0,  0, getWidth(), getHeight());
		if( img == null ) {
			g.setColor(Color.RED);
			g.drawString("No image", 0, 20);
		} else {
			g.drawImage(img, 0, 0, null );
		}
	}
	
	@Override public void update( Graphics g ) { paint(g); }
	
	public void setImage( BufferedImage img ) {
		this.img = img;
		repaint();
	}
	
	public void runWindowed() {
		final Frame f = new Frame("ZoomPaint");
		init();
		f.add(this);
		f.pack();
		f.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
				destroy();
			}
		});
		f.setVisible(true);
	}
}
