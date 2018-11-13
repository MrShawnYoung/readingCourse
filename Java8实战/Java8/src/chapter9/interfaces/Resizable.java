package chapter9.interfaces;

public interface Resizable extends Drawable {
	public int getWidth();

	public int getHeight();

	public void setWidth(int width);

	public void setHeight(int height);

	public void setAbsoluteSize(int width, int height);

	/* 第二版API添加了一个新方法 */
	// public void setRelativeSize(int widthFactor, int heightFactor);
}