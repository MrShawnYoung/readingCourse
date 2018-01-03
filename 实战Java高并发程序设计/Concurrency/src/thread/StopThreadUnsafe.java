package thread;

/**
 * stop()的慎用
 * 
 * @author 杨弢
 * 
 */
public class StopThreadUnsafe {
	private static User u = new User();

	/**
	 * 实体类
	 */
	private static class User {
		private int id;
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public User() {
			id = 0;
			name = "0";
		}

		public String toString() {
			return "User [id=" + id + ", name=" + name + "]";
		}

	}

	/**
	 * 写线程
	 */
	public static class ChangeObjectThread extends Thread {
		@Override
		public void run() {
			while (true) {
				synchronized (u) {
					int v = (int) (System.currentTimeMillis() / 1000);
					u.setId(v);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					u.setName(String.valueOf(v));
				}
				Thread.yield();
			}
		}
	}

	/**
	 * 读线程
	 */
	public static class ReaddObjectThread extends Thread {
		@Override
		public void run() {
			while (true) {
				synchronized (u) {
					if (u.getId() != Integer.parseInt(u.getName())) {
						System.out.println(u.toString());
					}
				}
				Thread.yield();
			}
		}

	}

	public static void main(String[] args) throws InterruptedException {
		new ReaddObjectThread().start();
		while (true) {
			Thread t = new ChangeObjectThread();
			t.start();
			Thread.sleep(150);
			t.stop();
		}
	}
}