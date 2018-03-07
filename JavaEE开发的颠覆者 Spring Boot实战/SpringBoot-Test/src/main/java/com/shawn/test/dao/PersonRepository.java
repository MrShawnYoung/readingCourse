package com.shawn.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shawn.test.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}