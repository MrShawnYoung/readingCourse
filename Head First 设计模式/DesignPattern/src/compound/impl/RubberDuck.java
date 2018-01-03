package compound.impl;

import compound.Quackable;
import compound.observer.Observer;
import compound.observer.impl.Observable;

/**
 * 橡皮鸭
 * 
 * @author 杨弢
 * 
 */
public class RubberDuck implements Quackable {
	Observable observable;

	public RubberDuck() {
		observable = new Observable(this);
	}

	@Override
	public void quack() {
		System.out.println("Honk");
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
		return "RubberDuck";
	}
}