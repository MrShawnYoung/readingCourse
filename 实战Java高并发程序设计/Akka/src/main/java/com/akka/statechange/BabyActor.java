package com.akka.statechange;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

/**
 * 婴儿Actor
 * 
 * @author 杨弢
 * 
 */
public class BabyActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);

	public static enum Msg {
		SLEEP, PLAY, CLOSE;
	}

	// 生气的状态
	Procedure<Object> angry = new Procedure<Object>() {

		public void apply(Object message) throws Exception {
			System.out.println("angryApply:" + message);
			if (message == Msg.SLEEP) {
				getSender().tell("I am already angry", getSelf());
				System.out.println("I am already angry");
			} else if (message == Msg.PLAY) {
				System.out.println("I like playing");
				getContext().become(happy);
			}
		}
	};

	// 开心的状态
	Procedure<Object> happy = new Procedure<Object>() {

		public void apply(Object message) throws Exception {
			System.out.println("happyApply:" + message);
			if (message == Msg.PLAY) {
				getSender().tell("I am already happy :-", getSelf());
				System.out.println("I am already happy :-");
			} else if (message == Msg.SLEEP) {
				System.out.println("I don't want to sleep");
				getContext().become(angry);
			}
		}
	};

	@Override
	public void onReceive(Object msg) throws Exception {
		System.out.println("onReceive:" + msg);
		// become切换状态
		if (msg == Msg.SLEEP) {
			getContext().become(angry);
		} else if (msg == Msg.PLAY) {
			getContext().become(happy);
		} else {
			unhandled(msg);
		}
	}
}