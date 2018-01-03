package jms.spring;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0;
		try {
			System.out.println(msg.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}