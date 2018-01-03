package thread;

/**
 * 同步
 * 
 * @author 杨弢
 * 
 */
public class AccountSync implements Runnable {
	static AccountSync instance = new AccountSync();
	static int i = 0;

	public synchronized void increase() {
		i++;
	}

	@Override
	public void run() {
		for (int j = 0; j < 10000000; j++) {
			increase();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(instance);
		Thread t2 = new Thread(instance);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(i);
	}
}