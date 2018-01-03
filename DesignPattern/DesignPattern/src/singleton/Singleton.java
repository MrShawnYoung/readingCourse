package singleton;

public class Singleton {
	/* 双重检查加锁 */
	private volatile static Singleton uniqueInstance;

	private Singleton() {

	}

	public static Singleton getInstance() {
		if (uniqueInstance == null) {
			synchronized (Singleton.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Singleton();
				}
			}
		}
		return uniqueInstance;
	}

	/*
	 * 急切实例化
	 * 
	 * private static Singleton uniqueInstance = new Singleton();
	 * 
	 * private Singleton() {
	 * 
	 * }
	 * 
	 * public static Singleton getInstance() { return uniqueInstance; }
	 */
}
