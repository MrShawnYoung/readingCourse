package com.shawn.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/* 设置对"/", "login"路径不拦截，登录页面为login，登录成功后转向chat路径 */
		http.authorizeRequests().antMatchers("/", "login").permitAll().anyRequest().authenticated().and().formLogin()
				.loginPage("/login").defaultSuccessUrl("/chat").permitAll().and().logout().permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/* 分配admin和yangtao两个角色为USER的用户 */
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("USER").and().withUser("yangtao")
				.password("yangtao").roles("USER");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		/* 不拦截static路径 */
		web.ignoring().antMatchers("resources/static/**");
	}
}