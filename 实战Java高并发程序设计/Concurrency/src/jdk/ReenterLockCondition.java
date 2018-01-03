package jdk;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * condition功能
 * 
 * @author 杨弢
 * 
 */
public class ReenterLockCondition implements Runnable {
	public static ReentrantLock lock = new ReentrantLock();
	public static Condition condition = lock.newCondition();

	@Override
	public void run() {
		try {
			lock.lock();
			condition.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ReenterLockCondition tl = new ReenterLockCondition();
		Thread t1 = new Thread(tl);
		t1.start();
		Thread.sleep(2000);
		// 通知继续执行
		lock.lock();
		condition.signal();
		lock.unlock();
	}
}