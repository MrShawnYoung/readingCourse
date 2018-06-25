package com.shawn.springcloud.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
/* 队列hello的监听 */
@RabbitListener(queues = "hello")
public class Receiver {

	/* 处理消息的方法 */
	@RabbitHandler
	public void process(String hello) {
		System.out.println("Receiver：" + hello);
	}
}