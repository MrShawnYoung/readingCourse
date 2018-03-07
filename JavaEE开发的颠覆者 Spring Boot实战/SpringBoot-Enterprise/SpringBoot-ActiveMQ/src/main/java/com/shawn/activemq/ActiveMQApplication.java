package com.shawn.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
/* 程序启动后执行的代码(run方法) */
public class ActiveMQApplication implements CommandLineRunner {
	@Autowired
	/* 注入Bean */
	JmsTemplate jmsTemplate;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ActiveMQApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		/* 向my-destination目的地发送Msg消息 */
		jmsTemplate.send("my-destination", new Msg());
	}
}