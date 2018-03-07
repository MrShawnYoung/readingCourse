package com.shawn.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HTTPApplication {
	@Autowired
	StatusService statusService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(HTTPApplication.class, args);
	}

	/* 注册端点Bean */
	@Bean
	public Endpoint<String> status() {
		Endpoint<String> status = new StatusEndPoint();
		return status;
	}

	/* 定义控制器方法用来改变status */
	@RequestMapping("/change")
	public String changeStatus(String status) {
		statusService.setStatus(status);
		return "OK";
	}
}