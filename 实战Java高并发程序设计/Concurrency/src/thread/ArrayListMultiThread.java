package thread;

import java.util.ArrayList;

/**
 * 并发下的ArrayList
 * 
 * @author 杨弢
 * 
 */
public class ArrayListMultiThread {
	static ArrayList<Integer> al = new ArrayList<Integer>(10);

	public static class AddThread implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 1000000; i++) {
				al.add(i);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new AddThread());
		Thread t2 = new Thread(new AddThread());
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(al.size());
	}
}