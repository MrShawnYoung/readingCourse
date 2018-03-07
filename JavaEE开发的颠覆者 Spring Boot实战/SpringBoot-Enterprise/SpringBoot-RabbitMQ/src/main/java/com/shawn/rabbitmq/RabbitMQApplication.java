package com.shawn.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitMQApplication implements CommandLineRunner {
	@Autowired
	/* 自动配置 */
	RabbitTemplate rabbitemplate;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RabbitMQApplication.class, args);
	}

	@Bean
	/* 定义队列 */
	public Queue shawnQueue() {
		return new Queue("my-queue");
	}

	@Override
	public void run(String... arg0) throws Exception {
		/* 向队列发送消息 */
		rabbitemplate.convertAndSend("my-queue", "来自RabbitMQ的问候");
	}
}