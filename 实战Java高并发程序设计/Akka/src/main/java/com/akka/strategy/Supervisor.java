package com.akka.strategy;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

/**
 * 监督策略
 * 
 * @author 杨弢
 * 
 */
public class Supervisor extends UntypedActor {
	// 运行actor遇到错误，1分钟内3次重试，超过直接杀死actor
	private static SupervisorStrategy strategy = new OneForOneStrategy(3,
			Duration.create(1, TimeUnit.MINUTES),
			new Function<Throwable, Directive>() {
				public Directive apply(Throwable t) throws Exception {
					if (t instanceof ArithmeticException) {
						System.out
								.println("meet ArithmeticException,just resume");
						// 继续指定这个actor,不做处理
						return SupervisorStrategy.resume();
					} else if (t instanceof NullPointerException) {
						System.out.println("meet NullPointerException,restart");
						// 重启
						return SupervisorStrategy.restart();
					} else if (t instanceof IllegalArgumentException) {
						// 停止
						return SupervisorStrategy.stop();
					} else {
						// 由顶层处理
						return SupervisorStrategy.escalate();
					}
				}
			});

	// 父类
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public void onReceive(Object o) throws Exception {
		if (o instanceof Props) {
			// 重启actor,当前的子actor
			getContext().actorOf((Props) o, "restartActor");
		} else {
			unhandled(o);
		}
	}
}