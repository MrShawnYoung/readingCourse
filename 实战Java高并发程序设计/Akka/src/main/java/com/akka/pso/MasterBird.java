package com.akka.pso;

import com.akka.pso.tool.GBestMsg;
import com.akka.pso.tool.PBestMsg;
import com.akka.pso.tool.PsoValue;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 粒子管理者
 * 
 * @author 杨弢
 * 
 */
public class MasterBird extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);
	private PsoValue gBest = null;

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof PBestMsg) {
			PsoValue pBest = ((PBestMsg) msg).getValue();
			if (gBest == null || gBest.getValue() < pBest.getValue()) {
				// 更新全局最优，通知所有粒子
				System.out.println(msg + "\n");
				gBest = pBest;
				ActorSelection selection = getContext().actorSelection(
						"/user/bird_*");
				selection.tell(new GBestMsg(gBest), getSelf());
			}
		} else {
			unhandled(msg);
		}
	}
}