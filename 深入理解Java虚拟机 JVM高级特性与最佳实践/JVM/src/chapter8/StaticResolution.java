package chapter8;

/**
 * 方法静态解析演示
 */
public class StaticResolution {

	public static void sayHello() {
		System.out.println("hello world!");
	}

	public static void main(String[] args) {
		StaticResolution.sayHello();
	}
}