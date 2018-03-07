package com.shawn.springmvc4.web.ch4_5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shawn.springmvc4.domain.DemoObj;

@Controller
public class ConverterController {
	/* 指定返回自定义媒体类型 */
	@RequestMapping(value = "/convert", produces = { "application/x-shawn" })
	public @ResponseBody DemoObj convert(@RequestBody DemoObj demoObj) {
		return demoObj;
	}
}