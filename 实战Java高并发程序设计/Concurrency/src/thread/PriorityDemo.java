package thread;

/**
 * 优先级
 * 
 * @author 杨弢
 * 
 */
public class PriorityDemo {
	public static class HightPriority extends Thread {
		static int count = 0;

		@Override
		public void run() {
			while (true) {
				synchronized (PriorityDemo.class) {
					count++;
					if (count > 10000000) {
						System.out.println("HightPriority is complete");
						break;
					}
				}
			}
		}
	}

	public static class LowPriority extends Thread {
		static int count = 0;

		@Override
		public void run() {
			while (true) {
				synchronized (PriorityDemo.class) {
					count++;
					if (count > 10000000) {
						System.out.println("LowPriority is complete");
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		HightPriority h = new HightPriority();
		LowPriority l = new LowPriority();
		h.setPriority(Thread.MAX_PRIORITY);
		l.setPriority(Thread.MIN_PRIORITY);
		h.start();
		l.start();
	}
}