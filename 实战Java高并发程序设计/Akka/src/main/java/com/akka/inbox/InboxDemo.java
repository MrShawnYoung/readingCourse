package com.akka.inbox;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;

/**
 * 消息收件箱
 * 
 * @author 杨弢
 * 
 */
public class InboxDemo {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("inboxdemo",
				ConfigFactory.load("samplehello.conf"));
		ActorRef worker = system
				.actorOf(Props.create(MyWorker.class), "worker");
		// 邮箱绑定system
		Inbox inbox = Inbox.create(system);
		// 监视worker，并发送消息
		inbox.watch(worker);
		inbox.send(worker, MyWorker.Msg.WORKING);
		inbox.send(worker, MyWorker.Msg.DONE);
		inbox.send(worker, MyWorker.Msg.CLOSE);
		while (true) {
			// 接收消息
			Object msg = inbox.receive(Duration.create(1, TimeUnit.SECONDS));
			if (msg == MyWorker.Msg.CLOSE) {
				System.out.println("My worker is Closing");
			} else if (msg instanceof Terminated) {
				// 停止消息关闭系统
				System.out.println("My worker is dead");
				system.shutdown();
				break;
			} else {
				System.out.println(msg);
			}
		}
	}
}