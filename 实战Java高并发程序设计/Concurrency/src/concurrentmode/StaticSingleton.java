package concurrentmode;

/**
 * 内部类单例模式
 * 
 * @author 杨弢
 * 
 */
public class StaticSingleton {
	private StaticSingleton() {
		System.out.println("StaticSingleton is create");
	}

	private static class SingletonHolder {
		private static StaticSingleton instance = new StaticSingleton();
	}

	public static StaticSingleton getInstance() {
		return SingletonHolder.instance;
	}
}