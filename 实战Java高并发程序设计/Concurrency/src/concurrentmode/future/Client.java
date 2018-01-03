package concurrentmode.future;

/**
 * 客户端程序
 * 
 * @author 杨弢
 * 
 */
public class Client {
	public Data request(final String query) {
		final FutureData future = new FutureData();
		new Thread() {
			// RealData的构建很慢，所以在单独的线程中进行
			public void run() {
				RealData realdata = new RealData(query);
				future.setRealData(realdata);
			}
		}.start();
		// Future会被立即返回
		return future;
	}
}