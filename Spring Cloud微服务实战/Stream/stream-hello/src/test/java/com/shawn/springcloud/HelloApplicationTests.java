package com.shawn.springcloud;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.shawn.springcloud.stream.SinkSender;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HelloApplication.class)
@WebAppConfiguration
public class HelloApplicationTests {

	@Autowired
	private SinkSender sinkSender;

	@Autowired
	MessageChannel input;

	@Test
	public void contextLoads() {
		// sinkSender.output().send(MessageBuilder.withPayload("From SinkSender").build());
		input.send(MessageBuilder.withPayload("From SinkSender").build());
	}
}