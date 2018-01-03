package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.spring.ActiveMQConnectionFactory;

public class Sender {

	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		// connection.start();
		Session session = connection.createSession(Boolean.TRUE,
				Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("my-queue");
		MessageProducer producer = session.createProducer(destination);
		for (int i = 0; i < 3; i++) {
			TextMessage message = session.createTextMessage("大家好这是个测试");
			Thread.sleep(1000);
			// 通过消息生产者发出消息
			producer.send(message);
		}
		session.commit();
		session.close();
		connection.close();
	}
}