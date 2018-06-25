package com.shawn.springcloud.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.shawn.springcloud.dto.User;
import com.shawn.springcloud.fallback.HelloServiceFallback;
import com.shawn.springcloud.log.FullLogConfiguration;

/*指定服务名绑定服务*/
@FeignClient(name = "hello-service", configuration = FullLogConfiguration.class, fallback = HelloServiceFallback.class)
public interface HelloServiceF {
	@RequestMapping("/hello")
	String hello();

	@RequestMapping(value = "/hello1", method = RequestMethod.GET)
	String hello(@RequestParam("name") String name);

	@RequestMapping(value = "/hello2", method = RequestMethod.GET)
	User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age);

	@RequestMapping(value = "/hello3", method = RequestMethod.POST)
	String hello(@RequestBody User user);
}