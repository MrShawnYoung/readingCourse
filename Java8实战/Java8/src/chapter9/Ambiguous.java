package chapter9;

public class Ambiguous {

	static interface A {
		public default void hello() {
			System.out.println("Hello from A");
		}
	}

	static interface B {
		public default void hello() {
			System.out.println("Hello from B");
		}
	}

	static class C implements B, A {
		@Override
		public void hello() {
			/* 显示地选择调用接口B中的方法 */
			B.super.hello();
		}
	}
}