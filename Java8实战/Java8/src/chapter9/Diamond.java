package chapter9;

/**
 * 菱形继承问题
 * 
 * @author Loops
 *
 */
public class Diamond {

	static interface A {
		public default void hello() {
			System.out.println("Hello from A");
		}
	}

	static interface B extends A {
	}

	static interface C extends A {
	}

	static class D implements B, C {
		public static void main(String[] args) {
			new D().hello();
		}
	}
}