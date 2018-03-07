package com.shawn.spring4.ch1.javaconfig;

import com.shawn.spring4.ch1.javaconfig.FunctionService;

public class UseFunctionService {
	FunctionService functionService;

	public void setFunctionService(FunctionService functionService) {
		this.functionService = functionService;
	}

	public String SayHello(String word) {
		return functionService.sayHello(word);
	}
}