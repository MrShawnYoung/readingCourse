package com.shawn.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shawn.test.dao.PersonRepository;
import com.shawn.test.domain.Person;

@RunWith(SpringJUnit4ClassRunner.class)
/* 配置SpringBoot的ApplicationContext */
@SpringBootTest(classes = TestApplicationtests.class)
@WebAppConfiguration
/* 确保测试后的数据会回滚 */
@Transactional
public class TestApplicationtests {
	@Autowired
	PersonRepository personRepository;

	MockMvc mvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	String expectedJson;

	/* 初始化工作 */
	@Before
	public void setUp() throws JsonProcessingException {
		Person p1 = new Person("yt");
		Person p2 = new Person("shawn");
		personRepository.save(p1);
		personRepository.save(p2);
		/* 获得期望的Json字符串 */
		expectedJson = Obj2Json(personRepository.findAll());
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	/* 转换方法 */
	protected String Obj2Json(List<Person> obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

	@Test
	public void testPersonController() throws Exception {
		String uri = "/person";
		/* 获得一个request的执行结果 */
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
		/* 获得一个执行结果状态 */
		int status = result.getResponse().getStatus();
		/* 获得一个执行结果内容 */
		String content = result.getResponse().getContentAsString();
		/* 预期状态和实际状态比较 */
		Assert.assertEquals("错误，正确的返回值为200", 200, status);
		/* 预期字符串和返回字符串比较 */
		Assert.assertEquals("错误，返回值和预期返回值不一致", expectedJson, content);
	}
}