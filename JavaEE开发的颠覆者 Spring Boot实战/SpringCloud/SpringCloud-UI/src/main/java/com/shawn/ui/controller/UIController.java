package com.shawn.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shawn.ui.domain.Person;
import com.shawn.ui.service.PersonHystrixService;
import com.shawn.ui.service.SomeHystrixService;

public class UIController {
	@Autowired
	SomeHystrixService someHystrixService;

	@Autowired
	PersonHystrixService personHystrixService;

	@RequestMapping("/dispatch")
	public List<Person> sendMessage(@RequestBody String personName) {
		return personHystrixService.save(personName);
	}

	@RequestMapping(value = "/getsome", produces = { MediaType.TEXT_PLAIN_VALUE })
	public String getSome() {
		return someHystrixService.getSome();
	}
}