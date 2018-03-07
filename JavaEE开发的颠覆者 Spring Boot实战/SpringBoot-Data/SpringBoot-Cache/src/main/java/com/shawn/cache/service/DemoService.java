package com.shawn.cache.service;

import com.shawn.cache.domain.Person;

public interface DemoService {
	public Person save(Person person);

	public void remove(Long id);

	public Person findOne(Person person);
}