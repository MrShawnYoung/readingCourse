package com.shawn.some;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SomeApplication {
	/* 注入的值来自于Config Server */
	@Value("${my.message}")
	private String message;

	@RequestMapping(value = "/getsome")
	public String getSome() {
		return message;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SomeApplication.class, args);
	}
}