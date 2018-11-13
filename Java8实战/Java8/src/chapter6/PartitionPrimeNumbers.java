package chapter6;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collector.Characteristics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * 质数和非质数分区
 * 
 * @author Loops
 *
 */
public class PartitionPrimeNumbers {

	public static void main(String[] args) {
		System.out.println("Numbers partitioned in prime and non-prime: " + partitionPrimes(100));
		System.out.println("Numbers partitioned in prime and non-prime: " + partitionPrimesWithCustomCollector(100));
	}

	public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
		return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
	}

	public static boolean isPrime(int candidate) {
		return IntStream.rangeClosed(2, candidate - 1).limit((long) Math.floor(Math.sqrt((double) candidate)) - 1)
				.noneMatch(i -> candidate % i == 0);
	}

	public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
		return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
	}

	public static boolean isPrime(List<Integer> primes, Integer candidate) {
		double candidateRoot = Math.sqrt((double) candidate);
		return takeWhile(primes, i -> i <= candidateRoot).stream().noneMatch(i -> candidate % i == 0);
	}

	public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
		int i = 0;
		for (A item : list) {
			if (!p.test(item)) {
				return list.subList(0, i);
			}
			i++;
		}
		return list;
	}

	/**
	 * 自定义收集器
	 * 
	 * @author Loops
	 *
	 */
	public static class PrimeNumbersCollector
			implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

		/* 从一个有两个空List的Map开始收集过程 */
		@Override
		public Supplier<Map<Boolean, List<Integer>>> supplier() {
			return () -> new HashMap<Boolean, List<Integer>>() {
				{
					put(true, new ArrayList<Integer>());
					put(false, new ArrayList<Integer>());
				}
			};
		}

		/* 将已经找到质数列表传递给isPrime方法，根据isPrime方法的返回值，从Map中取质数或非质数列表，把当前的被测数加进去 */
		@Override
		public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
			return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
				acc.get(isPrime(acc.get(true), candidate)).add(candidate);
			};
		}

		/* 让收集器并行工作 */
		@Override
		public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
			return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
				/* 将第二个Map合并到第一个 */
				map1.get(true).addAll(map2.get(true));
				map1.get(false).addAll(map2.get(false));
				return map1;
			};
		}

		@Override
		public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
			/* 收集过程最后无需转换，因此用identity函数收尾 */
			return Function.identity();
		}

		@Override
		public Set<Collector.Characteristics> characteristics() {
			return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
		}
	}
}