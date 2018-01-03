package concurrentmode.future;

/**
 * 真实数据
 * 
 * @author 杨弢
 * 
 */
public class RealData implements Data {
	protected final String result;

	public RealData(String para) {
		// RealData的构造可能很慢，需要用户等待很久，这里使用sleep模拟
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sb.append(para);
			try {
				// 这里使用sleep,代替一个很慢的操作过程
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}
		result = sb.toString();
	}

	@Override
	public String getResult() {
		return result;
	}
}