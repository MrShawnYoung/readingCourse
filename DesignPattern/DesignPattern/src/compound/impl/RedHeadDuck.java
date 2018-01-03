package compound.impl;

import compound.Quackable;
import compound.observer.Observer;
import compound.observer.impl.Observable;

/**
 * 红头鸭
 * 
 * @author 杨弢
 * 
 */
public class RedHeadDuck implements Quackable {
	Observable observable;

	public RedHeadDuck() {
		observable = new Observable(this);
	}

	@Override
	public void quack() {
		System.out.println("Quack");
		notifyObservers();
	}

	@Override
	public void registerObserver(Observer observer) {
		observable.registerObserver(observer);
	}

	@Override
	public void notifyObservers() {
		observable.notifyObservers();
	}

	@Override
	public String toString() {
		return "RedHeadDuck";
	}
}