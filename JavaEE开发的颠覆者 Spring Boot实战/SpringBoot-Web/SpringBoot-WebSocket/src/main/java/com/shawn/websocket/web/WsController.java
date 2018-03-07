package com.shawn.websocket.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.shawn.websocket.domain.ShawnMessage;
import com.shawn.websocket.domain.ShawnResponse;

@Controller
public class WsController {
	@MessageMapping("/welcome") // 映射地址
	@SendTo("/topic/getResponse") // 发送消息地址
	public ShawnResponse say(ShawnMessage message) throws Exception {
		Thread.sleep(3000);
		return new ShawnResponse("Welcome, " + message.getName() + "!");
	}

	@Autowired
	private SimpMessagingTemplate messagingTemplate;// 给浏览器发送消息

	@MessageMapping("/chat")
	/* 当前用户信息 */
	public void handleChat(Principal principal, String msg) {
		if (principal.getName().equals("admin")) {
			/* 给用户发送消息 */
			messagingTemplate.convertAndSendToUser("yangtao", "/queue/notifications",
					principal.getName() + "-send:" + msg);
		} else {
			messagingTemplate.convertAndSendToUser("admin", "/queue/notifications",
					principal.getName() + "-send:" + msg);
		}
	}
}