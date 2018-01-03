package com.java8;

import java.util.concurrent.locks.StampedLock;

/**
 * 点
 * 
 * @author 杨弢
 * 
 */
public class Point {
	private double x, y;
	private final StampedLock sl = new StampedLock();

	// 这是一个排他锁
	void move(double deltaX, double deltaY) {
		long stamp = sl.writeLock();
		try {
			x += deltaX;
			y += deltaY;
		} finally {
			sl.unlockWrite(stamp);
		}
	}

	// 只读方法
	double distanceFromOrigin() {
		// 尝试乐观读
		long stamp = sl.tryOptimisticRead();
		double currentX = x, currentY = y;
		// 判断在读取中是否被修改过
		if (!sl.validate(stamp)) {
			// 悲观读锁
			stamp = sl.readLock();
			try {
				currentX = x;
				currentY = y;
			} finally {
				sl.unlockRead(stamp);
			}
		}
		return Math.sqrt(currentX * currentY);
	}
}