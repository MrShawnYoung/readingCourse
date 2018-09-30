package jdk.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 寻找堆栈
 * 
 * @author 杨弢
 * 
 */
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

	public static class DivTask implements Runnable {
		int a, b;

		public DivTask(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public void run() {
			double re = a / b;
			System.out.println(re);
		}
	}

	public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	public void execute(Runnable task) {
		super.execute(wrap(task, ClientTrace(), Thread.currentThread().getName()));
	}

	@Override
	public Future<?> submit(Runnable task) {
		return super.submit(wrap(task, ClientTrace(), Thread.currentThread().getName()));
	}

	public Exception ClientTrace() {
		return new Exception("Client stack trace");
	}

	public Runnable wrap(final Runnable task, final Exception clientStask, String clientThreadName) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					clientStask.printStackTrace();
					throw e;
				}
			}
		};
	}

	public static void main(String[] args) {
		ThreadPoolExecutor pools = new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>());
		/**
		 * 错误堆栈中可以看到是在哪里提交的任务
		 */
		for (int i = 0; i < 5; i++) {
			pools.execute(new DivTask(100, i));
		}
	}
}