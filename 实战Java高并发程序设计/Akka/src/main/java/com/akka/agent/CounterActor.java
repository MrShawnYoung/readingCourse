package com.akka.agent;

import scala.concurrent.Future;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;

/**
 * 累加Actor
 * 
 * @author 杨弢
 * 
 */
public class CounterActor extends UntypedActor {
	// 累加动作action
	Mapper<Integer, Integer> addMapper = new Mapper<Integer, Integer>() {

		@Override
		public Integer apply(Integer i) {
			return i + 1;
		}
	};

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			for (int i = 0; i < 10000; i++) {
				// 我希望能够知道future何时结束
				Future<Integer> f = AgentDemo.counterAgent.alter(addMapper);
				// 对象收集
				AgentDemo.futures.add(f);
			}
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
}
