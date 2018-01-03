package jdk;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁
 * 
 * @author 杨弢
 * 
 */
public class ReenterLock implements Runnable {
	public static ReentrantLock lock = new ReentrantLock();
	public static int i = 0;

	@Override
	public void run() {
		for (int j = 0; j < 10000000; j++) {
			lock.lock();
			try {
				i++;
			} finally {
				lock.unlock();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ReenterLock tl = new ReenterLock();
		Thread t1 = new Thread(tl);
		Thread t2 = new Thread(tl);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(i);
	}
}