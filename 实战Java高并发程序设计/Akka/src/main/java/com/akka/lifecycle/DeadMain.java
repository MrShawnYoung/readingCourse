package com.akka.lifecycle;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

/**
 * 生命周期
 * 
 * @author 杨弢
 * 
 */
public class DeadMain {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("deadwatch",
				ConfigFactory.load("samplehello.conf"));
		ActorRef worker = system
				.actorOf(Props.create(MyWorker.class), "worker");
		// 第二个参数表示监视这个worker,也就是WatchActor的构造函数
		system.actorOf(Props.create(WatchActor.class, worker), "watcher");
		worker.tell(MyWorker.Msg.WORKING, ActorRef.noSender());
		worker.tell(MyWorker.Msg.DONE, ActorRef.noSender());
		worker.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}
}