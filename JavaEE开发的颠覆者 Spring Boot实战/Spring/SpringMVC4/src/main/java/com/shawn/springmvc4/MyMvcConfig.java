package com.shawn.springmvc4;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.shawn.springmvc4.interceptor.DemoInterceptor;
import com.shawn.springmvc4.messageconverter.MyMessageConverter;

@Configuration
/* 开启SpringMVC的支持，默认的配置 */
@EnableWebMvc
/* 开启计划任务支持 */
@EnableScheduling
@ComponentScan("com.shawn.springmvc4")
/* 继承重写方法可以对SpringMVC进行配置 */
public class MyMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	/* 视图解析，用来映射路径 */
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/classes/views/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setViewClass(JstlView.class);
		return viewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/* JS文件资源映射，对外暴露的访问路径，指定文件放置的目录 */
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
	}

	@Bean
	/* 拦截器的Bean */
	public DemoInterceptor demoInterceptor() {
		return new DemoInterceptor();
	}

	@Override
	/* 重写方法，注册拦截器 */
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(demoInterceptor());
	}

	@Override
	/* 页面跳转配置 */
	public void addViewControllers(ViewControllerRegistry registry) {
		/* 初始页面 */
		registry.addViewController("/index").setViewName("/index");
		/* 上传文件页面 */
		registry.addViewController("/toUpload").setViewName("/upload");
		/* 转换页面 */
		registry.addViewController("/converter").setViewName("/converter");
		/* SSE页面 */
		registry.addViewController("/sse").setViewName("/sse");
		/* 异步页面 */
		registry.addViewController("/async").setViewName("/async");
	}

	@Override
	/* 设置不忽略点后面的参数 */
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}

	@Bean
	/* 上传文件的Bean */
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1000000);
		return multipartResolver;
	}

	@Override
	/* 不覆盖默认注册的HttpMessageConverter */
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(converter());
	}

	@Bean
	/* 转换器的Bean */
	public MyMessageConverter converter() {
		return new MyMessageConverter();
	}
}