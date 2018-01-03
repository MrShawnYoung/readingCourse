package concurrentmode.future;

/**
 * 订单数据
 * 
 * @author 杨弢
 * 
 */
public class FutureData implements Data {
	// FutureData是RealData的包装
	protected RealData realdata = null;
	protected boolean isReady = false;

	public synchronized void setRealData(RealData realdata) {
		if (isReady) {
			return;
		}
		this.realdata = realdata;
		isReady = true;
		// RealData已经被注入，通知getResult()
		notifyAll();
	}

	@Override
	// 会等待RealData构造完成
	public synchronized String getResult() {
		while (!isReady) {
			try {
				// 一直等待，知道RealData被注入
				wait();
			} catch (InterruptedException e) {

			}
		}
		// 由RealData实现
		return realdata.result;
	}
}