package chapter9;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {

	interface IHello {
		void sayHello();
	}

	static class Hello implements IHello {

		@Override
		public void sayHello() {
			System.out.println("hello world");
		}
	}

	static class DynmicProxy implements InvocationHandler {
		Object origninalObj;

		Object bind(Object originalObj) {
			this.origninalObj = originalObj;
			return Proxy.newProxyInstance(originalObj.getClass().getClassLoader(),
					originalObj.getClass().getInterfaces(), this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("welcome");
			return method.invoke(origninalObj, args);
		}
	}

	public static void main(String[] args) {
		IHello hello = (IHello) new DynmicProxy().bind(new Hello());
		hello.sayHello();
	}
}