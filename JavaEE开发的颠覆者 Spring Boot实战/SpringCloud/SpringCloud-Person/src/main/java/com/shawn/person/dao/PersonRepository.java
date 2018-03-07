package com.shawn.person.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shawn.person.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}