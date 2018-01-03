package concurrentmode.sequence;

import java.util.concurrent.CountDownLatch;

/**
 * 并行奇偶交换排序
 * 
 * @author 杨弢
 * 
 */
public class OddEvenSort {
	static int arr[];
	static int exchFlag = 1;

	static synchronized void setExchFlag(int v) {
		exchFlag = v;
	}

	static synchronized int getExchFlag() {
		return exchFlag;
	}

	public static class OddEvenSortTask implements Runnable {
		int i;
		CountDownLatch latch;

		public OddEvenSortTask(int i, CountDownLatch latch) {
			this.i = i;
			this.latch = latch;
		}

		@Override
		public void run() {
			if (arr[i] > arr[i + 1]) {
				int temp = arr[i];
				arr[i] = arr[i + 1];
				arr[i + 1] = temp;
				setExchFlag(1);
			}
			latch.countDown();
		}
	}

	public static void pOddEvenSort(int[] arr) throws InterruptedException {
		int start = 0;
		while (getExchFlag() == 1 || start == 1) {
			setExchFlag(0);
			// 偶数的数组长度，当start为1时，只有len/2-1个线程
			CountDownLatch latch = new CountDownLatch(arr.length / 2
					- (arr.length % 2 == 0 ? start : 0));
			for (int i = start; i < arr.length - 1; i += 2) {
				// pool.submit(new OddEvenSortTask(i, latch));
			}
			// 等待所有线程结束
			latch.wait();
			if (start == 0) {
				start = 1;
			} else {
				start = 0;
			}
		}
	}
}
