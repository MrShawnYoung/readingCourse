package com.shawn.springcloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.springcloud.dto.User;
import com.shawn.springcloud.service.HelloServiceF;
import com.shawn.springcloud.service.RefactorHelloService;

@RestController
public class ConsumerController {
	@Autowired
	HelloServiceF helloService;

	@Autowired
	RefactorHelloService refactorHelloService;

	@RequestMapping(value = "/feign-consumer", method = RequestMethod.GET)
	public String helloConsumer() {
		return helloService.hello();
	}

	@RequestMapping(value = "/feign-consumer2", method = RequestMethod.GET)
	public String helloConsumer2() {
		StringBuilder sb = new StringBuilder();
		sb.append(helloService.hello()).append("\n");
		sb.append(helloService.hello("shawn")).append("\n");
		sb.append(helloService.hello("shawn", 26)).append("\n");
		sb.append(helloService.hello(new User("shawn", 26))).append("\n");
		return sb.toString();
	}

	@RequestMapping(value = "/feign-consumer3", method = RequestMethod.GET)
	public String helloConsumer3() {
		StringBuilder sb = new StringBuilder();
		sb.append(refactorHelloService.hello("Young")).append("\n");
		sb.append(refactorHelloService.hello("Young", 88)).append("\n");
		sb.append(refactorHelloService.hello(new User("Young", 88))).append("\n");
		return sb.toString();
	}
}