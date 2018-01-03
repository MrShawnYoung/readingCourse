package proxy;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageProxy implements Icon {
	ImageIcon icon;
	URL imageURL;
	Thread thread;
	boolean retrieving = false;

	public ImageProxy(URL url) {
		this.imageURL = url;
	}

	@Override
	public int getIconWidth() {
		if (icon != null) {
			return icon.getIconWidth();
		} else {
			return 800;
		}
	}

	@Override
	public int getIconHeight() {
		if (icon != null) {
			return icon.getIconHeight();
		} else {
			return 600;
		}
	}

	@Override
	public void paintIcon(final Component c, Graphics g, int x, int y) {
		if (icon != null) {
			icon.paintIcon(c, g, x, y);
		} else {
			g.drawString("Loading CD cover,please wait...", x + 300, y + 190);
			if (!retrieving) {
				retrieving = true;
				thread = new Thread(new Runnable() {
					public void run() {
						try {
							icon = new ImageIcon(imageURL, "CD cover");
							c.repaint();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		}
	}
}