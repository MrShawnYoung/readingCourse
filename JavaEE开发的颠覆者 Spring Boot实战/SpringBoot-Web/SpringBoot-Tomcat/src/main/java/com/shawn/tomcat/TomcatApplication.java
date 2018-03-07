package com.shawn.tomcat;

import java.util.concurrent.TimeUnit;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TomcatApplication {

	public static void main(String[] args) {
		SpringApplication.run(TomcatApplication.class, args);
	}

	// /* 通用配置(文件内) */
	// @Component
	// public static class CustomServletContainer implements
	// EmbeddedServletContainerCustomizer {
	// @Override
	// public void customize(ConfigurableEmbeddedServletContainer container) {
	// container.setPort(8888);
	// container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,
	// "/404.html"));
	// container.setSessionTimeout(10, TimeUnit.MINUTES);
	// }
	// }

	// /* 特定配置 */
	// @Bean
	// public EmbeddedServletContainerFactory servletContainer() {
	// /* 指定Tomcat配置 */
	// TomcatEmbeddedServletContainerFactory factory = new
	// TomcatEmbeddedServletContainerFactory();
	// /* 配置端口号 */
	// factory.setPort(8888);
	// /* 配置错误页面 */
	// factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
	// /* 用户会话过期时间 */
	// factory.setSessionTimeout(10, TimeUnit.MINUTES);
	// return factory;
	// }

	// /* http转https */
	// @Bean
	// public EmbeddedServletContainerFactory servletContainer() {
	// TomcatEmbeddedServletContainerFactory tomcat = new
	// TomcatEmbeddedServletContainerFactory() {
	// @Override
	// protected void postProcessContext(Context context) {
	// SecurityConstraint securityConstraint = new SecurityConstraint();
	// securityConstraint.setUserConstraint("CONFIDENTIAL");
	// SecurityCollection collection = new SecurityCollection();
	// collection.addPattern("/*");
	// securityConstraint.addCollection(collection);
	// context.addConstraint(securityConstraint);
	// }
	// };
	// tomcat.addAdditionalTomcatConnectors(httpConnector());
	// return tomcat;
	// }
	//
	// @Bean
	// public Connector httpConnector() {
	// Connector connector = new
	// Connector("org.apache.coyote.http11.Http11NioProtocol");
	// connector.setScheme("http");
	// connector.setPort(8080);
	// connector.setSecure(false);
	// connector.setRedirectPort(8443);
	// return connector;
	// }
}