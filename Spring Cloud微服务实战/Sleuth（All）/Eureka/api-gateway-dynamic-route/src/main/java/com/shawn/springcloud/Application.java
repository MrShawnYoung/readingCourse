package com.shawn.springcloud;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringCloudApplication
public class Application {
	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(Application.class).web(true).run(args);
	}

	@Bean
	/* Zuul配置内容动态化 */
	@RefreshScope
	@ConfigurationProperties("zuul")
	public ZuulProperties zuulPreperies() {
		return new ZuulProperties();
	}
}