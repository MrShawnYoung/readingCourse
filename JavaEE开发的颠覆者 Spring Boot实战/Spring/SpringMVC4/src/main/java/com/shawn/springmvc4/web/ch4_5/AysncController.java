package com.shawn.springmvc4.web.ch4_5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.shawn.springmvc4.service.PushService;

@Controller
public class AysncController {
	@Autowired
	/* 定时任务，定时更新deferredResult */
	PushService pushService;

	@RequestMapping("/defer")
	@ResponseBody
	/* 返回给客户端 */
	public DeferredResult<String> deferredCall() {
		return pushService.getAsyncUpdate();
	}
}