package concurrentmode.disruptor;

/**
 * 数据代表
 * 
 * @author 杨弢
 * 
 */
public class PCData {
	private long value;

	public long get() {
		return value;
	}

	public void set(long value) {
		this.value = value;
	}
}