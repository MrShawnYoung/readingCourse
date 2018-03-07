package com.shawn.cache.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shawn.cache.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}