package com.shawn.springcloud.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;

public interface SinkSender {

	/* 这里是个坑 */
	@Output(Source.OUTPUT)
	MessageChannel output();
}