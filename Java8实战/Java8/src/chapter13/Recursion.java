package chapter13;

import java.util.stream.LongStream;

/**
 * 递归和迭代
 * 
 * @author Loops
 *
 */
public class Recursion {

	public static void main(String[] args) {
		System.out.println(factorialIterative(5));
		System.out.println(factorialRecursive(5));
		System.out.println(factorialStreams(5));
		System.out.println(factorialTailRecursive(5));
	}

	/* 迭代式的阶乘计算 */
	public static int factorialIterative(int n) {
		int r = 1;
		for (int i = 1; i <= n; i++) {
			r *= i;
		}
		return r;
	}

	/* 递归式的阶乘计算 */
	public static long factorialRecursive(long n) {
		return n == 1 ? 1 : n * factorialRecursive(n - 1);
	}

	/* 基于Stream的阶乘 */
	public static long factorialStreams(long n) {
		return LongStream.rangeClosed(1, n).reduce(1, (long a, long b) -> a * b);
	}

	/* 基于“尾—递”的阶乘 */
	public static long factorialTailRecursive(long n) {
		return factorialHelper(1, n);
	}

	public static long factorialHelper(long acc, long n) {
		return n == 1 ? acc : factorialHelper(acc * n, n - 1);
	}
}