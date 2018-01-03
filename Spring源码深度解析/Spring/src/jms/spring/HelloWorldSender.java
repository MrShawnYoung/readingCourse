package jms.spring;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class HelloWorldSender {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"activeMQ.xml");
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
		Destination destination = (Destination) context.getBean("destination");
		jmsTemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("大家好这个是测试！");
			}
		});
	}
}