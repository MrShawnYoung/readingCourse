package com.akka.router;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

/**
 * 监视
 * 
 * @author 杨弢
 * 
 */
public class WatchActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);
	public Router router;
	{
		// 被路由的actor
		List<Routee> routees = new ArrayList<Routee>();
		for (int i = 0; i < 5; i++) {
			// Routee路由由5个worker的actor组成
			ActorRef worker = getContext().actorOf(
					Props.create(MyWorker.class), "worker_" + i);
			routees.add(new ActorRefRoutee(worker));
		}
		// 对所有的路由消息进行发送
		router = new Router(new RoundRobinRoutingLogic(), routees);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof MyWorker.Msg) {
			// 若有消息传递给这个5个actor,传递给router即可，根据给定的消息策略进行消息投递
			router.route(msg, getSender());
		} else if (msg instanceof Terminated) {
			// 停止工作就移除
			router = router.removeRoutee(((Terminated) msg).actor());
			System.out.println(((Terminated) msg).actor().path()
					+ " is closed, routee=" + router.routees().size());
			if (router.routees().size() == 0) {
				// 无可用actor直接关闭系统
				System.out.println("Close system");
				RouteMain.flag.send(false);
				getContext().system().shutdown();
			}
		} else {
			unhandled(msg);
		}
	}
}