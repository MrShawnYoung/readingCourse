package com.shawn.websocket.domain;

/*向浏览器发送消息*/
public class ShawnResponse {
	private String responseMessage;

	public ShawnResponse(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}
}