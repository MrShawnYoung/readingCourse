package thread;

/**
 * 守护线程
 * 
 * @author 杨弢
 * 
 */
public class Daemon {
	public static class DaemonT extends Thread {
		@Override
		public void run() {
			while (true) {
				System.out.println("I am alive");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t = new DaemonT();
		t.setDaemon(true);
		t.start();
		Thread.sleep(2000);
	}
}