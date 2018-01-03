package com.akka.strategy;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Option;

/**
 * 子Actor
 * 
 * @author 杨弢
 * 
 */
public class RestartActor extends UntypedActor {
	public enum Msg {
		DONE, RESTART
	}

	/* 初始化(废弃) */
	@Deprecated
	public void perStart() {
		System.out.println("preStart hashcode:" + this.hashCode());
	}

	/* 初始化 */
	@Override
	public void preStart() throws Exception {
		System.out.println("preStart hashcode:" + this.hashCode());
	}

	/* 停止 */
	@Override
	public void postStop() {
		System.out.println("postStop hashcode:" + this.hashCode());
	}

	/* 重启完成后调用 */
	@Override
	public void postRestart(Throwable reason) throws Exception {
		super.postRestart(reason);
		System.out.println("postRestart hashcode:" + this.hashCode());
	}

	/* 重启(废弃) */
	@Deprecated
	public void preRestart(Throwable reason, Option opt) throws Exception {
		System.out.println("preRestart hashcode:" + this.hashCode());
	}

	/* 重启 */
	@Override
	public void preRestart(Throwable reason, scala.Option<Object> message)
			throws Exception {
		System.out.println("preRestart hashcode:" + this.hashCode());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Msg.DONE) {
			getContext().stop(getSelf());
		} else if (msg == Msg.RESTART) {
			System.out.println(((Object) null).toString());
			// 抛出异常 默认会被restart,但这里会resume
			double a = 0 / 0;
		}
		unhandled(msg);
	}

	public static void customStrategy(ActorSystem system) {
		ActorRef a = system.actorOf(Props.create(Supervisor.class),
				"Supervisor");
		a.tell(Props.create(RestartActor.class), ActorRef.noSender());
		ActorSelection sel = system
				.actorSelection("akka://lifecycle/user/Supervisor/restartActor");
		for (int i = 0; i < 100; i++) {
			sel.tell(RestartActor.Msg.RESTART, ActorRef.noSender());
		}
	}

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("lifecycle",
				ConfigFactory.load("lifecycle.conf"));
		customStrategy(system);
	}
}