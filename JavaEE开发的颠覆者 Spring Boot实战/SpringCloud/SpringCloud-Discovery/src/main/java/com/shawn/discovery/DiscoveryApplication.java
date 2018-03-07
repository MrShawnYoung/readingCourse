package com.shawn.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
/* 开启EurekaServer支持 */
@EnableEurekaServer
public class DiscoveryApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DiscoveryApplication.class, args);
	}
}