package com.shawn.ui.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.shawn.ui.domain.Person;

@Service
public class PersonHystrixService {
	@Autowired
	PersonService personService;

	/* 指定，调用失败后调用备用方法fallbackSave */
	@HystrixCommand(fallbackMethod = "fallbackSave")
	public List<Person> save(String name) {
		return personService.save(name);
	}

	public List<Person> fallbackSave() {
		List<Person> list = new ArrayList<Person>();
		Person p = new Person("Person Service 故障");
		list.add(p);
		return list;
	}
}