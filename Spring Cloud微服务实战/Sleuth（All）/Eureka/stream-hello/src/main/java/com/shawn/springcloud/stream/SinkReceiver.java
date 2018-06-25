package com.shawn.springcloud.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;

import com.shawn.springcloud.HelloApplication;

/*绑定消息通道*/
@EnableBinding(value = { Sink.class, SinkSender.class })
public class SinkReceiver {

	// private static Logger logger =
	// LoggerFactory.getLogger(HelloApplication.class);
	private static Logger logger = LoggerFactory.getLogger(SinkReceiver.class);

	/* 注册为消息中间件上数据流的事件监听器 */
	@StreamListener(Sink.INPUT)
	public void receive(Object payload) {
		logger.info("Received：" + payload);
	}
}