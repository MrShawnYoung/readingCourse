package com.shawn.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
/* 开启配置服务器支持 */
@EnableConfigServer
/* 开启作为Eureka Server的客户端支持 */
@EnableEurekaClient
public class ConfigApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConfigApplication.class, args);
	}
}