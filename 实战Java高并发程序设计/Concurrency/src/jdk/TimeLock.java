package jdk;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 等待限时
 * 
 * @author 杨弢
 * 
 */
public class TimeLock implements Runnable {
	public static ReentrantLock lock = new ReentrantLock();

	@Override
	public void run() {
		try {
			if (lock.tryLock(5, TimeUnit.SECONDS)) {
				Thread.sleep(6000);
			} else {
				System.out.println("get lock failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	public static void main(String[] args) {
		TimeLock tl = new TimeLock();
		Thread t1 = new Thread(tl);
		Thread t2 = new Thread(tl);
		t1.start();
		t2.start();
	}
}