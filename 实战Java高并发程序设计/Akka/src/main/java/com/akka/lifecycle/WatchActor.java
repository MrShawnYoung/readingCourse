package com.akka.lifecycle;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 监视者
 * 
 * @author 杨弢
 * 
 */
public class WatchActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public WatchActor(ActorRef ref) {
		// 监视一个actor
		getContext().watch(ref);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		// actor退出终止的话，会收到一个Terminated消息
		if (msg instanceof Terminated) {
			System.out.println(
					String.format("%s has terminated, shutting down system", ((Terminated) msg).getActor().path()));
			// 关闭系统
			getContext().system().shutdown();
		} else {
			unhandled(msg);
		}
	}
}