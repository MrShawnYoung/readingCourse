package chapter14;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 创建你自己的延迟列表
 * 
 * @author Loops
 *
 */
public class LazyLists {

	public static void main(String[] args) {
		MyList<Integer> l = new MyLinkedList<>(5, new MyLinkedList<>(10, new Empty<>()));
		System.out.println(l.head());

		LazyList<Integer> numbers = from(2);
		int two = numbers.head();
		int three = numbers.tail().head();
		int four = numbers.tail().tail().head();
		System.out.println(two + " " + three + " " + four);

		numbers = from(2);
		int prime_two = primes(numbers).head();
		int prime_three = primes(numbers).tail().head();
		int prime_five = primes(numbers).tail().tail().head();
		System.out.println(prime_two + " " + prime_three + " " + prime_five);

		// printAll(primes(from(2)));
	}

	public interface MyList<T> {
		T head();

		MyList<T> tail();

		default boolean isEmpty() {
			return true;
		}

		MyList<T> filter(Predicate<T> p);
	}

	public static class MyLinkedList<T> implements MyList<T> {
		private final T head;
		private final MyList<T> tail;

		public MyLinkedList(T head, MyList<T> tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public T head() {
			return head;
		}

		@Override
		public MyList<T> tail() {
			return tail;
		}

		public boolean isEmpty() {
			return false;
		}

		@Override
		public MyList<T> filter(Predicate<T> p) {
			return isEmpty() ? this : p.test(head()) ? new MyLinkedList<>(head(), tail().filter(p)) : tail().filter(p);
		}
	}

	public static class Empty<T> implements MyList<T> {

		@Override
		public T head() {
			throw new UnsupportedOperationException();
		}

		@Override
		public MyList<T> tail() {
			throw new UnsupportedOperationException();
		}

		@Override
		public MyList<T> filter(Predicate<T> p) {
			return null;
		}
	}

	public static class LazyList<T> implements MyList<T> {
		final T head;
		final Supplier<MyList<T>> tail;

		public LazyList(T head, Supplier<MyList<T>> tail) {
			this.head = head;
			this.tail = tail;
		}

		@Override
		public T head() {
			return head;
		}

		@Override
		public MyList<T> tail() {
			/* 注意，与前面的head不同，这里tail使用了一个Supplier方法提供了延迟性 */
			return tail.get();
		}

		public boolean isEmpty() {
			return false;
		}

		@Override
		public MyList<T> filter(Predicate<T> p) {
			/* 你可以返回一个新的Empty<>()，不过这和返回一个空对象的效果是一样的 */
			return isEmpty() ? this
					: p.test(head()) ? new LazyList<>(head(), () -> tail().filter(p)) : tail().filter(p);
		}
	}

	public static LazyList<Integer> from(int n) {
		return new LazyList<Integer>(n, () -> from(n + 1));
	}

	public static MyList<Integer> primes(MyList<Integer> numbers) {
		return new LazyList<>(numbers.head(), () -> primes(numbers.tail().filter(n -> n % numbers.head() != 0)));
	}

	public static <T> void printAll(MyList<T> numbers) {
		if (numbers.isEmpty()) {
			return;
		}
		System.out.println(numbers.head());
		printAll(numbers.tail());
	}
}