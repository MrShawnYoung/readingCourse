package com.shawn.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	/* 监听目的地，接收发送的消息 */
	@JmsListener(destination = "my-destination")
	public void receiveMessage(String message) {
		System.out.println("接受到：<" + message + ">");
	}
}