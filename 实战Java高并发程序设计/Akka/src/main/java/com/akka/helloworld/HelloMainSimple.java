package com.akka.helloworld;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class HelloMainSimple {
	public static void main(String[] args) {
		// 创建和管理Actor的系统(一个足够)，Hello为系统名
		ActorSystem system = ActorSystem.create("Hello", ConfigFactory.load("samplehello.conf"));
		// 创建顶级Actor
		ActorRef a = system.actorOf(Props.create(HelloWorld.class), "helloWorld");
		System.out.println("HelloWorld Actor Path:" + a.path());
	}
}