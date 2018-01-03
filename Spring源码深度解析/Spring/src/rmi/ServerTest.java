package rmi;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerTest {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("RMIServer.xml");
	}
}