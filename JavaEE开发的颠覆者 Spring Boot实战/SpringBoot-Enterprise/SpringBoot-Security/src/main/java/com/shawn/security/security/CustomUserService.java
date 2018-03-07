package com.shawn.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shawn.security.dao.SysUserRepository;
import com.shawn.security.domain.SysUser;

public class CustomUserService implements UserDetailsService {

	@Autowired
	SysUserRepository userRepository;

	@Override
	/* 获取用户 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在");
		}
		/* 返回给Spring Security使用 */
		return user;
	}
}