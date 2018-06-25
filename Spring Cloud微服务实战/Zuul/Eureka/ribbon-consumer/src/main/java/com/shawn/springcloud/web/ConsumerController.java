package com.shawn.springcloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.shawn.springcloud.service.HelloService;

@RestController
public class ConsumerController {
	@Autowired
	// RestTemplate restTemplate;
	HelloService helloService;

	@RequestMapping(value = "/ribbon-consumer", method = RequestMethod.GET)
	public String helloConsumer() {
		// return restTemplate.getForEntity("http://HELLO-SERVICE/hello",
		// String.class).getBody();
		return helloService.helloService();
	}
}