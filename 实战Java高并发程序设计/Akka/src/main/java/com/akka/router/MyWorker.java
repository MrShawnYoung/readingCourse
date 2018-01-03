package com.akka.router;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 记录消息
 * 
 * @author 杨弢
 * 
 */
public class MyWorker extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);

	public static enum Msg {
		WORKING, DONE, CLOSE;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Msg.WORKING) {
			log.info("I am working");
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