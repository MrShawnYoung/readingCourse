package concurrentmode.disruptor;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者
 * 
 * @author 杨弢
 * 
 */
public class Consumer implements WorkHandler<PCData> {

	@Override
	public void onEvent(PCData event) throws Exception {
		System.out.println(Thread.currentThread().getId() + ":Event: --" + event.get() * event.get() + "--");
	}
}