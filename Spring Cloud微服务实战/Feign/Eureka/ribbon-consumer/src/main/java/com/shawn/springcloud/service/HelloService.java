package com.shawn.springcloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class HelloService {
	@Autowired
	RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "helloFallback", commandKey = "helloKey")
	public String helloService() {
		long start = System.currentTimeMillis();
		String result = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
		long end = System.currentTimeMillis();
		System.out.println("Speed time : " + (end - start));
		return result;
	}

	public String helloFallback() {
		return "error";
	}
}