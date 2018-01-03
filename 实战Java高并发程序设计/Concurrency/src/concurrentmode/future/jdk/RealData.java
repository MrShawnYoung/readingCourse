package concurrentmode.future.jdk;

import java.util.concurrent.Callable;

/**
 * 真实对象
 * 
 * @author 杨弢
 * 
 */
public class RealData implements Callable<String> {
	private String para;

	public RealData(String para) {
		this.para = para;
	}

	@Override
	public String call() throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sb.append(para);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		return sb.toString();
	}
}