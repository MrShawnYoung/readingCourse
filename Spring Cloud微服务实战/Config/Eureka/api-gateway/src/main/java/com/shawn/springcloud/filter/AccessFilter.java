package com.shawn.springcloud.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class AccessFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

	/* 具体逻辑 */
	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		log.info("send {} request to {} ", request.getMethod(), request.getRequestURL().toString());
		Object accessToken = request.getParameter("accessToken");
		if (accessToken == null) {
			log.warn("access token is empty");
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(401);
			return null;
		}
		log.info("access token ok");
		return null;
	}

	/* 是否需要被执行 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/* 过滤的执行顺序 */
	@Override
	public int filterOrder() {
		return 0;
	}

	/* 过滤的类型，在请求的哪个生命周期中执行,pre表示之前 */
	@Override
	public String filterType() {
		return "pre";
	}
}