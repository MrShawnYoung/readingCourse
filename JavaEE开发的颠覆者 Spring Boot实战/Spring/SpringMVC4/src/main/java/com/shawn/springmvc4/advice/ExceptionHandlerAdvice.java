package com.shawn.springmvc4.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/*控制器建言，自动注册为Spring的Bean*/
@ControllerAdvice
public class ExceptionHandlerAdvice {

	/* 定义全局处理，设置拦截所有的Exception */
	@ExceptionHandler(value = Exception.class)
	public ModelAndView exception(Exception exception, WebRequest request) {
		/* error页面 */
		ModelAndView modelAndView = new ModelAndView("error");
		modelAndView.addObject("errorMessage", exception.getMessage());
		return modelAndView;
	}

	/* 添加键值对到全局，所有@RequestMapping的方法可获得此键值对 */
	@ModelAttribute
	public void addAttributes(Model model) {
		model.addAttribute("msg", "额外信息");
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		/* 忽略request的参数id */
		webDataBinder.setDisallowedFields("id");
	}
}