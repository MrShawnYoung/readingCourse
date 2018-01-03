package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.spring.ActiveMQConnectionFactory;

public class Receiver {

	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		connection.start();
		final Session session = connection.createSession(Boolean.TRUE,
				Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("my-queue");
		MessageConsumer consumer = session.createConsumer(destination);
		int i = 0;
		while (i < 3) {
			i++;
			TextMessage message = (TextMessage) consumer.receive();
			session.commit();
			// TODO something...
			System.out.println("收到消息：" + message.getText());
		}
		session.close();
		connection.close();
	}
}