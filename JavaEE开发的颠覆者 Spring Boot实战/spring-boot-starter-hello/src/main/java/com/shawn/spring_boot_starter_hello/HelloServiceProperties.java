package com.shawn.spring_boot_starter_hello;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*信息安全的属性获取*/
@ConfigurationProperties(prefix = "hello")
public class HelloServiceProperties {
	private static final String MSG = "world";

	private String msg = MSG;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}