package com.shawn.transaction.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shawn.transaction.domain.Person;
import com.shawn.transaction.service.DemoService;

@RestController
public class MyController {
	@Autowired
	DemoService demoService;

	/* 回滚情况 */
	@RequestMapping("/rollback")
	public Person rollback(Person person) {
		return demoService.savePersonWithRollBack(person);
	}

	/* 不回滚情况 */
	@RequestMapping("/norollback")
	public Person noRollback(Person person) {
		return demoService.savePersonWithoutRollBack(person);
	}
}