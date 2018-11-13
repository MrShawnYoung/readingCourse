package chapter9;

import java.util.List;

import chapter9.interfaces.Resizable;

public class Utils {

	public static void paint(List<Resizable> l) {
		l.forEach(r -> {
			/* 调用每个形状自己的setAbsoluteSize方法 */
			r.setAbsoluteSize(42, 42);
			r.draw();
		});
	}
}