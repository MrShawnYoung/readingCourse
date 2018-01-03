package concurrentmode.sequence;

import java.util.concurrent.CountDownLatch;

/**
 * 并行希尔排序
 * 
 * @author 杨弢
 * 
 */
public class ShellSort {
	static int arr[];

	public static class ShellSortTask implements Runnable {
		int i = 0;
		int h = 0;
		CountDownLatch l;

		public ShellSortTask(int i, int h, CountDownLatch latch) {
			this.i = i;
			this.h = h;
			this.l = latch;
		}

		@Override
		public void run() {
			if (arr[i] < arr[i - h]) {
				int tmp = arr[i];
				int j = i - h;
				while (j >= 0 && arr[j] > tmp) {
					arr[j + h] = arr[j];
					j -= h;
				}
				arr[j + h] = tmp;
			}
			l.countDown();
		}
	}

	public static void pShellSort(int[] arr) throws InterruptedException {
		// 计算出最大的h值
		int h = 1;
		CountDownLatch latch = null;
		while (h <= arr.length / 3) {
			h = h * 3 + 1;
		}
		while (h > 0) {
			System.out.println("h=" + h);
			if (h >= 4) {
				latch = new CountDownLatch(arr.length - h);
			}
			for (int i = h; i < arr.length; i++) {
				// 控制线程数量
				if (h >= 4) {
					// pool.execute(new ShellSortTask(i, h, latch));
				} else {
					if (arr[i] < arr[i - h]) {
						int tmp = arr[i];
						int j = i - h;
						while (j >= 0 && arr[j] > tmp) {
							arr[j + h] = arr[j];
							j -= h;
						}
						arr[j + h] = tmp;
					}
					// System.out.println(Arrays.toString(arr));
				}
			}
			latch.await();
			// 计算出下一个h值
			h = (h - 1) / 3;
		}
	}
}