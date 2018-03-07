package com.shawn.cache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.shawn.cache.dao.PersonRepository;
import com.shawn.cache.domain.Person;
import com.shawn.cache.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {
	@Autowired
	PersonRepository personReopsitory;

	@Override
	/* 缓存名称为people，key为person的id */
	@CachePut(value = "people", key = "#person.id")
	public Person save(Person person) {
		Person p = personReopsitory.save(person);
		System.out.println("为id、key为：" + p.getId() + "数据做了缓存");
		return p;
	}

	@Override
	/* 从缓存中删除key为id的数据 */
	@CacheEvict(value = "people")
	public void remove(Long id) {
		System.out.println("删除了id、key为" + id + "的数据缓存");
		personReopsitory.delete(id);
	}

	@Override
	/* key为person的id数据到缓存people中 */
	@Cacheable(value = "people", key = "#person.id")
	public Person findOne(Person person) {
		Person p = personReopsitory.findOne(person.getId());
		System.out.println("为id、key为：" + p.getId() + "数据做了缓存");
		return p;
	}

}
