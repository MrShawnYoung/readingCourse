package com.akka.future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 计算平方处理
 * 
 * @author 杨弢
 * 
 */
public class MyWorker extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public static enum Msg {
		WORKING, DONE, CLOSE;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			int i = (Integer) msg;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			// 计算平方并告诉请求者
			getSender().tell(i * i, getSelf());
		}
		if (msg == Msg.DONE) {
			log.info("Stop working");
		}
		if (msg == Msg.CLOSE) {
			log.info("I will shutdown");
			getSender().tell(Msg.CLOSE, getSelf());
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
}