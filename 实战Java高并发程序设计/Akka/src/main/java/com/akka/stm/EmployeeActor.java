package com.akka.stm;

import scala.concurrent.stm.Ref;
import scala.concurrent.stm.japi.STM;
import akka.actor.UntypedActor;
import akka.transactor.Coordinated;

/**
 * 雇员账户Actor
 * 
 * @author 杨弢
 * 
 */
public class EmployeeActor extends UntypedActor {
	// 初始金额50元
	private Ref.View<Integer> count = STM.newRef(50);

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Coordinated) {
			// 如果是Coordinated自动加入指定事务
			final Coordinated c = (Coordinated) msg;
			final int downCount = (Integer) c.getMessage();
			try {
				// 修改余额
				c.atomic(new Runnable() {
					public void run() {
						STM.increment(count, downCount);
					}
				});
			} catch (Exception e) {

			}
		} else if ("GetCount".equals(msg)) {
			getSender().tell(count.get(), getSelf());
		} else {
			unhandled(msg);
		}
	}
}