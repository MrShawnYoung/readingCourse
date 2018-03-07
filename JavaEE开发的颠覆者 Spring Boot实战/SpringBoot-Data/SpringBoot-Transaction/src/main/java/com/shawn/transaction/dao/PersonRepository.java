package com.shawn.transaction.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shawn.transaction.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}