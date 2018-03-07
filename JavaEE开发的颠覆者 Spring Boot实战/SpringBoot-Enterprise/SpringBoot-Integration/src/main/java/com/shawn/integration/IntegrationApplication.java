package com.shawn.integration;

import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.scheduling.PollerMetadata;

import com.rometools.rome.feed.synd.SyndEntry;

@SpringBootApplication
public class IntegrationApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(IntegrationApplication.class, args);
	}

	/* 自动获得资源 */
	@Value("http://spring.io/blog.atom")
	Resource resource;

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	/* 配置默认的轮询方式 */
	public PollerMetadata poller() {
		return Pollers.fixedRate(500).get();
	}

	@Bean
	public FeedEntryMessageSource feedEntryMessageSource() throws IOException {
		/* 构造feed的入站通道适配器作为数据输入 */
		FeedEntryMessageSource messageSource = new FeedEntryMessageSource(resource.getURL(), "news");
		return messageSource;
	}

	@Bean
	public IntegrationFlow myFlow() throws IOException {
		/*
		 * from开始，通过route选择路由，消息体类型为SyndEntry，判断条件类型为String，判断值是通过payload获得的分类，
		 * 不同分类转向不同的分类通道，通过get获取实体
		 */
		return IntegrationFlows.from(feedEntryMessageSource()).<SyndEntry, String>route(
				payload -> payload.getCategories().get(0).getName(),
				mapping -> mapping.channelMapping("releases", "releasesChannel")
						.channelMapping("engineering", "engineeringChannel").channelMapping("news", "newsChannel"))
				.get();
	}

	@Bean
	public IntegrationFlow releasesFlow() {
		/*
		 * 开始获取数据，transform类型转换，payload类型为SyndEntry，转换为字符串，
		 * 用handle方法处理file的出站的适配器
		 */
		return IntegrationFlows.from(MessageChannels.queue("releasesChannel", 10))
				.<SyndEntry, String>transform(
						payload -> "《" + payload.getTitle() + "》" + payload.getLink() + getProperty("line.separator"))
				.handle(Files.outboundAdapter(new File("e:/springblog")).fileExistsMode(FileExistsMode.APPEND)
						.charset("UTF-8").fileNameGenerator(message -> "releases.txt").get())
				.get();
	}

	@Bean
	public IntegrationFlow engineeringFlow() {
		return IntegrationFlows.from(MessageChannels.queue("engineerChannel", 10))
				.<SyndEntry, String>transform(
						e -> "《" + e.getTitle() + "》" + e.getLink() + getProperty("line.separator"))
				.handle(Files.outboundAdapter(new File("e:/springblog")).fileExistsMode(FileExistsMode.APPEND)
						.charset("UTF-8").fileNameGenerator(message -> "engineering.txt").get())
				.get();
	}

	@Bean
	public IntegrationFlow newFlow() {
		/* 增加消息头的消息 */
		return IntegrationFlows.from(MessageChannels.queue("newChannel", 10))
				.<SyndEntry, String>transform(
						payload -> "《" + payload.getTitle() + "》" + payload.getLink() + getProperty("line.separator"))
				.enrichHeaders(Mail.headers().subject("来自Spring的新闻").to("chsewx@163.com").from("chsewx@163.com"))
				.handle(Mail.outboundAdapter("smtp.126.com").port(25).protocol("smtp")
						.credentials("chsewx@163.com", "******").javaMailProperties(p -> p.put("mail.debub", "false")),
						e -> e.id("smtpOut"))
				.get();
	}
}