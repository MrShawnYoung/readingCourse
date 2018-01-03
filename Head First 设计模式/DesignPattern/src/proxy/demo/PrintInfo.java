package proxy.demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class PrintInfo {

	public static void main(String[] args) {
		// 我们要代理的真实对象
		Subject realSubject = new RealSubject();

		// 我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
		InvocationHandler handler = new ObjectProxy(realSubject);

		Subject subject = (Subject) Proxy.newProxyInstance(realSubject
				.getClass().getClassLoader(), realSubject.getClass()
				.getInterfaces(), handler);

		System.out.println(subject.getClass().getName());
		subject.rent();
		subject.hello("world");
	}
}
