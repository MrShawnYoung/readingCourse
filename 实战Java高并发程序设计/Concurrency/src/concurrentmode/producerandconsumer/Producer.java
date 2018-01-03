package concurrentmode.producerandconsumer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者线程
 * 
 * @author 杨弢
 * 
 */
public class Producer implements Runnable {
	private volatile boolean isRunning = true;
	// 内存缓冲区
	private BlockingQueue<PCData> queue;
	// 总数，原子操作
	private static AtomicInteger count = new AtomicInteger();
	private static final int SLEEPTIME = 1000;

	public Producer(BlockingQueue<PCData> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		PCData data = null;
		Random r = new Random();
		System.out.println("start producer id="
				+ Thread.currentThread().getId());
		try {
			while (isRunning) {
				Thread.sleep(r.nextInt(SLEEPTIME));
				// 构造任务数据
				data = new PCData(count.incrementAndGet());
				System.out.println(data + " is put into queue");
				// 提交数据到缓冲区中
				if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
					System.err.println("failed to put data: " + data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	public void stop() {
		isRunning = false;
	}
}
