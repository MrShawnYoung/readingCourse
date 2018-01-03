package com.akka.pso;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Akka实现粒子群算法
 * 
 * @author 杨弢
 * 
 */
public class PSOMain {
	public static final int BIRD_COUNT = 100000;

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("psoSystem",
				ConfigFactory.load("samplehello.conf"));
		system.actorOf(Props.create(MasterBird.class), "masterbird");
		for (int i = 0; i < BIRD_COUNT; i++) {
			system.actorOf(Props.create(Bird.class), "bird" + i);
		}
	}
}