package com.shawn.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableEurekaClient
/* 开启feign客户端支持 */
@EnableFeignClients
/* 开启CircuitBreaker的支持 */
@EnableCircuitBreaker
/* 开启网关代理的支持 */
@EnableZuulProxy
public class UIApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(UIApplication.class, args);
	}
}