package com.shawn.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	/* 监听目的地，接收发送的消息 */
	@RabbitListener(queues = "my-queue")
	public void receiveMessage(String message) {
		System.out.println("Received：<" + message + ">");
	}
}