package com.akka.helloworld;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * HelloWorld的Actor
 * 
 * @author 杨弢
 * 
 */
public class HelloWorld extends UntypedActor {
	ActorRef greeter;

	// Akka回调方法，完成初始化工作
	@Override
	public void preStart() {
		// 创建Greeter实例
		greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
		System.out.println("Greeter Actor Path:" + greeter.path());
		// 向Greeter发送GREET消息，getSelf表示Greeter是HelloWorld的子Actor
		greeter.tell(Greeter.Msg.GREET, getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Greeter.Msg.DONE) {
			greeter.tell(Greeter.Msg.GREET, getSelf());
			// 将自己停止
			getContext().stop(getSelf());
		} else {
			unhandled(msg);
		}
	}
}