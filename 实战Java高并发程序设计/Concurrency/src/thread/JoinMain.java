package thread;

/**
 * 加入
 * 
 * @author 杨弢
 * 
 */
public class JoinMain {
	public volatile static int i = 0;

	public static class AddThread extends Thread {
		@Override
		public void run() {
			for (i = 0; i < 1000000; i++)
				;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		AddThread add = new AddThread();
		add.start();
		add.join();
		System.out.println(i);
	}
}