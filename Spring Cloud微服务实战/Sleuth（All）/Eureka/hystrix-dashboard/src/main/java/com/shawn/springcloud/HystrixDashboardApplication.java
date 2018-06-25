package com.shawn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/*启用Hystrix Dashboard功能*/
@EnableHystrixDashboard
@SpringBootApplication
public class HystrixDashboardApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(HystrixDashboardApplication.class, args);
	}
}