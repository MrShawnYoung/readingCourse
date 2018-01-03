package compound.impl;

import compound.Quackable;
import compound.observer.Observer;
import compound.observer.impl.Observable;

/**
 * 鸭鸣器
 * 
 * @author 杨弢
 * 
 */
public class DuckCall implements Quackable {
	Observable observable;

	public DuckCall() {
		observable = new Observable(this);
	}

	@Override
	public void quack() {
		System.out.println("Kwak");
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
		return "DuckCall";
	}
}