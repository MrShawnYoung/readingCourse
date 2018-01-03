package concurrentmode.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 数据工厂
 * 
 * @author 杨弢
 * 
 */
public class PCDataFactory implements EventFactory<PCData> {

	@Override
	public PCData newInstance() {
		return new PCData();
	}
}