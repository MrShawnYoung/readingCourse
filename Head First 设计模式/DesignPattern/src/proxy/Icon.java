package proxy;

import java.awt.Component;
import java.awt.Graphics;

/**
 * 图标接口
 * 
 * @author 杨弢
 * 
 */
public interface Icon {
	public int getIconWidth();

	public int getIconHeight();

	public void paintIcon(final Component c, Graphics g, int x, int y);
}
