package nolock.threadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadLocal案例
 * 
 * @author 杨弢
 * 
 */
public class ThreadLocalDemo {
	static ThreadLocal<SimpleDateFormat> tl = new ThreadLocal<SimpleDateFormat>();

	public static class ParseDate implements Runnable {
		int i = 0;

		public ParseDate(int i) {
			this.i = i;
		}

		@Override
		public void run() {
			try {
				if (tl.get() == null) {
					tl.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				}
				Date t = tl.get().parse("2015-03-29 19:29:" + i % 60);
				System.out.println(i + ":" + t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 1000; i++) {
			es.execute(new ParseDate(i));
		}
	}
}