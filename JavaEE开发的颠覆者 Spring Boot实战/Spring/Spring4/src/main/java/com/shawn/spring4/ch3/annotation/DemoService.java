package com.shawn.spring4.ch3.annotation;

import org.springframework.stereotype.Service;

@Service
public class DemoService {
	public void outputResult() {
		System.out.println("从组合直接配置照样获得的bean");
	}
}