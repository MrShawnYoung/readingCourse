package com.akka.lifecycle;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 带有生命周期的回调函数Actor
 * 
 * @author 杨弢
 * 
 */
public class MyWorker extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public static enum Msg {
		WORKING, DONE, CLOSE;
	}

	/* 初始化 */
	@Override
	public void preStart() {
		System.out.println("MyWorker is starting");
	}

	/* 停止 */
	@Override
	public void postStop() {
		System.out.println("MyWorker is stopping");
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Msg.WORKING) {
			System.out.println("I am working");
		}
		if (msg == Msg.DONE) {
			System.out.println("Stop working");
		}
		if (msg == Msg.CLOSE) {
			System.out.println("I will shutdown");
			getSender().tell(Msg.CLOSE, getSelf());
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
}