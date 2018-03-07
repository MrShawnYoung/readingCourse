package com.shawn.spring4.ch1.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.shawn.spring4.ch1.aop")
@EnableAspectJAutoProxy
public class AopConfig {

}