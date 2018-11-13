package chapter9;

public class MostSpecific {
	static interface A {
		public default void hello() {
			System.out.println("Hello from A");
		}
	}

	static interface B extends A {
		public default void hello() {
			System.out.println("Hello from B");
		}
	}

	static class C extends D implements B, A {
		public static void main(String[] args) {
			new C().hello();
		}
	}

	static class D implements A {
	}
}