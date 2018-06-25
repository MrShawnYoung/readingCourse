package com.shawn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;

/*启用Turbine Stream配置*/
@EnableTurbineStream
@EnableDiscoveryClient
@SpringBootApplication
public class TurbineApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TurbineApplication.class, args);
	}
}