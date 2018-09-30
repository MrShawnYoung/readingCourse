package com.akka.future;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

/**
 * actor中的future
 * 
 * @author 杨弢
 * 
 */
public class AskMain {

	public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create("askdemo", ConfigFactory.load("samplehello.conf"));
		ActorRef worker = system.actorOf(Props.create(MyWorker.class), "worker");
		ActorRef printer = system.actorOf(Props.create(Printer.class), "printer");
		system.actorOf(Props.create(WatchActor.class, worker), "watcher");

		// 等待future返回
		Future<Object> f = ask(worker, 5, 1500);
		// 等待worker返回
		Object re = Await.result(f, Duration.create(6, TimeUnit.SECONDS));
		System.out.println("return:" + re.toString());

		// 直接导向其他Actor,pipe不会等待
		f = ask(worker, 6, 1500);
		// 不等待，重定向到printer
		pipe(f, system.dispatcher()).to(printer);
		worker.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}
}