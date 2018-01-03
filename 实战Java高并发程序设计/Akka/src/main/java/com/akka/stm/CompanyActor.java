package com.akka.stm;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.japi.STM;
import akka.actor.UntypedActor;
import akka.transactor.Coordinated;

/**
 * 公司账户Actor
 * 
 * @author 杨弢
 * 
 */
public class CompanyActor extends UntypedActor {
	private Ref.View<Integer> count = STM.newRef(100);

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Coordinated) {
			// 如果是Coordinated表示新事务的开始
			final Coordinated c = (Coordinated) msg;
			// 获取转账金额
			final int downCount = (Integer) c.getMessage();
			// 把employee也加入当前事务
			STMDemo.employee.tell(c.coordinate(downCount), getSelf());
			try {
				// 原子执行块
				c.atomic(new Runnable() {
					public void run() {
						// 汇款额度>可用额度，宣告失败
						if (count.get() < downCount) {
							throw new RuntimeException("less than " + downCount);
						}
						// 调整余额
						STM.increment(count, -downCount);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("GetCount".equals(msg)) {
			// 返回余额
			getSender().tell(count.get(), getSelf());
		} else {
			unhandled(msg);
		}
	}
}