package concurrentmode.assemblyline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 加法运算
 * 
 * @author 杨弢
 * 
 */
public class Plus implements Runnable {
	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();

	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();
				msg.j = msg.i + msg.j;
				Multiply.bq.add(msg);
			} catch (InterruptedException e) {

			}
		}
	}
}