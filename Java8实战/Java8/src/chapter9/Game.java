package chapter9;

import java.util.Arrays;
import java.util.List;

import chapter9.classes.Ellipse;
import chapter9.classes.Square;
import chapter9.classes.Triangle;
import chapter9.interfaces.Resizable;

public class Game {

	public static void main(String[] args) {
		/* 可以调整大小的形状列表 */
		List<Resizable> resizableShapes = Arrays.asList(new Square(), new Triangle(), new Ellipse());
		Utils.paint(resizableShapes);
	}
}