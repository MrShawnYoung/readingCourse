package thread;

/**
 * 线程组
 * 
 * @author 杨弢
 * 
 */
public class ThreadGroupName implements Runnable {
	@Override
	public void run() {
		String name = Thread.currentThread().getThreadGroup().getName() + "-" + Thread.currentThread().getName();
		while (true) {
			System.out.println("I am " + name);
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ThreadGroup tp = new ThreadGroup("PrintGroup");
		Thread t1 = new Thread(tp, new ThreadGroupName(), "T1");
		Thread t2 = new Thread(tp, new ThreadGroupName(), "T2");
		t1.start();
		t2.start();
		System.out.println(tp.activeCount());
		tp.list();
	}
}