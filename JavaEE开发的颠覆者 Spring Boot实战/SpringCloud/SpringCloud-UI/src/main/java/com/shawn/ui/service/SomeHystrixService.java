package com.shawn.ui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class SomeHystrixService {
	@Autowired
	/* 使用Ribbon */
	RestTemplate restTemplate;

	/* 指定，调用失败后调用备用方法fallbackSome */
	@HystrixCommand(fallbackMethod = "fallbackSome")
	public String getSome() {
		return restTemplate.getForObject("http://some/getsome", String.class);
	}

	public String fallbackSome() {
		return "some service 模块故障";
	}
}