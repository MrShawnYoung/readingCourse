package com.shawn.springcloud.integration;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@EnableBinding(value = { SinkSender.SinkOutput.class })
public class SinkSender {
	private static Logger logger = LoggerFactory.getLogger(SinkSender.class);

	@Transformer(inputChannel = Sink.INPUT, outputChannel = Source.OUTPUT)
	public Object transform(Date message) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message);
	}

	@Bean
	// @InboundChannelAdapter(value = SinkOutput.OUTPUT, poller =
	// @Poller(fixedDelay = "2000"))
	@InboundChannelAdapter(value = Sink.INPUT, poller = @Poller(fixedDelay = "2000"))
	public MessageSource<String> timerMessageSource() {
		// return () -> new GenericMessage<>(new Date());
		return () -> new GenericMessage<>("{\"name\":\"shawn\",\"age\":30}");
	}

	public interface SinkOutput {
		String OUTPUT = "input";

		@Output(Source.OUTPUT)
		MessageChannel output();
	}
}