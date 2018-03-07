package com.shawn.redis.dao;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.shawn.redis.domain.Person;

@Repository
public class PersonDao {
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Resource(name = "stringRedisTemplate")
	ValueOperations<String, String> valOpsStr;

	@Autowired
	RedisTemplate<Object, Object> redisTemplate;

	@Resource(name = "redisTemplate")
	ValueOperations<Object, Object> valOps;

	/* 存储字符串类型 */
	public void stringRedisTemplateDemo() {
		valOpsStr.set("xx", "yy");
	}

	/* 存储对象类型 */
	public void save(Person person) {
		valOps.set(person.getId(), person);
	}

	/* 获取字符串 */
	public String getString() {
		return valOpsStr.get("xx");
	}

	/* 获取对象 */
	public Person getPerson() {
		return (Person) valOps.get("1");
	}

}
