package jdk;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁
 * 
 * @author 杨弢
 * 
 */
public class FairLock implements Runnable {
	public static ReentrantLock fairlock = new ReentrantLock(true);

	@Override
	public void run() {
		while (true) {
			try {
				fairlock.lock();
				System.out.println(Thread.currentThread().getName() + "获得锁");
			} finally {
				fairlock.unlock();
			}
		}
	}

	public static void main(String[] args) {
		FairLock r1 = new FairLock();
		Thread t1 = new Thread(r1, "T1");
		Thread t2 = new Thread(r1, "T2");
		t1.start();
		t2.start();
	}
}