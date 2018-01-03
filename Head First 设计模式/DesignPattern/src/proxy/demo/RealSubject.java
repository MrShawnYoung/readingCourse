package proxy.demo;

/**
 * 真实对象
 * 
 * @author 杨弢
 * 
 */
public class RealSubject implements Subject {

	@Override
	public void rent() {
		System.out.println("I want to rent my house");
	}

	@Override
	public void hello(String str) {
		System.out.println("hello: " + str);
	}
}