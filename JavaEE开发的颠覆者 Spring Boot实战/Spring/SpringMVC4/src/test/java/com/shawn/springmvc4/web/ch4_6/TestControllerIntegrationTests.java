package com.shawn.springmvc4.web.ch4_6;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.shawn.springmvc4.MyMvcConfig;
import com.shawn.springmvc4.service.DemoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MyMvcConfig.class })
/* 指定Web资源的位置 */
@WebAppConfiguration("src/main/resources")
public class TestControllerIntegrationTests {
	/* 模拟的MVC对象 */
	private MockMvc mockMvc;

	@Autowired
	/* 测试用例中注入Spring的Bean */
	private DemoService demoService;

	@Autowired
	/* 注入WebApplicationContext */
	WebApplicationContext wac;

	@Autowired
	/* 模拟session，不使用 */
	MockHttpSession session;

	@Autowired
	/* 模拟request，不使用 */
	MockHttpServletRequest request;

	@Before
	/* 测试前初始化 */
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNormalController() throws Exception {
		/* 模拟向normal发送get请求 */
		/* 返回状态为200 */
		/* view名称为page */

		/* 预期真正的路径 */
		/* 预期demoService.saySomething()返回hello */
		mockMvc.perform(get("/normal")).andExpect(status().isOk()).andExpect(view().name("page"))
				.andExpect(forwardedUrl("/WEB-INF/classes/views/page.jsp"))
				.andExpect(model().attribute("msg", demoService.saySomething()));
	}

	@Test
	public void testRestController() throws Exception {
		/* 模拟向testRest发送get请求 */
		/* 返回媒体类型text/plain;charset=UTF-8 */
		/* 预期demoService.saySomething()返回hello */
		mockMvc.perform(get("/testRest")).andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andExpect(content().string(demoService.saySomething()));
	}
}