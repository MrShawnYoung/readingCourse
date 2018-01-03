package thread;

/**
 * 等待和通知
 * 
 * @author 杨弢
 * 
 */
public class SimpleWN {
	final static Object obj = new Object();

	public static class T1 extends Thread {
		@Override
		public void run() {
			synchronized (obj) {
				System.out.println(System.currentTimeMillis() + ":T1 start!");
				try {
					System.out.println(System.currentTimeMillis()
							+ ":T1 wait for object!");
					obj.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(System.currentTimeMillis() + ":T1 end!");
			}
		}
	}

	public static class T2 extends Thread {
		@Override
		public void run() {
			synchronized (obj) {
				System.out.println(System.currentTimeMillis()
						+ ":T2 start! notify one thread");
				obj.notify();
				System.out.println(System.currentTimeMillis() + ":T2 end!");
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		Thread T1 = new T1();
		Thread T2 = new T2();
		T1.start();
		T2.start();
	}
}