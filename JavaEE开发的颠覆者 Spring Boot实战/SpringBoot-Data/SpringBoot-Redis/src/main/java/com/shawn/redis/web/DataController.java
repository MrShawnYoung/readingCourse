package com.shawn.redis.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.redis.dao.PersonDao;
import com.shawn.redis.domain.Person;

@RestController
public class DataController {
	@Autowired
	PersonDao personDao;

	/* 设置字符及对象 */
	@RequestMapping("/set")
	public void set() {
		Person person = new Person("1", "shawn", 26);
		personDao.save(person);
		personDao.stringRedisTemplateDemo();
	}

	/* 获取字符 */
	@RequestMapping("/getStr")
	public String getStr() {
		return personDao.getString();
	}

	/* 获取对象 */
	@RequestMapping("/get")
	public Person getPerson() {
		return personDao.getPerson();
	}
}