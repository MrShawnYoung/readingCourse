package chapter13;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atomic变量自增运算测试
 */
public class AtomicTest {

	public static AtomicInteger race = new AtomicInteger();

	public static void increase() {
		race.incrementAndGet();
	}

	private static final int THREADS_COUNT = 20;

	public static void main(String[] args) {
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 10000; j++) {
						increase();
					}
				}
			});
			threads[i].start();
		}
		while (Thread.activeCount() > 1)
			Thread.yield();
		System.out.println(race);
	}
}