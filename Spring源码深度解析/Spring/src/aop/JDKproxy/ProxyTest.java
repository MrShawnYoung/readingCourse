package aop.JDKproxy;

public class ProxyTest {
	public static void main(String[] args) {
		// 实例化目标对象
		UserService userService = new UserServiceImpl();
		// 实例化InvocationHander
		MyInvocationHandler invocationHander = new MyInvocationHandler(
				userService);
		// 根据目标对象生成代理对象
		UserService proxy = (UserService) invocationHander.getProxy();

		// UserService proxy = (UserService)
		// Proxy.newProxyInstance(userService.getClass().getClassLoader(),
		// userService.getClass().getInterfaces(), invocationHander);

		// 调用方法
		proxy.add();
	}
}
