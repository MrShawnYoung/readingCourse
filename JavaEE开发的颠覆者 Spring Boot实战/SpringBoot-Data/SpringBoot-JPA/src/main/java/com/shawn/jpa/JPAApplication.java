package com.shawn.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.shawn.jpa.dao.PersonRepository;
import com.shawn.jpa.support.CustomRepositoryFactoryBean;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class)
public class JPAApplication {
	@Autowired
	PersonRepository personRepository;

	public static void main(String[] args) {
		SpringApplication.run(JPAApplication.class, args);
	}
}