package chapter6;

import java.util.function.*;

/**
 * 比较收集器的性能
 * 
 * @author Loops
 *
 */
public class CollectorHarness {

	public static void main(String[] args) {
		// System.out.println("Partitioning done in: " +
		// execute(PartitionPrimeNumbers::partitionPrimes) + " msecs");
		System.out.println("Partitioning done in: " + execute(PartitionPrimeNumbers::partitionPrimesWithCustomCollector)
				+ " msecs");
	}

	private static long execute(Consumer<Integer> primePartitioner) {
		long fastest = Long.MAX_VALUE;
		/* 运行测试10次 */
		for (int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			/* 将一百万个自然数按质数和非质数分区 */
			primePartitioner.accept(1_000_000);
			/* 取运行时间的毫秒值 */
			long duration = (System.nanoTime() - start) / 1_000_000;
			/* 检查这个执行是否是最快的一个 */
			if (duration < fastest)
				fastest = duration;
			System.out.println("done in " + duration);
		}
		return fastest;
	}
}