package com.shawn.springcloud.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableBinding(value = { Sink.class })
public class SinkReceiver {

	private static Logger logger = LoggerFactory.getLogger(SinkReceiver.class);

	@ServiceActivator(inputChannel = Sink.INPUT)
	// @StreamListener(Sink.INPUT)用这个注解的话，删除下方transform方法
	public void receive(User user) {
		logger.info("Received：" + user);
	}

	@Transformer(inputChannel = Sink.INPUT, outputChannel = Source.OUTPUT)
	public Object transform(String message) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		User user = objectMapper.readValue(message, User.class);
		return user;
	}
}