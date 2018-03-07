package com.shawn.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
/* 加载属性文件配置，指定属性前缀，绑定一个Bean */
@ConfigurationProperties(prefix = "author")
public class AuthorSettings {
	private String name;
	private Long age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
}