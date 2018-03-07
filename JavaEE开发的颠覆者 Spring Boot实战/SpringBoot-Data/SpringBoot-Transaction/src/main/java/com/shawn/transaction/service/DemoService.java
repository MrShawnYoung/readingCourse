package com.shawn.transaction.service;

import com.shawn.transaction.domain.Person;

public interface DemoService {
	public Person savePersonWithRollBack(Person person);

	public Person savePersonWithoutRollBack(Person person);
}