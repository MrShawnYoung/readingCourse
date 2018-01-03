package com.akka.helloworld;

import akka.actor.UntypedActor;

/**
 * 欢迎者
 * 
 * @author 杨弢
 * 
 */
public class Greeter extends UntypedActor {
	public static enum Msg {
		GREET, DONE;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg == Msg.GREET) {
			System.out.println("Hello World!");
			// 给发送方发送DONE消息
			getSender().tell(Msg.DONE, getSelf());
		} else {
			unhandled(msg);
		}
	}
}