package com.shawn.http;

import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/*通过设置可以在application.properties中配置端点*/
@ConfigurationProperties(prefix = "endpoints.status", ignoreUnknownFields = false)
/* 继承AbstractEndpoint类，重写invoke方法，实现ApplicationContextAware接口为了访问容器资源 */
public class StatusEndPoint extends AbstractEndpoint<String> implements ApplicationContextAware {
	ApplicationContext context;

	public StatusEndPoint() {
		super("status");
	}

	@Override
	/* 返回监控内容 */
	public String invoke() {
		StatusService statusService = context.getBean(StatusService.class);
		return "The Current Status is :" + statusService.getStatus();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}