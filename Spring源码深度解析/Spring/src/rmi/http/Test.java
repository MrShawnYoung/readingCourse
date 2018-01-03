package rmi.http;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"client.xml");
		HttpInvokerTestI httpInvokerTestI = (HttpInvokerTestI) context
				.getBean("remoteService");
		System.out.println(httpInvokerTestI.getTestPo("dddd"));
	}
}