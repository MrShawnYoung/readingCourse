package aop.JDKproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyInvocationHandler implements InvocationHandler {
	// 目标对象
	private Object obj;

	public MyInvocationHandler(Object obj) {
		this.obj = obj;
	}

	/**
	 * 执行目标对象的方法
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// 在目标对象之前打印
		System.out.println("----------before----------");
		// 执行目标对象方法
		Object result = method.invoke(obj, args);
		// 在目标对象之后打印
		System.out.println("----------after----------");
		return result;
	}

	/**
	 * 获取目标对象的代理对象
	 * 
	 * @return 代理对象
	 */
	public Object getProxy() {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), obj.getClass().getInterfaces(), this);
	}
}
