package compound.decorator;

import compound.Quackable;
import compound.observer.Observer;

/**
 * 计算鸭子叫数量
 * 
 * @author 杨弢
 * 
 */
public class QuackCounter implements Quackable {
	Quackable duck;
	static int count;

	public QuackCounter(Quackable duck) {
		this.duck = duck;
	}

	@Override
	public void quack() {
		duck.quack();
		count++;
	}

	public static int getQuacks() {
		return count;
	}

	@Override
	public void registerObserver(Observer observer) {
		duck.registerObserver(observer);
	}

	@Override
	public void notifyObservers() {
		duck.notifyObservers();
	}
}