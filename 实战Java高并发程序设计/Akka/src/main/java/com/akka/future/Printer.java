package com.akka.future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 打印输出数据
 * 
 * @author 杨弢
 * 
 */
public class Printer extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public static enum Msg {
		WORKING, DONE, CLOSE;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			// 这里通过pipe的方法得到worker的输出结果
			System.out.println("Printer:" + msg);
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