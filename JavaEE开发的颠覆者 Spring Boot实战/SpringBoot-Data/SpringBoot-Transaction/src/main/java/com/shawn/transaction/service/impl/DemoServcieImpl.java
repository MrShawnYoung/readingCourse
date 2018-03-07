package com.shawn.transaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shawn.transaction.dao.PersonRepository;
import com.shawn.transaction.domain.Person;
import com.shawn.transaction.service.DemoService;

@Service
public class DemoServcieImpl implements DemoService {
	@Autowired
	PersonRepository personRepostory;

	/* 特定异常时，数据将回滚 */
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public Person savePersonWithRollBack(Person person) {
		Person p = personRepostory.save(person);
		if (person.getName().contentEquals("shawn")) {
			throw new IllegalArgumentException("shawn已存在，数据将回滚");
		}
		return p;
	}

	/* 特定异常时，数据将回滚 */
	@Transactional(noRollbackFor = { IllegalArgumentException.class })
	public Person savePersonWithoutRollBack(Person person) {
		Person p = personRepostory.save(person);
		if (person.getName().contentEquals("shawn")) {
			throw new IllegalArgumentException("shawn虽已存在，数据将不会回滚");
		}
		return p;
	}
}