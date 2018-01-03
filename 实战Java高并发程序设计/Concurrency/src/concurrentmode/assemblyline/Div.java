package concurrentmode.assemblyline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 除法运算
 * 
 * @author 杨弢
 * 
 */
public class Div implements Runnable {
	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();

	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();
				msg.i = msg.i / 2;
				System.out.println(msg.orgStr + "=" + msg.i);
			} catch (InterruptedException e) {

			}
		}
	}
}