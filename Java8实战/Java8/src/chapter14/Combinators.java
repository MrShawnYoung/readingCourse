package chapter14;

import java.util.function.Function;

/**
 * 结合器
 * 
 * @author Loops
 *
 */
public class Combinators {

	public static void main(String[] args) {
		System.out.println(repeat(3, (Integer x) -> 2 * x).apply(10));
	}

	public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
		return x -> g.apply(f.apply(x));
	}

	public static <A> Function<A, A> repeat(int n, Function<A, A> f) {
		/* 如果n的值为0，直接返回“什么也不做”的标识符 */
		/* 否则执行函数f，重复执行n-1次，紧接着再执行一次 */
		return n == 0 ? x -> x : compose(f, repeat(n - 1, f));
	}
}