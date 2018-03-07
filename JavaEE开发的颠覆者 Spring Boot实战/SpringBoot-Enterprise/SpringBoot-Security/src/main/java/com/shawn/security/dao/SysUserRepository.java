package com.shawn.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shawn.security.domain.SysUser;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {
	SysUser findByUsername(String username);
}