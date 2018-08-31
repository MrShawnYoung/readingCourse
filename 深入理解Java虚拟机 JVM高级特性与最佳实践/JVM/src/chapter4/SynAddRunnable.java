package chapter4;

import java.util.Scanner;

/**
 * 线程死锁等待演示
 */
public class SynAddRunnable implements Runnable {
	int a, b;

	public SynAddRunnable(int a, int b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public void run() {
		synchronized (Integer.valueOf(a)) {
			synchronized (Integer.valueOf(b)) {
				System.out.println(a + b);
			}
		}
	}

	public static void main(String[] args) {
		// 这里为了等待输入后开始
		new Scanner(System.in).nextInt();
		for (int i = 0; i < 100; i++) {
			new Thread(new SynAddRunnable(1, 2)).start();
			new Thread(new SynAddRunnable(2, 1)).start();
		}
	}
}