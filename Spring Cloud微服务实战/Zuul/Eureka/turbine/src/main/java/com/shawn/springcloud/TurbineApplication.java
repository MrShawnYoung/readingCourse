package com.shawn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/*开启Turbine*/
@EnableTurbine
@EnableDiscoveryClient
@SpringBootApplication
public class TurbineApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TurbineApplication.class, args);
	}
}